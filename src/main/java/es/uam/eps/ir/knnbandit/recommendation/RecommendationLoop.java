/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0.
 * 
 */
package es.uam.eps.ir.knnbandit.recommendation;

import es.uam.eps.ir.knnbandit.metrics.CumulativeMetric;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.jooq.lambda.tuple.Tuple2;

import java.util.*;

/**
 * Class for simulating the recommendation loop.
 * @param <U> User type.
 * @param <I> Item type.
 *
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class RecommendationLoop<U,I>
{
    /**
     * User index.
     */
    private final FastUserIndex<U> userIndex;
    /**
     * Item index.
     */
    private final FastItemIndex<I> itemIndex;
    /**
     * The recommendation algorithm.
     */
    private final InteractiveRecommender<U,I> recommender;
    /**
     * The metrics we want to find.
     */
    private final Map<String, CumulativeMetric<U,I>> metrics;
    /**
     * Random number generator.
     */
    private Random rng;
    /**
     * The random seed for the random number generator.
     */
    private final int rngSeed;
    /**
     * The number of users with recommendations.
     */
    private int numUsers;
    /**
     * The current iteration number.
     */
    private int iteration;
    /**
     * Total number of iterations.
     */
    private final int nIter;
    /**
     * Preference data.
     */
    private final FastPreferenceData<U,I> prefData;
    /**
     * List of users to be recommended
     */
    private final IntList userList;

    /**
     * Constructor. Uses 0 as the default random seed.
     * @param userIndex Index containing the users.
     * @param itemIndex Index containing the items.
     * @param recommender The interactive recommendation algorithm.
     * @param metrics The map of metrics.
     * @param nIter Total number of iterations. 0 for iterating until no more recommendations can be done.
     */
    public RecommendationLoop(FastUserIndex<U> userIndex, FastItemIndex<I> itemIndex,  FastPreferenceData<U,I> prefData, InteractiveRecommender<U,I> recommender, Map<String, CumulativeMetric<U,I>> metrics, int nIter)
    {
        this.userIndex = userIndex;
        this.itemIndex = itemIndex;
        this.prefData = prefData;

        this.recommender = recommender;
        this.metrics = metrics;

        this.userList = new IntArrayList();
        this.prefData.getUidxWithPreferences().forEach(userList::add);
        this.numUsers = userList.size();

        this.rngSeed = 0;
        this.nIter = nIter;
        rng = new Random(rngSeed);
        this.iteration = 0;
    }

    /**
     * Constructor.
     * @param userIndex Index containing the users.
     * @param itemIndex Index containing the items.
     * @param recommender The interactive recommendation algorithm.
     * @param metrics The map of metrics.
     * @param nIter Total number of iterations. 0 for iterating until no more recommendations can be done.
     * @param rngSeed seed for a random number generator.
     */
    public RecommendationLoop(FastUserIndex<U> userIndex, FastItemIndex<I> itemIndex, FastPreferenceData<U,I> prefData, InteractiveRecommender<U,I> recommender, Map<String, CumulativeMetric<U,I>> metrics, int nIter, int rngSeed)
    {
        this.userIndex = userIndex;
        this.itemIndex = itemIndex;
        this.prefData = prefData;

        this.recommender = recommender;
        this.metrics = metrics;

        this.userList = new IntArrayList();
        this.prefData.getUidxWithPreferences().forEach(userList::add);
        this.numUsers = userList.size();

        this.rngSeed = rngSeed;
        rng = new Random(rngSeed);
        this.nIter = nIter;
        this.iteration = 0;
    }

    /**
     * Checks if the loop has ended or not.
     * @return true if the loop has ended, false otherwise.
     */
    public boolean hasEnded()
    {
        if(numUsers == 0) return true;
        return nIter > 0 && this.iteration >= nIter;
    }

    /**
     * Recovers previous iterations from a file.
     * @param tuple A tuple containing the user and item to update.
     */
    public void update(Tuple2<U, I> tuple)
    {
        int uidx = userIndex.user2uidx(tuple.v1);
        int iidx = itemIndex.item2iidx(tuple.v2);

        this.recommender.update(uidx, iidx);
        this.metrics.forEach((name, metric) -> metric.update(uidx, iidx));
        ++this.iteration;
    }

    /**
     * Obtains the iteration number.
     * @return the iteration number.
     */
    public int getCurrentIteration()
    {
        return this.iteration;
    }

    /**
     * Executes the next iteration of the loop.
     * @return a tuple containing the user and the item selected in the loop. Null if the loop has finished.
     */
    public Tuple2<U, I> nextIteration()
    {
        // We cannot continue.
        if(this.numUsers == 0)
            return null;

        // Select user and item for this iteration.
        boolean cont = false;
        int uidx;
        int iidx;
        do
        {
            int index = rng.nextInt(numUsers);
            uidx = this.userList.get(index);
            iidx = recommender.next(uidx);
            // If the user cannot be recommended another item.
            if(iidx != -1)
            {
                cont = true;
            }
            else
            {
                this.numUsers--;
                this.userList.remove(index);
            }
        }
        while(!cont && this.numUsers > 0);

        if(this.numUsers == 0)
            return null;

        int defUidx = uidx;
        int defIidx = iidx;
        recommender.update(defUidx, defIidx);
        metrics.forEach((name, metric) -> metric.update(defUidx, defIidx));
        ++this.iteration;
        return new Tuple2<>(userIndex.uidx2user(uidx),itemIndex.iidx2item(iidx));
    }

    /**
     * Obtains the values for the metrics in the current iteration.
     * @return the values for the metrics in the current iteration.
     */
    public Map<String, Double> getMetrics()
    {
        Map<String, Double> values = new HashMap<>();
        this.metrics.forEach((name, metric) -> values.put(name, metric.compute()));
        return values;
    }
}
