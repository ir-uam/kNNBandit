/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0.
 * 
 */
package es.uam.eps.ir.knnbandit.recommendation.knn.user;

import es.uam.eps.ir.knnbandit.data.preference.index.fast.FastUpdateableItemIndex;
import es.uam.eps.ir.knnbandit.data.preference.index.fast.FastUpdateableUserIndex;
import es.uam.eps.ir.knnbandit.recommendation.InteractiveRecommender;
import es.uam.eps.ir.knnbandit.recommendation.knn.similarities.UpdateableSimilarity;
import es.uam.eps.ir.ranksys.fast.preference.SimpleFastPreferenceData;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import org.jooq.lambda.tuple.Tuple3;
import org.ranksys.core.util.tuples.Tuple2id;

/**
 * Abstract version of an interactive user-based kNN algorithm
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public abstract class AbstractInteractiveUserBasedKNN<U,I> extends InteractiveRecommender<U,I>
{
    /**
     * Updateable similarity.
     */
    protected final UpdateableSimilarity sim;
    /**
     * Random number generator to untie neighbors.
     */
    private final Random neighborUntie = new Random();
    /**
     * Number of neighbors to use. 
     */
    private final int k;
    /**
     * Neighbor comparator.
     */
    private final Comparator<Tuple2id> comp;
    /**
     * Shuffled list of users.
     */
    private final IntList userList;
    
    private final boolean ignoreZeros;
    
    /**
     * Constructor.
     * @param uIndex User index.
     * @param iIndex Item index.
     * @param prefData Preference data.
     * @param ignoreUnknown True if we must ignore unknown items when updating.
     * @param ignoreZeros True if we ignore zero ratings when updating.
     * @param k Number of neighbors to use.
     * @param sim Updateable similarity
     */
    public AbstractInteractiveUserBasedKNN(FastUpdateableUserIndex<U> uIndex, FastUpdateableItemIndex<I> iIndex, SimpleFastPreferenceData<U, I> prefData, boolean ignoreUnknown, boolean ignoreZeros, int k, UpdateableSimilarity sim)
    {
        super(uIndex, iIndex, prefData, ignoreUnknown);
        this.sim = sim;
        this.k = (k > 0) ? k : prefData.numUsers();
        this.userList = new IntArrayList();
        uIndex.getAllUidx().forEach(uidx -> userList.add(uidx));
        this.comp = (Tuple2id x, Tuple2id y) -> 
        {
            int value = (int) Math.signum(x.v2 - y.v2);
            if(value == 0)
                return userList.indexOf(x.v1) - userList.indexOf(y.v1);
            return value;
        };
        this.ignoreZeros = ignoreZeros;
    }
    
    /**
     * Constructor.
     * @param uIndex User index.
     * @param iIndex Item index.
     * @param prefData Preference data.
     * @param ignoreUnknown True if we must ignore unknown items when updating.
     * @param ignoreZeros True if we ignore zero ratings when updating.
     * @param notReciprocal True if we do not recommend reciprocal social links, false otherwise.
     * @param k Number of neighbors to use.
     * @param sim Updateable similarity
     */
    public AbstractInteractiveUserBasedKNN(FastUpdateableUserIndex<U> uIndex, FastUpdateableItemIndex<I> iIndex, SimpleFastPreferenceData<U, I> prefData, boolean ignoreUnknown, boolean ignoreZeros, boolean notReciprocal, int k, UpdateableSimilarity sim)
    {
        super(uIndex, iIndex, prefData, ignoreUnknown, notReciprocal);
        this.sim = sim;
        this.k = (k > 0) ? k : prefData.numUsers();
        this.userList = new IntArrayList();
        uIndex.getAllUidx().forEach(uidx -> userList.add(uidx));
        this.comp = (Tuple2id x, Tuple2id y) -> 
        {
            int value = (int) Math.signum(x.v2 - y.v2);
            if(value == 0)
                return userList.indexOf(x.v1) - userList.indexOf(y.v1);
            return value;
        };      
        this.ignoreZeros = ignoreZeros;
    }
    
    @Override
    public int next(int uidx)
    {
        IntList list = this.availability.get(uidx);
        if(list == null || list.isEmpty()) return -1;
        
        // Shuffle the order of users.
        Collections.shuffle(userList, neighborUntie);
        
        // Obtain the top-k best neighbors for user uidx.
        PriorityQueue<Tuple2id> neighborHeap = new PriorityQueue<>(k, comp);
        this.sim.similarElems(uidx).forEach(vidx -> 
        {
            double s = vidx.v2;
            if(neighborHeap.size() < k) neighborHeap.add(new Tuple2id(vidx.v1,vidx.v2));
            else if(neighborHeap.peek().v2 <= s)
            {
                neighborHeap.poll();
                neighborHeap.add(new Tuple2id(vidx.v1, s));
            }
        });
        
        if(neighborHeap.isEmpty())
        {
            return list.get(rng.nextInt(list.size()));
        }

        Int2DoubleOpenHashMap itemScores = new Int2DoubleOpenHashMap();
        itemScores.defaultReturnValue(0.0);
                
        // Then, generate scores for the different items.
        while(!neighborHeap.isEmpty())
        {
            Tuple2id neigh = neighborHeap.poll();
            
            this.trainData.getUidxPreferences(neigh.v1).forEach(vs -> 
            {
                double p = neigh.v2*this.score(neigh.v1, vs.v2);
                if(!ignoreZeros || p > 0)
                {
                    itemScores.addTo(vs.v1, p);
                }
            });
        }
        
        // Select the best item.
        double max = Double.NEGATIVE_INFINITY;
        IntList top = new IntArrayList();
        
        for(int iidx : itemScores.keySet())
        {
            double val = itemScores.get(iidx);
            if(!list.contains(iidx)) continue;

            if(top.isEmpty() || val > max)
            {
                top = new IntArrayList();
                max = val;
                top.add(iidx);
            }
            else if(val == max)
            {
                top.add(iidx);
            }
        }
        
        int topSize = top.size();
        if(top.isEmpty()) return list.get(rng.nextInt(list.size()));
        else if(topSize == 1) return top.get(0);
        return top.get(rng.nextInt(topSize));
    }

    /**
     * Scoring function.
     * @param vidx Identifier of the neighbor user.
     * @param rating The rating value.
     * @return 
     */
    protected abstract double score(int vidx, double rating);
    
    @Override
    public void updateMethod(List<Tuple3<Integer,Integer,Double>> train)
    {
        this.sim.update(this.trainData);
    }
}
