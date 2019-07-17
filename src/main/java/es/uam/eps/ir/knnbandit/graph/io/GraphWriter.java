/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0.
 * 
 */
package es.uam.eps.ir.knnbandit.graph.io;

import es.uam.eps.ir.knnbandit.graph.Graph;
import java.io.OutputStream;

/**
 * Interface for graph writers.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <V> type of the vertices.
 */
public interface GraphWriter<V>
{
    /**
     * Writes a graph into a file. It writes the weights, but not the types
     * @param graph The graph we want to write.
     * @param file The file.
     * @return true if everything went OK, false otherwise.
     */
    public boolean write(Graph<V> graph, String file);
    
    /**
     * Writes a graph into an output stream. It writes the weights, but not the types.
     * @param graph The graph we want to write.
     * @param file The output stream.
     * @return true if everything went OK, false otherwise.
     */
    public boolean write(Graph<V> graph, OutputStream file);
    
    /**
     * Writes a graph into a file. Simple graphs types are written, while multigraph
     * types are not.
     * @param graph The graph we want to write.
     * @param file The file.
     * @param writeWeights Indicates if weights have to be written.
     * @param writeTypes Indicates if types have to be written.
     * @return true if everything is ok, false otherwise.
     */
    public boolean write(Graph<V> graph, String file, boolean writeWeights, boolean writeTypes);
    
    /**
     * Writes a graph into a output stream. Simple graphs types are written, while multigraph
     * types are not.
     * @param graph The graph we want to write.
     * @param file The file.
     * @param writeWeights Indicates if weights have to be written.
     * @param writeTypes Indicates if types have to be written.
     * @return true if everything is ok, false otherwise.
     */
    public boolean write(Graph<V> graph, OutputStream file, boolean writeWeights, boolean writeTypes);
    
    
}
