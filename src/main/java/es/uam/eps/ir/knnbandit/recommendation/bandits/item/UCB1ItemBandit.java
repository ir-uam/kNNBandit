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

import es.uam.eps.ir.knnbandit.recommendation.bandits.functions.ValueFunction;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

/**
 * Item bandit using the UCB1 algorithm.
 * @author Javier Sanz-Cruzado Puig (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <U> User type.
 * @param <I> Item type.
 */
public class UCB1ItemBandit<U,I> extends ItemBandit<U,I>
{
    /**
     * The values for each user.
     */
    double[] values;
    /**
     * The number of times each item has been selected.
     */
    double[] numTimes;
    /**
     * The number of iterations.
     */
    int numIter;
    /**
     * The number of items.
     */
    int numItems;
    
    /**
     * Constructor.
     * @param numItems The number of items.
     */
    public UCB1ItemBandit(int numItems)
    {
        this.numItems = numItems;
        this.values = new double[numItems];
        this.numTimes = new double[numItems];
    }
    
    @Override
    public int next(int uidx, int[] available, ValueFunction valF)
    {
        if(available == null || available.length == 0)
            return -1;
        else if(available.length == 1)
        {
            return available[0];
        }
        else
        {
            double max = Double.NEGATIVE_INFINITY;
            IntList top = new IntArrayList();
            for(int i : available)
            {
                double val;
                if(this.numTimes[i] == 0)
                {
                    val = Double.POSITIVE_INFINITY;
                }
                else
                {
                    val = valF.apply(uidx, i,values[i] + Math.sqrt(2*Math.log(numIter+1)/(numTimes[i])), numTimes[i]);
                }
                
                if(val > max)
                {
                    max = val;
                    top = new IntArrayList();
                    top.add(i);
                }
                else if(val == max)
                {
                    top.add(i);
                }
            }
            
            int size = top.size();
            int item;
            if(size == 1) item = top.get(0);
            else item = top.get(untierng.nextInt(size));

            return item;
        }
    }
    
    @Override
    public int next(int uidx, IntList available, ValueFunction valF)
    {
        if(available == null || available.isEmpty())
            return -1;
        else if(available.size() == 1)
        {
            return available.get(0);
        }
        else
        {
            double max = Double.NEGATIVE_INFINITY;
            IntList top = new IntArrayList();
            for(int i : available)
            {
                double val;
                if(this.numTimes[i] == 0)
                {
                    val = Double.POSITIVE_INFINITY;
                }
                else
                {
                    val = valF.apply(uidx, i,values[i] + Math.sqrt(2*Math.log(numIter+1)/(numTimes[i])), numTimes[i]);
                }
                
                if(val > max)
                {
                    max = val;
                    top = new IntArrayList();
                    top.add(i);
                }
                else if(val == max)
                {
                    top.add(i);
                }
            }
            
            int size = top.size();
            int item;
            if(size == 1) item = top.get(0);
            else item = top.get(untierng.nextInt(size));
            

            return item;
        }
    }

    @Override
    public void update(int i, double value)
    {
        numTimes[i]++;
        numIter++;
        values[i] = values[i] + 1.0/(numTimes[i] + 0.0)*(value - values[i]);
    }
    
}
