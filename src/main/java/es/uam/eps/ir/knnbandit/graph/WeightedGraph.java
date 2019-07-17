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

import java.util.stream.Stream;

/**
 * Interface for directed graphs.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <V> Type of vertices.
 */
public interface WeightedGraph<V> extends Graph<V>
{
    /**
     * Given a node, finds all the nodes u such that the edge (u to node) is in the graph.
     * @param node The node.
     * @return A stream of the incident nodes.
     */
    @Override
    public default Stream<Weight<V,Double>> getIncidentNodesWeights(V node)
    {
        return this.getIncidentNodes(node).map((inc) -> new Weight<>(inc, this.getEdgeWeight(inc,node)));
    }
    
    /**
     * Given a node, finds all the nodes u such that the edge (node to u) is in the graph.
     * @param node The node.
     * @return A stream containing the adjacent nodes.
     */
    @Override
    public default Stream<Weight<V,Double>> getAdjacentNodesWeights(V node)
    {
        return this.getAdjacentNodes(node).map((inc) -> new Weight<>(inc, this.getEdgeWeight(node,inc)));
    }
    
    /**
     * Given a node, finds all the nodes u so that either (node to u) or (u to node) are in the graph.
     * @param node The node.
     * @return A stream containing all the nodes in the neighbourhood.
     */
    @Override
    public default Stream<Weight<V,Double>> getNeighbourNodesWeights(V node)
    {
        return this.getNeighbourNodes(node).map((inc) -> new Weight<>(inc, this.getEdgeWeight(node,inc)));
    }
    
    @Override
    public default boolean isWeighted()
    {
        return true;
    }
}
