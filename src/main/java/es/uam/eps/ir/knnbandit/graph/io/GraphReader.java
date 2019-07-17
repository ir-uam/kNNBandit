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
import es.uam.eps.ir.knnbandit.graph.index.Index;
import java.io.InputStream;

/**
 * Interface for graph readers.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <V> Type of the vertices.
 */
public interface GraphReader<V>
{
    /**
     * Given a file, reads a graph. 
     * @param file The file containing the nodes.
     * @return the graph if everything goes ok, null otherwise.
     */
    public Graph<V> read(String file);
    
    /**
     * Given a file, reads a graph.
     * @param file The file containing the graph.
     * @param readWeights True if the file contains weights, false otherwise.
     * @param readTypes True if the file contains types, false otherwise.
     * @return the graph if everything goes ok, null otherwise.
     */
    public Graph<V> read(String file, boolean readWeights, boolean readTypes);
    
    /**
     * Given an file, reads a graph.
     * @param file The file containing the graph.
     * @param readWeights True if the file contains weights, false otherwise.
     * @param readTypes True if the file contains graph types.
     * @param nodes An index containing the nodes in the network.
     * @return the graph if everything goes ok, null otherwise.
     */
    public Graph<V> read(String file, boolean readWeights, boolean readTypes, Index<V> nodes);
    
    /**
     * Given an input stream, reads a file from it (for reading embedded graphs in greater files).
     * By default, assumes the graph contains information about weights, but not about types.
     * @param stream The input stream we read the graph from.
     * @return the graph if everything goes ok, null otherwise.
     */
    public Graph<V> read(InputStream stream);
    
    /**
     * Given an input stream, reads a file from it (for reading embedded graphs in greater files).
     * @param stream The input stream we read the graph from.
     * @param readWeights True if the file contains weights, false otherwise.
     * @param readTypes True if the file contains graph types.
     * @return the graph if everything goes ok, null otherwise.
     */
    public Graph<V> read(InputStream stream, boolean readWeights, boolean readTypes);
    
    /**
     * Given an input stream, reads a file from it (for reading embedded graphs in greater files).
     * @param stream The input stream we read the graph from.
     * @param readWeights True if the file contains weights, false otherwise.
     * @param readTypes True if the file contains graph types.
     * @param nodes An index containing the nodes in the network.
     * @return the graph if everything goes ok, null otherwise.
     */
    public Graph<V> read(InputStream stream, boolean readWeights, boolean readTypes, Index<V> nodes);
}
