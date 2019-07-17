/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0.
 * 
 */
package es.uam.eps.ir.knnbandit.recommendation.bandits;

import es.uam.eps.ir.knnbandit.data.preference.index.fast.FastUpdateableItemIndex;
import es.uam.eps.ir.knnbandit.data.preference.index.fast.FastUpdateableUserIndex;
import es.uam.eps.ir.knnbandit.recommendation.InteractiveRecommender;
import es.uam.eps.ir.knnbandit.recommendation.bandits.functions.ValueFunction;
import es.uam.eps.ir.knnbandit.recommendation.bandits.item.ItemBandit;
import es.uam.eps.ir.ranksys.fast.preference.SimpleFastPreferenceData;

/**
 * Simple non-personalized item-based multi-armed bandit recommender.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <U> User type.
 * @param <I> Item type.
 */
public class ItemBanditRecommender<U,I> extends InteractiveRecommender<U,I>
{
    /**
     * Implementation of an item bandit.
     */
    private final ItemBandit<U,I> itemBandit;
    /**
     * Function for evaluating the value.
     */
    private final ValueFunction valFunc;
    
    /**
     * Constructor.
     * @param uIndex User index
     * @param iIndex Item index.
     * @param prefData Preference data.
     * @param ignoreUnknown True if we want to ignore missing ratings when updating, false if we want to count them as failures.
     * @param itemBandit An item bandit.
     * @param valFunc A value function of the reward.
     */
    public ItemBanditRecommender(FastUpdateableUserIndex<U> uIndex, FastUpdateableItemIndex<I> iIndex, SimpleFastPreferenceData<U, I> prefData, boolean ignoreUnknown, ItemBandit<U,I> itemBandit, ValueFunction valFunc)
    {
        super(uIndex, iIndex, prefData, ignoreUnknown);
        this.itemBandit = itemBandit;
        this.valFunc = valFunc;
    }
    
    /**
     * Constructor.
     * @param uIndex User index
     * @param iIndex Item index.
     * @param prefData Preference data.
     * @param ignoreUnknown True if we want to ignore missing ratings when updating, false if we want to count them as failures.
     * @param itemBandit An item bandit.
     * @param valFunc A value function of the reward.
     */
    public ItemBanditRecommender(FastUpdateableUserIndex<U> uIndex, FastUpdateableItemIndex<I> iIndex, SimpleFastPreferenceData<U, I> prefData, boolean ignoreUnknown, boolean notReciprocal, ItemBandit<U,I> itemBandit, ValueFunction valFunc)
    {
        super(uIndex, iIndex, prefData, ignoreUnknown, notReciprocal);
        this.itemBandit = itemBandit;
        this.valFunc = valFunc;
    }
    
    @Override
    public int next(int uidx)
    {
        int iidx = this.itemBandit.next(uidx, availability.get(uidx).toIntArray(), valFunc);
        return iidx;
    }

    @Override
    public void updateMethod(int uidx, int iidx, double value)
    {
        this.itemBandit.update(iidx, value);
    }
}
