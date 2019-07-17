/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0.
 * 
 */
package es.uam.eps.ir.knnbandit.metrics;

import it.unimi.dsi.fastutil.ints.Int2LongMap;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import java.util.stream.IntStream;

/**
 * Cumulative version of the Gini index.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <U> User type.
 * @param <I> Item type.
 */
public class CumulativeGini<U,I> implements CumulativeMetric<U,I>
{
    /**
     * For each item in the collection, stores the number of times it has been recommended.
     */
    private final Int2LongMap frequencies;
    /**
     * The minimum indexes for the different possible frequency values.
     */
    private final Long2IntMap mins;
    /**
     * The maximum indexes for the different possible frequencies values.
     */
    private final Long2IntMap maxs;
    /**
     * The sum of the frequencies of all items.
     */
    private double freqSum;
    /**
     * The total number of items.
     */
    private final int numItems;
    /**
     * The main term of the Gini index.
     */
    private double numSum;
    
    /**
     * Constructor.
     * @param numItems The number of items.
     */
    public CumulativeGini(int numItems)
    {
        this.numItems = numItems;
        this.freqSum = 0.0;
        this.numSum  = 0.0;

        this.mins = new Long2IntOpenHashMap();
        this.maxs = new Long2IntOpenHashMap();
        
        // To start, initialize the minimums and the maximums. Only the zero has appeared.
        this.mins.put(0L, 1);
        this.maxs.put(0L, numItems);
        
        this.frequencies = new Int2LongOpenHashMap();
        IntStream.range(0, numItems).forEach(iidx -> frequencies.put(iidx, 0L));
    }
    
    @Override
    public double compute()
    {
        if(numItems <= 1) return Double.NaN;
        else if(freqSum == 0) return Double.NaN;
        else return numSum/((numItems - 1.0)*freqSum);
    }

    @Override
    public void update(int uidx, int iidx)
    {
        this.freqSum += 1.0;
        // Update the value of numSum.
        // First, get the frequency of item iidx.
        long freq = this.frequencies.get(iidx);
        this.frequencies.put(iidx, freq + 1);
        
        // Obtain the minimum and maximum indexes for the old value
        int minFreq = this.mins.get(freq);
        int maxFreq = this.maxs.get(freq);
        // Obtain the minimum index for the new value.
        int minNewFreq = this.mins.getOrDefault(freq + 1L, maxFreq);
        
        // Compute the increment.
        double increment = (numItems + 1 - 2*maxFreq)*freq;
        if(minNewFreq == maxFreq) increment += (2*maxFreq - numItems - 1)*(freq+1);
        else increment += (2*minNewFreq - numItems - 3)*(freq+1);
        
        // Update the minimum and maximum indexes.
        this.numSum += increment;
        if(minFreq == maxFreq)
        {
            this.mins.remove(freq);
            this.maxs.remove(freq);
        }
        else
        {
            this.maxs.put(freq, maxFreq-1);
        }
        
        if(minNewFreq == maxFreq)
        {
            this.mins.put(freq + 1L, maxFreq);
            this.maxs.put(freq + 1L, maxFreq);
        }
        else
        {
            this.mins.put(freq + 1L, minNewFreq - 1);
        }
    }

    @Override
    public void reset()
    {
        this.mins.clear();
        this.maxs.clear();
        this.frequencies.clear();
        
        this.mins.put(0L, 1);
        this.maxs.put(0L, numItems);       
        IntStream.range(0, numItems).forEach(iidx -> frequencies.put(iidx, 0L));
        this.freqSum = 0.0;
        this.numSum = 0.0;
    }
}
