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
 * Identifiers of the algorithms that can be used.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class AlgorithmIdentifiers 
{
    // Simple algorithms.
    public static final String RANDOM = "random";
    public static final String AVG = "average";
    public static final String POP = "popularity";
    // Non-personalized item-oriented bandits.
    public static final String ITEMBANDIT = "itembandit";
    // User based.
    public static final String USERBASEDKNN = "ubknn";
    public static final String BANDITKNN = "knnbandit";
    // Matrix factorization.
    public static final String MF = "mf";
}
