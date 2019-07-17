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

import java.util.Optional;
import es.uam.eps.ir.ranksys.fast.preference.IdxPref;
import es.uam.eps.ir.ranksys.fast.preference.SimpleFastPreferenceData;

/**
 * Cumulative implementation of global recall.
 * @author Javier Sanz-Cruzado Puig (javier.sanz-cruzado@uam.es)
 * @param <U> User type.
 * @param <I> Item type.
 */
public class CumulativeRecall<U,I> implements CumulativeMetric<U,I>
{
    /**
     * Number of relevant (user,item) pairs.
     */
    private final int numRel;
    /**
     * Number of currently discovered (user, item) pairs.
     */
    private double current;
    /**
     * Relevance threshold.
     */
    private final double threshold;
    /**
     * Preference data.
     */
    private final SimpleFastPreferenceData<U,I> prefData;
    
    /**
     * Constructor.
     * @param prefData Total preference data.
     * @param numRel Number of relevant (user, item) pairs. 
     * @param threshold Relevance threshold.
     */
    public CumulativeRecall(SimpleFastPreferenceData<U,I> prefData, int numRel, double threshold)
    {
        this.prefData = prefData;
        this.numRel = numRel;
        this.current = 0.0;
        this.threshold = threshold;
    }
    
    @Override
    public double compute()
    {
        if(numRel == 0) return 0.0;
        return this.current/(this.numRel + 0.0);
    }

    @Override
    public void update(int uidx, int iidx)
    {
        Optional<IdxPref> value = this.prefData.getPreference(uidx, iidx);
        if(value.isPresent() && value.get().v2 >= threshold)
        {
            this.current++;
        }
    }

    @Override
    public void reset()
    {
        this.current = 0.0;
    }

}
