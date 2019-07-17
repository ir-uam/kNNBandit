/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0.
 * 
 */
package es.uam.eps.ir.knnbandit.recommendation.knn.similarities;

import java.util.function.IntToDoubleFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import org.ranksys.core.util.tuples.Tuple2id;

/**
 * Vector cosine similarity.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class VectorCosineSimilarity implements UpdateableSimilarity
{
    /**
     * The scalar product.
     */
    private final double[][] num;
    /**
     * The norms of each user.
     */
    private final double[] norm;
    /**
     * The number of users.
     */
    private final int numUsers;
    private int lastUser;
    private int lastItem;
    
    public VectorCosineSimilarity(int numUsers)
    {
        this.numUsers = numUsers;
        this.num = new double[numUsers][numUsers];
        this.norm = new double[numUsers];
        this.lastUser = -1;
        this.lastItem = -1;
    }
    
    @Override
    public void update(int uidx, int vidx, int iidx, double uval, double vval)
    {
        if(vval != Double.NaN)
        {
            this.num[uidx][vidx] += uval*vval;
            this.num[vidx][uidx] += uval*vval;
        }
        
        if(lastUser != uidx || lastItem != iidx)
        {
            norm[uidx] += uval*uval;
            lastUser = uidx;
            lastItem = iidx;
        }
    }

    @Override
    public IntToDoubleFunction similarity(int idx)
    {
        return (int idx2) ->
        {
            double sum = Math.sqrt(this.norm[idx])*Math.sqrt(this.norm[idx2]);
            if(sum == 0) return 0.0;
            else return this.num[idx][idx2]/sum;
        };
    }

    @Override
    public Stream<Tuple2id> similarElems(int idx)
    {
        return IntStream.range(0, this.numUsers).filter(i -> i != idx).mapToObj(i -> new Tuple2id(i, similarity(idx, i))).filter(x -> x.v2 > 0.0);
    }
    
    @Override
    public void update(FastPreferenceData<?,?> prefData)
    {
        prefData.getAllUidx().forEach(uidx -> 
        {
            prefData.getAllUidx().forEach(vidx -> 
            {
                this.num[uidx][vidx] = 0.0;
            });
        });

        prefData.getAllUidx().forEach(uidx -> 
        {
            this.norm[uidx] = prefData.getUidxPreferences(uidx).mapToDouble(iidx -> 
            {
                prefData.getIidxPreferences(iidx.v1).forEach(vidx -> 
                {
                    this.num[uidx][vidx.v1] += iidx.v2*vidx.v2;
                });
                return iidx.v2*iidx.v2;
            }).sum();
        });
    }
}
