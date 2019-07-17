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

import es.uam.eps.ir.knnbandit.UntieRandomNumber;
import es.uam.eps.ir.knnbandit.data.preference.index.fast.FastUpdateableItemIndex;
import es.uam.eps.ir.knnbandit.data.preference.index.fast.FastUpdateableUserIndex;
import es.uam.eps.ir.knnbandit.recommendation.InteractiveRecommender;
import es.uam.eps.ir.ranksys.fast.preference.SimpleFastPreferenceData;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Random;


/**
 * Interactive version of a random algorithm.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <U> User type.
 * @param <I> Item type.
 */
public class RandomRecommender<U,I> extends InteractiveRecommender<U,I>
{
    /**
     * Random number generator.
     */
    private final Random rng = new Random(UntieRandomNumber.RNG);

    /**
     * Constructor.
     * @param uIndex user index.
     * @param iIndex item index.
     * @param prefData preference data.
     * @param ignoreUnknown true if we want to ignore missing ratings at updating, false if we want to treat them as failures.
     */
    public RandomRecommender(FastUpdateableUserIndex<U> uIndex, FastUpdateableItemIndex<I> iIndex, SimpleFastPreferenceData<U,I> prefData, boolean ignoreUnknown)
    {
        super(uIndex, iIndex, prefData, ignoreUnknown);
    }
    
    /**
     * Constructor.
     * @param uIndex user index.
     * @param iIndex item index.
     * @param prefData preference data.
     * @param ignoreUnknown true if we want to ignore missing ratings at updating, false if we want to treat them as failures.
     * @param notReciprocal true if we do not recommend reciprocal social links, false otherwise
     */
    public RandomRecommender(FastUpdateableUserIndex<U> uIndex, FastUpdateableItemIndex<I> iIndex, SimpleFastPreferenceData<U,I> prefData, boolean ignoreUnknown, boolean notReciprocal)
    {
        super(uIndex, iIndex, prefData, ignoreUnknown, notReciprocal);
    }
    
    @Override
    public int next(int uidx)
    {
        IntList list = this.availability.get(uidx);
        if(list == null || list.isEmpty()) return -1;
        else
        {
            return list.get(rng.nextInt(list.size()));
        }
    }

    @Override
    public void updateMethod(int uidx, int iidx, double value)
    {
        
    }

}
