/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0.
 * 
 */
package es.uam.eps.ir.knnbandit.recommendation.basic;

import java.util.List;
import java.util.stream.IntStream;
import es.uam.eps.ir.knnbandit.data.preference.index.fast.FastUpdateableItemIndex;
import es.uam.eps.ir.knnbandit.data.preference.index.fast.FastUpdateableUserIndex;
import es.uam.eps.ir.ranksys.fast.preference.SimpleFastPreferenceData;
import org.jooq.lambda.tuple.Tuple3;

/**
 * Interactive version of an average rating recommendation algorithm.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <U> User type.
 * @param <I> Item type.
 */
public class AvgRecommender<U,I> extends AbstractBasicInteractiveRecommender<U,I>
{  
    /**
     * Number of times an arm has been selected.
     */
    private double[] numTimes;
   
    /**
     * Constructor.
     * @param uIndex User index.
     * @param iIndex Item index.
     * @param prefData Preference data.
     * @param ignoreUnknown True if (user, item) pairs without training must be ignored.
     */
    public AvgRecommender(FastUpdateableUserIndex<U> uIndex, FastUpdateableItemIndex<I> iIndex, SimpleFastPreferenceData<U,I> prefData, boolean ignoreUnknown)
    {
        super(uIndex, iIndex, prefData, ignoreUnknown);
        this.numTimes = new double[prefData.numItems()];
        IntStream.range(0, prefData.numItems()).forEach(iidx -> this.numTimes[iidx] =0);
    }
    
    /**
     * Constructor.
     * @param uIndex User index.
     * @param iIndex Item index.
     * @param prefData Preference data.
     * @param ignoreUnknown True if (user, item) pairs without training must be ignored.
     * @param notReciprocal True if we do not recommend reciprocal social links, false otherwise
     */
    public AvgRecommender(FastUpdateableUserIndex<U> uIndex, FastUpdateableItemIndex<I> iIndex, SimpleFastPreferenceData<U,I> prefData, boolean ignoreUnknown, boolean notReciprocal)
    {
        super(uIndex, iIndex, prefData, ignoreUnknown, notReciprocal);
        this.numTimes = new double[prefData.numItems()];
        IntStream.range(0, prefData.numItems()).forEach(iidx -> this.numTimes[iidx] =0);
    }
    
    @Override
    public void updateMethod(int uidx, int iidx, double value)
    {
        double oldValue = values[iidx];
        if(numTimes[iidx] <= 0.0)
            this.values[iidx] = value;
        else
            this.values[iidx] = oldValue + (value-oldValue)/(numTimes[iidx]+1.0);
        this.numTimes[iidx]++;
    }
    
    @Override
    public void updateMethod(List<Tuple3<Integer,Integer,Double>> train)
    {
        for(int i = 0; i < this.prefData.numItems();++i)
        {
            this.values[i] = this.prefData.getIidxPreferences(i).mapToDouble(v -> v.v2).sum();
            this.numTimes[i] = this.prefData.numUsers(i);
            if(this.numTimes[i] > 0) this.values[i]/=(this.numTimes[i] + 0.0);
        }
    }
}
