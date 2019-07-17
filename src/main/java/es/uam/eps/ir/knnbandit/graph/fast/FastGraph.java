/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0.
 * 
 */
package es.uam.eps.ir.knnbandit.graph.fast;

import es.uam.eps.ir.knnbandit.graph.edges.EdgeOrientation;
import es.uam.eps.ir.knnbandit.graph.edges.EdgeType;
import es.uam.eps.ir.knnbandit.graph.edges.Edges;
import es.uam.eps.ir.ranksys.fast.preference.IdxPref;
import es.uam.eps.ir.knnbandit.graph.Graph;
import es.uam.eps.ir.knnbandit.graph.Weight;

import es.uam.eps.ir.knnbandit.graph.index.Index;
import java.io.Serializable;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Fast implementation of a graph. 
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <V> Type of the vertices.
 */
public abstract class FastGraph<V> implements Graph<V>, Serializable
{
    /**
     * Index of vertices.
     */
    protected final Index<V> vertices;
    /**
     * Edges in the network.
     */
    protected final Edges edges;
    
    /**
     * Constructor.
     * @param vertices An index for the vertices of the graph.
     * @param edges Edges. 
     */
    public FastGraph(Index<V> vertices, Edges edges)
    {
        this.vertices = vertices;
        this.edges = edges;
    }
    
    @Override
    public boolean addNode(V node)
    {
        if(vertices.containsObject(node))
            return false;
        int idx = vertices.addObject(node);
        
        if(idx != -1)
        {
            return edges.addUser(idx);
        }
        return false;
    }
        
    @Override
    public boolean addEdge(V nodeA, V nodeB, double weight, int type, boolean insertNodes)
    {
        if(insertNodes)
        {
            this.addNode(nodeA);
            this.addNode(nodeB);
        }
        
        if(this.containsVertex(nodeA) && this.containsVertex(nodeB))
        {
            return this.edges.addEdge(this.vertices.object2idx(nodeA), this.vertices.object2idx(nodeB), weight, type);
        }
        return false;
    }

    @Override
    public Stream<V> getAllNodes()
    {
        return this.vertices.getAllObjects();
    }
    
    @Override
    public Stream<V> getIncidentNodes(V node)
    {
        if(this.containsVertex(node))
            return this.edges.getIncidentNodes(this.vertices.object2idx(node)).map(n -> this.vertices.idx2object(n));
        return Stream.empty();
    }

    @Override
    public Stream<V> getAdjacentNodes(V node)
    {
        if(this.containsVertex(node))
            return this.edges.getAdjacentNodes(this.vertices.object2idx(node)).map(n -> this.vertices.idx2object(n));   
        return Stream.empty();
    }

    @Override
    public Stream<V> getNeighbourNodes(V node)
    {
        if(this.containsVertex(node))
            return this.edges.getNeighbourNodes(this.vertices.object2idx(node)).map(n -> this.vertices.idx2object(n));
        return Stream.empty();
    }
    
    @Override
    public Stream<V> getMutualNodes(V node)
    {
        if(this.containsVertex(node))
            return this.edges.getMutualNodes(this.vertices.object2idx(node)).map(n -> this.vertices.idx2object(n));
        return Stream.empty();
    }
    
    @Override
    public Stream<V> getNeighbourhood(V node, EdgeOrientation direction)
    {
        switch (direction)
        {
            case IN:
                return this.getIncidentNodes(node);
            case OUT:
                return this.getAdjacentNodes(node);
            case MUTUAL:
                return this.getMutualNodes(node);               
            default:
                return this.getNeighbourNodes(node);
        }
    }
    
    @Override
    public int getIncidentEdgesCount(V node)
    {
        if(this.containsVertex(node))
            return (int) this.edges.getIncidentCount(this.vertices.object2idx(node));
        return 0;
    }
    
    @Override
    public int getAdjacentEdgesCount(V node)
    {
        if(this.containsVertex(node))
            return (int) this.edges.getAdjacentCount(this.vertices.object2idx(node));
        return 0;
    }
        
    @Override
    public int getMutualEdgesCount(V node)
    {
        if(this.containsVertex(node))
            return (int) this.edges.getMutualCount(this.vertices.object2idx(node));
        return 0;
    }
    
    @Override
    public boolean containsVertex(V node)
    {
        return this.vertices.containsObject(node);
    }
    
    @Override
    public boolean containsEdge(V nodeA, V nodeB)
    {
        if(this.containsVertex(nodeA) && this.containsVertex(nodeB))            
            return this.edges.containsEdge(this.vertices.object2idx(nodeA), this.vertices.object2idx(nodeB));
        return false;
    }    

    @Override
    public double getEdgeWeight(V nodeA, V nodeB)
    {
        return this.edges.getEdgeWeight(this.vertices.object2idx(nodeA), this.vertices.object2idx(nodeB));
    }
    
