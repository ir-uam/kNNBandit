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

/**
 * Interface for computing cumulative metrics.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <U> User type.
 * @param <I> Item type.
 */
public interface CumulativeMetric<U,I>
{
    /**
     * Obtains the current value of the metric.
     * @return the value of the metric.
     */
    public double compute();
    
    /**
     * Updates the current value of the metric.
     * @param uidx User identifier.
     * @param iidx Item identifier.
     */
    public void update(int uidx, int iidx);
    
    /**
     * Resets the metric.
     */
    public void reset();
}
