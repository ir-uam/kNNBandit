/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0.
 * 
 */
package es.uam.eps.ir.knnbandit.graph.edges;

import es.uam.eps.ir.ranksys.fast.preference.IdxPref;

import java.util.stream.Stream;

/**
 * Interface for weighted edges.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public interface WeightedEdges extends Edges
{

    /**
     * Given a node, finds all the weights of edges such that the edge (u to node) is in the graph.
     * @param node The node.
     * @return a stream of the weights of incident nodes.
     */
    @Override
    public default Stream<IdxPref> getIncidentWeights(int node)
    {
        return this.getIncidentNodes(node).map((inc) -> new EdgeWeight(inc, this.getEdgeWeight(inc, node)));
    }
    
    /**
     * Given a node, finds all the weights of edges u such that the edge (node to u) is in the graph.
     * @param node The node.
     * @return a stream containing the weights adjacent nodes.
     */
    @Override
    public default Stream<IdxPref> getAdjacentWeights(int node)
    {
        return this.getAdjacentNodes(node).map((inc) -> new EdgeWeight(inc, this.getEdgeWeight(node,inc)));
    }
    
    /**
     * Given a node, finds all the all the weights of edges so that either (node to u) or (u to node) are in the graph.
     * @param node The node.
     * @return a stream containing all the weights of the nodes in the neighbourhood.
     */
    @Override
    public default Stream<IdxPref> getNeighbourWeights(int node)
    {
        return this.getNeighbourNodes(node).map((inc) -> new EdgeWeight(inc, this.getEdgeWeight(node,inc)));
    }
}
