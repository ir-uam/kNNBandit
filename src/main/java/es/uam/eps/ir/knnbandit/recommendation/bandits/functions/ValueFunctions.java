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
 * Functions that determine the value of arms.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class ValueFunctions 
{
    /**
     * The value stays the same.
     * @return a function that keeps the value.
     */
    public static ValueFunction identity()
    {
        return (int uidx, int iidx, double currentValue, double numTimes) -> currentValue;
    }
}
