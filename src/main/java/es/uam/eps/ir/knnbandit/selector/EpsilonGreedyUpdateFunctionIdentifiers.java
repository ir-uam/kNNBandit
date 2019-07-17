/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0.
 * 
 */
package es.uam.eps.ir.knnbandit.selector;

/**
 * Identifiers of the different Epsilon-Greedy update functions.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class EpsilonGreedyUpdateFunctionIdentifiers 
{
    public static final String STATIONARY = "stationary";
    public static final String NONSTATIONARY = "nonStationary";
    public static final String USEALL = "useAll";
    public static final String COUNT = "count";
}
