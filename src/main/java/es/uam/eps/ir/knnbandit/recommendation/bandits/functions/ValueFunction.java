/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0.
 * 
 */
package es.uam.eps.ir.knnbandit.recommendation.bandits.functions;

/**
 * Interface for a function that determines the current value of an arm.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
@FunctionalInterface
public interface ValueFunction 
{
    /**
     * Applies the function.
     * @param uidx Identifier of the user.
     * @param iidx Identifier of the item.
     * @param currentValue Current value of the arm.
     * @param numTimes Number of times the arm has been selected.
     * @return the value of the arm in our context.
     */
    public abstract double apply(int uidx, int iidx, double currentValue, double numTimes);
}
