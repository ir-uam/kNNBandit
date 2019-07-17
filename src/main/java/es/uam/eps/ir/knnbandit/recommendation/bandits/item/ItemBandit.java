/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0.
 * 
 */
package es.uam.eps.ir.knnbandit.recommendation.bandits.item;

import es.uam.eps.ir.knnbandit.UntieRandomNumber;
import es.uam.eps.ir.knnbandit.recommendation.bandits.functions.ValueFunction;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Random;

/**
 * Bandit in which arms are items.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <U> User type.
 * @param <I> Item type.
 */
public abstract class ItemBandit<U,I>
{       
    /**
     * Untie random.
     */
    protected final Random untierng;
    /**
     * Constructor.
     */
    public ItemBandit()
    {
        this.untierng = new Random(UntieRandomNumber.RNG);
    }
    
    /**
     * Selects the next item, assuming a selection of them is available.
     * @param uidx Identifier of the user that selects the item.
     * @param available The selection of available items.
     * @param valF A function that determines the effective value of the arm, given a context.
     * @return the next selected item.
     */
    public abstract int next(int uidx, int[] available, ValueFunction valF);
    
    /**
     * Selects the next item, given that a selection of them is available.
     * @param uidx Identifier of the user that selects the item.
     * @param available The selection of available items.
     * @param valF A function that determines the effective value of the arm, given a context.
     * @return the next selected item.
     */
    public abstract int next(int uidx, IntList available, ValueFunction valF);
    
    /**
     * Updates the corresponding item, given the reward.
     * @param iidx The item to update.
     * @param value The reward.
     */
    public abstract void update(int iidx, double value);
}
