/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0.
 * 
 */
package es.uam.eps.ir.knnbandit.graph;

import es.uam.eps.ir.knnbandit.graph.edges.EdgeOrientation;
import es.uam.eps.ir.knnbandit.graph.edges.EdgeWeight;
import java.util.stream.Stream;

/**
 * Interface for undirected unweighted graphs.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <V> Type of vertices.
 */
public interface UndirectedUnweightedGraph<V> extends UnweightedGraph<V>, UndirectedGraph<V>
{   

    @Override
    public default Stream<Weight<V,Double>> getIncidentNodesWeights(V node)
    {
        return getNeighbourNodesWeights(node);
    }

    @Override
    public default Stream<Weight<V,Double>> getAdjacentNodesWeights(V node)
    {
        return getNeighbourNodesWeights(node);
    }

    @Override
    public default Stream<Weight<V,Double>> getNeighbourNodesWeights(V node)
    {
        if(this.containsVertex(node))
            return this.getNeighbourNodes(node).map((neigh)->new Weight<>(neigh, EdgeWeight.getDefaultValue()));
        return null;
    }

    @Override
    public default Stream<Weight<V,Double>> getNeighbourhoodWeights(V node, EdgeOrientation direction)
    {
        return getNeighbourNodesWeights(node);
    }
}
