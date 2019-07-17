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

import java.util.stream.Stream;

import es.uam.eps.ir.ranksys.fast.preference.IdxPref;

/**
 * Interface for the directed edges.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public interface UndirectedEdges extends Edges
{   
    @Override
    public default Stream<Integer> getIncidentNodes(int node)
    {
        return this.getNeighbourNodes(node);
    }
    
    @Override
    public default Stream<Integer> getAdjacentNodes(int node)
    {
        return this.getNeighbourNodes(node);
    }
    
    @Override
    public default Stream<Integer> getMutualNodes(int node)
    {
        return this.getNeighbourNodes(node);
    }
    
    @Override
    public default Stream<EdgeType> getIncidentTypes(int node)
    {
        return this.getNeighbourTypes(node);
    }
    
    @Override
    public default Stream<EdgeType> getAdjacentTypes(int node)
    {
        return this.getNeighbourTypes(node);
    }
    
    @Override
    public default Stream<IdxPref> getIncidentWeights(int node)
    {
        return this.getNeighbourWeights(node);
    }
    
    @Override
    public default Stream<EdgeType> getMutualAdjacentTypes(int node)
    {
        return this.getNeighbourTypes(node);
    }
    
    @Override
    public default Stream<EdgeType> getMutualIncidentTypes(int node)
    {
        return this.getNeighbourTypes(node);
    }
    
    @Override
    public default Stream<EdgeType> getMutualTypes(int node)
    {
        return this.getNeighbourTypes(node);
    }
    
    @Override
    public default Stream<IdxPref> getAdjacentWeights(int node)
    {
        return this.getNeighbourWeights(node);
    }
    
      @Override
    public default Stream<IdxPref> getMutualAdjacentWeights(int node)
    {
        return this.getNeighbourWeights(node);
    }
    
    @Override
    public default Stream<IdxPref> getMutualIncidentWeights(int node)
    {
        return this.getNeighbourWeights(node);
    }
    
    @Override
    public default Stream<IdxPref> getMutualWeights(int node)
    {
        return this.getNeighbourWeights(node);
    }

    @Override
    public default long getAdjacentCount(int node)
    {
        return this.getNeighbourCount(node);
    }
    
    @Override
    public default long getMutualCount(int node)
    {
        return this.getNeighbourCount(node);
    }

}