    @Override
    public boolean updateEdgeWeight(V nodeA, V nodeB, double weight)
    {
        return this.edges.updateEdgeWeight(this.vertices.object2idx(nodeA),this.vertices.object2idx(nodeB), weight);
    }

    @Override
    public Stream<Weight<V,Double>> getIncidentNodesWeights(V node)
    {
        if(this.containsVertex(node))
            return this.edges.getIncidentWeights(this.vertices.object2idx(node))
                .map(weight->new Weight<>(this.vertices.idx2object(weight.v1()), weight.v2()));
        return Stream.empty();
    }

    @Override
    public Stream<Weight<V,Double>> getAdjacentNodesWeights(V node)
    {
        if(this.containsVertex(node))
            return this.edges.getAdjacentWeights(this.vertices.object2idx(node))
                .map(weight->new Weight<>(this.vertices.idx2object(weight.v1()), weight.v2()));
        return Stream.empty();
    }
    
    @Override
    public Stream<Weight<V,Double>> getNeighbourNodesWeights(V node)
    {
        if(this.containsVertex(node))
            return this.edges.getNeighbourWeights(this.vertices.object2idx(node))
                .map(weight->new Weight<>(this.vertices.idx2object(weight.v1()), weight.v2()));
        return Stream.empty();
    }
    
    @Override
    public Stream<Weight<V,Double>> getAdjacentMutualNodesWeights(V node)
    {
        if(!this.containsVertex(node))
            return this.edges.getMutualAdjacentWeights(this.vertices.object2idx(node))
                    .map(weight -> new Weight<>(this.vertices.idx2object(weight.v1()), weight.v2()));
        return Stream.empty();
    }

    @Override
    public Stream<Weight<V,Double>> getIncidentMutualNodesWeights(V node)
    {
        if(!this.containsVertex(node))
            return this.edges.getMutualIncidentWeights(this.vertices.object2idx(node))
                    .map(weight -> new Weight<>(this.vertices.idx2object(weight.v1()), weight.v2()));
        return Stream.empty();
    }
    
    @Override
    public Stream<Weight<V,Double>> getMutualNodesWeights(V node)
    {
        if(!this.containsVertex(node))
            return this.edges.getMutualWeights(this.vertices.object2idx(node))
                    .map(weight -> new Weight<>(this.vertices.idx2object(weight.v1()), weight.v2()));
        return Stream.empty();
    }

    @Override
    public int getEdgeType(V nodeA, V nodeB)
    {
        return this.edges.getEdgeType(this.vertices.object2idx(nodeA), this.vertices.object2idx(nodeB));
    }

    @Override
    public Stream<Weight<V,Integer>> getIncidentNodesTypes(V node)
    {
        if(this.containsVertex(node))
            return this.edges.getIncidentTypes(this.vertices.object2idx(node))
                .map(type -> new Weight<>(this.vertices.idx2object(type.getIdx()), type.getValue()));
        return null;
    }

    @Override
    public Stream<Weight<V,Integer>> getAdjacentNodesTypes(V node)
    {
        if(this.containsVertex(node))
            return this.edges.getAdjacentTypes(this.vertices.object2idx(node))
                .map(type -> new Weight<>(this.vertices.idx2object(type.getIdx()), type.getValue()));
        return null;
    }

    @Override
    public Stream<Weight<V,Integer>> getNeighbourNodesTypes(V node)
    {
        if(this.containsVertex(node))
            return this.edges.getNeighbourTypes(this.vertices.object2idx(node))
                .map(type -> new Weight<>(this.vertices.idx2object(type.getIdx()), type.getValue()));
        return null;
    }

    @Override
    public Stream<Weight<V,Integer>> getAdjacentMutualNodesTypes(V node)
    {
        if(this.containsVertex(node))
            return this.edges.getMutualAdjacentTypes(this.vertices.object2idx(node))
                .map(type -> new Weight<>(this.vertices.idx2object(type.getIdx()), type.getValue()));
        return null;
    }
    
     @Override
    public Stream<Weight<V,Integer>> getIncidentMutualNodesTypes(V node)
    {
        if(this.containsVertex(node))
            return this.edges.getMutualIncidentTypes(this.vertices.object2idx(node))
                .map(type -> new Weight<>(this.vertices.idx2object(type.getIdx()), type.getValue()));
        return null;
    }
    
    @Override
    public long getVertexCount() 
    {
        return this.vertices.numObjects();
    }

    @Override
    public long getEdgeCount() 
    {
        return this.edges.getNumEdges();
    }
    
    @Override
    public boolean removeEdge(V orig, V dest)
    {
        int origIdx = this.vertices.object2idx(orig);
        int destIdx = this.vertices.object2idx(dest);
        return this.edges.removeEdge(origIdx, destIdx);
    }
    
