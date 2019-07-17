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
 * Bandit identifiers.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class ItemBanditIdentifiers 
{
    public static final String EGREEDY = "epsilon";
    public static final String UCB1 = "ucb1";
    public static final String UCB1TUNED = "ucb1tuned";
    public static final String THOMPSON = "thompson";
    public static final String ETGREEDY = "epsilont";
}
