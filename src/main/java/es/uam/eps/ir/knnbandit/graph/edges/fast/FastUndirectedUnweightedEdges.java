/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0.
 * 
 */
package es.uam.eps.ir.knnbandit.graph.edges.fast;

import es.uam.eps.ir.knnbandit.graph.edges.EdgeType;
import es.uam.eps.ir.knnbandit.graph.edges.EdgeWeight;
import es.uam.eps.ir.knnbandit.graph.edges.UndirectedEdges;
import es.uam.eps.ir.knnbandit.graph.edges.UnweightedEdges;
import es.uam.eps.ir.ranksys.fast.preference.IdxPref;

import es.uam.eps.ir.knnbandit.graph.index.FastUnweightedAutoRelation;
import es.uam.eps.ir.knnbandit.graph.index.FastWeightedAutoRelation;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Fast implementation of undirected unweighted edges.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class FastUndirectedUnweightedEdges extends FastEdges implements UndirectedEdges,UnweightedEdges
{
    /**
     * Constructor.
     */
    public FastUndirectedUnweightedEdges()
    {
        super(new FastUnweightedAutoRelation<>(), new FastWeightedAutoRelation<>());
    }

    @Override
    public Stream<Integer> getNeighbourNodes(int node)
    {
        return this.weights.getIdsFirst(node).map(weight -> weight.getIdx());
    }

    @Override
    public Stream<EdgeType> getNeighbourTypes(int node)
    {
        return this.types.getIdsFirst(node).map(type -> new EdgeType(type.getIdx(), type.getValue()));
    }

    @Override
    public Stream<IdxPref> getIncidentWeights(int node)
    {
        return this.getNeighbourWeights(node);
    }

    @Override
    public Stream<IdxPref> getAdjacentWeights(int node)
    {
        return this.getNeighbourWeights(node);
    }

    @Override
    public Stream<IdxPref> getNeighbourWeights(int node)
    {
        return this.weights.getIdsFirst(node).map(weight -> new EdgeWeight(weight.getIdx(), EdgeWeight.getDefaultValue()));
    }

    @Override
    public boolean addEdge(int orig, int dest, double weight, int type)
    {
        if(orig != dest)
        {
            if (this.weights.addRelation(orig, dest, weight) &&
                    this.weights.addRelation(dest, orig, weight) &&
                    this.types.addRelation(orig, dest, type) &&
                    this.types.addRelation(dest, orig, type))
            {
                this.numEdges++;
                return true;
            }
        }
        else
        {
            if(this.weights.addRelation(orig, dest, weight) && this.types.addRelation(orig, dest, type))
            {
                this.numEdges++;
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean updateEdgeWeight(int orig, int dest, double weight)
    {
        return this.containsEdge(orig, dest);
    }

    @Override
    public boolean removeNode(int idx)
    {
        long toDel = this.getAdjacentCount(idx);
        if(this.weights.remove(idx) && this.types.remove(idx))
        {
            this.numEdges -= toDel;
            return true;
        }
        return false;
    }
    
    @Override
    public IntStream getNodesWithIncidentEdges() 
    {
        return this.weights.secondsWithFirsts();
    }

    @Override
    public IntStream getNodesWithAdjacentEdges() 
    {
        return this.weights.firstsWithSeconds();
    }

    @Override
    public IntStream getNodesWithEdges() 
    {
        return this.weights.firstsWithSeconds();
    }
    
    @Override
    public IntStream getNodesWithMutualEdges() 
    {
        return this.weights.firstsWithSeconds();
    }
}