    @Override
    public boolean removeNode(V u)
    {
        int uidx = this.vertices.object2idx(u);
        if(this.edges.removeNode(uidx))
        {
            if(this.vertices.removeObject(u) >= 0)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public int object2idx(V u)
    {
        return this.vertices.object2idx(u);
    }
    
    @Override
    public V idx2object(int idx)
    {
        return this.vertices.idx2object(idx);
    }
    
    /**
     * Obtains the index for the vertices.
     * @return the index for the vertices.
     */
    public Index<V> getIndex()
    {
        return this.vertices;
    }
    
    public double getEdgeWeight(int uidx, int vidx)
    {
        return this.edges.getEdgeWeight(uidx, vidx);
    }
    
    public Stream<Integer> getNeighborhood(int uidx, EdgeOrientation orientation)
    {
        switch (orientation)
        {
            case OUT:
                return this.edges.getAdjacentNodes(uidx);
            case IN:
                return this.edges.getIncidentNodes(uidx);
            case MUTUAL:
                return this.edges.getMutualNodes(uidx);                
            default:
                return this.edges.getNeighbourNodes(uidx);
        }
    }
    
    public Stream<IdxPref> getNeighborhoodWeights(int uidx, EdgeOrientation orientation)
    {
        switch (orientation)
        {
            case OUT:
                return this.edges.getAdjacentWeights(uidx);
            case IN:
                return this.edges.getIncidentWeights(uidx);
            case MUTUAL:
                return this.edges.getMutualWeights(uidx);                
            default:
                return this.edges.getNeighbourWeights(uidx);
        }
    }
    
    public Stream<EdgeType> getNeighborhoodTypes(int uidx, EdgeOrientation orientation)
    {
        switch (orientation)
        {
            case OUT:
                return this.edges.getAdjacentTypes(uidx);
            case IN:
                return this.edges.getIncidentTypes(uidx);
            case MUTUAL:
                return this.edges.getMutualTypes(uidx);               
            default:
                return this.edges.getNeighbourTypes(uidx);
        }
    }
    
    public IntStream getAllNodesIds()
    {
        return this.vertices.getAllObjectsIds();
    }
    
    public boolean containsEdge(int uidx, int vidx)
    {
        return this.edges.containsEdge(uidx, vidx);
    }
    
    /**
     * Uncontrolled edge addition method, using ids.
     * @param nodeA Identifier of the first node.
     * @param nodeB Identifier of the second node.
     * @param weight Weight of the link.
     * @param type Type of the link.
     * @return true if everything went ok, false otherwise.
     */
    public boolean addEdge(int nodeA, int nodeB, double weight, int type)
    {
        return this.edges.addEdge(nodeA, nodeB, weight, type);
    }
        
    /**
     * Uncontrolled edge update method, using ids.
     * @param nodeA Identifier of the first node.
     * @param nodeB Identifier of the second node.
     * @param weight Weight of the link.
     * @return true if everything went ok, false otherwise.
     */
    public boolean updateEdgeWeight(int nodeA, int nodeB, double weight)
    {
        return this.edges.updateEdgeWeight(nodeA,nodeB, weight);
    }
    
    @Override
    public Stream<V> getIsolatedNodes() 
    {
        return this.edges.getIsolatedNodes().mapToObj(i -> this.idx2object(i));
    }

    @Override
    public Stream<V> getNodesWithEdges(EdgeOrientation direction) 
    {
        switch (direction) 
        {
            case IN:
                return this.edges.getNodesWithIncidentEdges().mapToObj(i -> this.idx2object(i));
            case OUT:
                return this.edges.getNodesWithAdjacentEdges().mapToObj(i -> this.idx2object(i));
            case UND:
                return this.edges.getNodesWithEdges().mapToObj(i -> this.idx2object(i));
            case MUTUAL:
                return this.edges.getNodesWithMutualEdges().mapToObj(i -> this.idx2object(i));
            default:
                break;
        }
        
        return Stream.empty();
    }
    
    @Override
    public Stream<V> getNodesWithAdjacentEdges()
    {
        return this.getNodesWithEdges(EdgeOrientation.OUT);
    }
    
    @Override
    public Stream<V> getNodesWithIncidentEdges()
    {
        return this.getNodesWithEdges(EdgeOrientation.IN);
    }
    
    @Override
    public Stream<V> getNodesWithEdges()
    {
        return this.getNodesWithEdges(EdgeOrientation.UND);
    }
    
    @Override
    public Stream<V> getNodesWithMutualEdges()
    {
        return this.getNodesWithEdges(EdgeOrientation.MUTUAL);
    }
    
    @Override
    public boolean hasAdjacentEdges(V u)
    {
        return this.edges.hasAdjacentEdges(this.object2idx(u));
    }
    @Override
    public boolean hasIncidentEdges(V u)
    {
         return this.edges.hasIncidentEdges(this.object2idx(u));
    }
    @Override
    public boolean hasEdges(V u)
    {
        return this.edges.hasEdges(this.object2idx(u));
    }
    @Override
    public boolean hasMutualEdges(V u)
    {
        return this.edges.hasMutualEdges(this.object2idx(u));
    }
    
}
