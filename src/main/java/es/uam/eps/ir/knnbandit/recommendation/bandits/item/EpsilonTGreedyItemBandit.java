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
import java.util.Random;

/**
 * Variable Epsilon-Greedy item bandit.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <U> User type.
 * @param <I> Item type.
 */
public class EpsilonTGreedyItemBandit<U,I> extends ItemBandit<U,I>
{
    /**
     * Slope parameter.
     */
    private final double alpha;
    /**
     * Values of each arm.
     */
    double[] values;
    /**
     * Number of times an arm has been selected.
     */
    double[] numTimes;
    /**
     * The sum of the values.
     */
    double sumValues;
    /**
     * The number of items.
     */
    private final int numItems;
    /**
     * Number of iterations.
     */
    private int numIter;
    /**
     * Random number generator.
     */
    private final Random rng = new Random();
    /**
     * Epsilon greedy update function.
     */
    private final EpsilonGreedyUpdateFunction updateFunction;
    
    /**
     * Constructor.
     * @param alpha 
     * @param numItems 
     * @param updateFunction
     */
    public EpsilonTGreedyItemBandit(double alpha, int numItems, EpsilonGreedyUpdateFunction updateFunction)
    {
        super();
        this.alpha = alpha;
        this.numItems = numItems;
        this.sumValues = 0.0;
        this.values = new double[numItems];
        this.numTimes = new double[numItems];
        this.updateFunction = updateFunction;
        this.numIter = 1;
    }
    
    @Override
    public int next(int uidx, int[] available, ValueFunction valF)
    {
        if(available == null || available.length == 0)
            return -1;
        if(available.length == 1)
        {
            return available[0];
        }
        else
        {
            double epsilon = Math.min(1.0, this.alpha*numItems/(numIter+0.0));
            if(rng.nextDouble() < epsilon)
            {
                int item = untierng.nextInt(available.length);
                return available[item];
            }
            else
            {
                double max = Double.NEGATIVE_INFINITY;
                IntList top = new IntArrayList();
                
                for(int i : available)
                {
                    double val = valF.apply(uidx, i, values[i], numTimes[i]);
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
                int iidx;
                if(size == 1)
                {
                    iidx = top.get(0);
                }
                else
                {
                    iidx = top.get(untierng.nextInt(size));
                }
                
                return iidx;
            }
        }
    }
    
    @Override
    public int next(int uidx, IntList available, ValueFunction valF)
    {
        if(available == null || available.isEmpty())
            return -1;
        if(available.size() == 1)
        {
            return available.get(0);
        }
        else
        {
            double epsilon = Math.min(1.0, this.alpha*numItems/(numIter+0.0));
            if(rng.nextDouble() < epsilon)
            {
                int item = untierng.nextInt(available.size());
                return available.get(item);
            }
            else
            {
                double max = Double.NEGATIVE_INFINITY;
                IntList top = new IntArrayList();
                
                for(int i : available)
                {
                    double val = valF.apply(uidx, i, values[i], numTimes[i]);
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
                int iidx;
                if(size == 1)
                {
                    iidx = top.get(0);
                }
                else
                {
                    iidx = top.get(untierng.nextInt(size));
                }
                
                return iidx;
            }
        }
    }

    @Override
    public void update(int i, double value)
    {
        double oldSum = this.sumValues;
        double increment = value;
        double nTimes = this.numTimes[i]+1;
        double oldVal = this.values[i];
     
        numTimes[i]++;
        numIter++;
        double newVal = this.updateFunction.apply(oldVal, value, oldSum, increment, nTimes);
        this.values[i] = newVal;
        this.sumValues += (newVal - oldVal);
    }
}
