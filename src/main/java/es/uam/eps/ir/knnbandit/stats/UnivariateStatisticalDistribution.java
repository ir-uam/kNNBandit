/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0.
 * 
 */
package es.uam.eps.ir.knnbandit.stats;

/**
 * Interface for determining a univariate statistical distribution.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public interface UnivariateStatisticalDistribution 
{
    /**
     * Updates the parameters of the distribution.
     * @param values The corresponding values.
     */
    public void update(Double... values);
    
    /**
     * Updates a single parameter of the distribution.
     * @param value The new value.
     * @param i The index of the parameter.
     */
    public void update(double value, int i);
    
    /**
     * Finds the mean of the distribution.
     * @return the mean of the distribution.
     */
    public double mean();
    
    /**
     * Obtains the value of a given parameter.
     * @param i The index of the parameter.
     * @return the value of the parameter.
     */
    public double getParameter(int i);
    
    /**
     * Obtains a random sample from the distribution.
     * @return The random sample.
     */
    public double sample();
}
