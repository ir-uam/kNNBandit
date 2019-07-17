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
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Writes a graph to a file.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @param <V> Type of the vertices.
 */
public class TextGraphWriter<V> implements GraphWriter<V>
{
    /**
     * The delimiter for separating fields.
     */
    private final String delimiter;
    
    /**
     * Constructor.
     * @param delimiter the delimiter for separating fields.
     */
    public TextGraphWriter(String delimiter)
    {
        this.delimiter = delimiter;
    }
    
    @Override
    public boolean write(Graph<V> graph, String file)
    {
        try
        {
            return this.write(graph, new FileOutputStream(file));
        } 
        catch (FileNotFoundException ex)
        {
            return false;
        }
    }

    @Override
    public boolean write(Graph<V> graph, OutputStream file)
    {
        return this.write(graph, file, true, false);
    }

    @Override
    public boolean write(Graph<V> graph, String file, boolean writeWeights, boolean writeTypes)
    {
        try
        {
            return this.write(graph, new FileOutputStream(file), writeWeights, writeTypes);
        } 
        catch (FileNotFoundException ex)
        {
            return false;
        }
    }

    @Override
    public boolean write(Graph<V> graph, OutputStream file, boolean writeWeights, boolean writeTypes)
    {
        try
        {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(file));
            boolean ret;
            ret =writeSimpleGraph(graph, bw, writeWeights, writeTypes);
            bw.close();
            return ret;
        } 
        catch (IOException ex)
        {
            return false;
        }
    }
    

    
    /**
     * Writes a simple graph into a file
     * @param graph The simple graph we want to write
     * @param bw The file
     * @param writeWeights Indicates if weights have to be written or not.
     * @param writeTypes Indicates if types have to be written or not.
     * @return true if everything went OK, false if not.
     */
    private boolean writeSimpleGraph(Graph<V> graph, BufferedWriter bw, boolean writeWeights, boolean writeTypes)
    {
        boolean directed = graph.isDirected();
        
        try
        {
            if(directed)
            {
                List<V> nodes = graph.getAllNodes().collect(Collectors.toCollection(ArrayList::new));
                for(V node : nodes)
                {
                    List<V> adjacentNodes = graph.getAdjacentNodes(node).collect(Collectors.toCollection(ArrayList::new));
                    for(V v : adjacentNodes)
                    {
                        Double weight = graph.getEdgeWeight(node, v);
                        Integer type = graph.getEdgeType(node, v);
                        
                        String toWrite = node.toString() + delimiter + v.toString();
                        
                        if(writeWeights) toWrite += delimiter + weight;
                        if(writeTypes) toWrite += delimiter + type;
                        toWrite += "\n";
                        
                        bw.write(toWrite);
                    }
                }
            }
            else
            {
                Set<V> visited = new HashSet<>();
                List<V> nodes = graph.getAllNodes().collect(Collectors.toCollection(ArrayList::new));
                for(V node : nodes)
                {
                    List<V> adjacentNodes = graph.getAdjacentNodes(node).filter(v -> !visited.contains(v)).collect(Collectors.toCollection(ArrayList::new));
                    for(V v : adjacentNodes)
                    {
                        Double weight = graph.getEdgeWeight(node, v);
                        Integer type = graph.getEdgeType(node, v);
                        
                        bw.write(node.toString() + delimiter + v.toString());
                        if(writeWeights) bw.write(delimiter + weight);
                        if(writeTypes) bw.write(delimiter + type);
                        bw.write("\n");
                        
                    }
                    
                    visited.add(node);
                }
            }
                
        }
        catch(IOException ex)
        {
            return false;
        }

        return true;
    }

    
}
