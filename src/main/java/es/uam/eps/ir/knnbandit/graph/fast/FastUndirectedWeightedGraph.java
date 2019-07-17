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

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import es.uam.eps.ir.knnbandit.graph.UndirectedWeightedGraph;
import es.uam.eps.ir.knnbandit.graph.edges.EdgeOrientation;
import es.uam.eps.ir.knnbandit.graph.edges.fast.FastUndirectedWeightedEdges;
import es.uam.eps.ir.knnbandit.graph.index.fast.FastIndex;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.sparse.LinkedSparseMatrix;

/**
 * Fast implementation for an Undirected Weighted graph.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <V> type of the nodes
 */
public class FastUndirectedWeightedGraph<V> extends FastGraph<V> implements UndirectedWeightedGraph<V>
{
    /**
     * Constructor for an empty graph.
     */
    public FastUndirectedWeightedGraph()
    {
        super(new FastIndex<>(), new FastUndirectedWeightedEdges());
    }
 
    @Override
    public DoubleMatrix2D getAdjacencyMatrix(EdgeOrientation direction)
    {
        DoubleMatrix2D matrix = new SparseDoubleMatrix2D(new Long(this.getVertexCount()).intValue(), new Long(this.getVertexCount()).intValue());
        // Creation of the adjacency matrix.
        for(int row = 0; row < matrix.rows(); ++row)
        {   
            for(int col = 0; col < matrix.rows(); ++col)
            {
                if(this.containsEdge(this.vertices.idx2object(col), this.vertices.idx2object(row)) ||
                    this.containsEdge(this.vertices.idx2object(row), this.vertices.idx2object(col)))
                     matrix.setQuick(row, col, 1.0);

            }
        }
        
        return matrix;
    }
    
    @Override
    public Matrix getAdjacencyMatrixMTJ(EdgeOrientation direction)
    {
        Matrix matrix = new LinkedSparseMatrix(new Long(this.getVertexCount()).intValue(), new Long(this.getVertexCount()).intValue());
        this.vertices.getAllObjects().forEach(u -> 
        {
            int uIdx = this.vertices.object2idx(u);
            this.getNeighbourNodes(u).forEach(v -> 
            {
                int vIdx = this.vertices.object2idx(v);
                matrix.set(uIdx, vIdx, 1.0);
            });
        });
        
        return matrix;
    }
}
