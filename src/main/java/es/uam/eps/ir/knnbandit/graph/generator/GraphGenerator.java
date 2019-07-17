/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0.
 * 
 */
package es.uam.eps.ir.knnbandit.graph.generator;

import es.uam.eps.ir.knnbandit.graph.Graph;

/**
 * Generates different graphs.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <V> Type of the vertices.
 */
public interface GraphGenerator<V> 
{
    /**
     * Configures the generator.
     * @param configuration An array containing the configuration parameters.
     */
    public void configure(Object... configuration);
    /**
     * Generates a graph.
     * @return the generated graph.
     * @throws GeneratorNotConfiguredException The generator is not configured.
     * @throws GeneratorBadConfiguredException The generator parameters are incorretct.
     */
    public Graph<V> generate() throws GeneratorNotConfiguredException, GeneratorBadConfiguredException;
}
