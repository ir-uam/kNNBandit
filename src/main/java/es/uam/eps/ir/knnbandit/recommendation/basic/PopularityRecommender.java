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
import es.uam.eps.ir.knnbandit.data.preference.index.fast.FastUpdateableItemIndex;
import es.uam.eps.ir.knnbandit.data.preference.index.fast.FastUpdateableUserIndex;
import es.uam.eps.ir.ranksys.fast.preference.SimpleFastPreferenceData;
import org.jooq.lambda.tuple.Tuple3;

/**
 * Interactive version of a popularity-based algorithm.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <U> User type.
 * @param <I> Item type.
 */
public class PopularityRecommender<U,I> extends AbstractBasicInteractiveRecommender<U,I>
{
    /**
     * Relevance threshold. 
     */
    public final double threshold;
    
    /**
     * Constructor.
     * @param uIndex User index.
     * @param iIndex Item index.
     * @param prefData Preference data.
     * @param ignoreUnknown True if we must ignore unknown items when updating.
     * @param threshold Relevance threshold
     */
    public PopularityRecommender(FastUpdateableUserIndex<U> uIndex, FastUpdateableItemIndex<I> iIndex, SimpleFastPreferenceData<U,I> prefData, boolean ignoreUnknown, double threshold)
    {
        super(uIndex, iIndex, prefData,ignoreUnknown);
        this.threshold = threshold;
    }
    
    /**
     * Constructor.
     * @param uIndex User index.
     * @param iIndex Item index.
     * @param prefData Preference data.
     * @param ignoreUnknown True if we must ignore unknown items when updating.
     * @param threshold Relevance threshold
     * @param notReciprocal True if we do not recommend reciprocal social links, false otherwise
     */
    public PopularityRecommender(FastUpdateableUserIndex<U> uIndex, FastUpdateableItemIndex<I> iIndex, SimpleFastPreferenceData<U,I> prefData, boolean ignoreUnknown, double threshold, boolean notReciprocal)
    {
        super(uIndex, iIndex, prefData,ignoreUnknown, notReciprocal);
        this.threshold = threshold;
    }
    
    @Override
    public void updateMethod(int uidx, int iidx, double value)
    {
        this.values[iidx] += (value >= threshold ? 1.0 : 0.0);
    }
    
    @Override
    public void updateMethod(List<Tuple3<Integer,Integer,Double>> train)
    {
        for(int iidx = 0; iidx < this.prefData.numItems(); ++iidx)
        this.values[iidx] = this.trainData.getIidxPreferences(iidx).filter(vidx -> vidx.v2 > 0).count();
    }

}
