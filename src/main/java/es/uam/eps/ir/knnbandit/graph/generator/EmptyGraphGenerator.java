/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0.
 * 
 */
package es.uam.eps.ir.knnbandit.graph.generator;


import es.uam.eps.ir.knnbandit.graph.Graph;
import es.uam.eps.ir.knnbandit.graph.fast.FastDirectedUnweightedGraph;
import es.uam.eps.ir.knnbandit.graph.fast.FastDirectedWeightedGraph;
import es.uam.eps.ir.knnbandit.graph.fast.FastUndirectedUnweightedGraph;
import es.uam.eps.ir.knnbandit.graph.fast.FastUndirectedWeightedGraph;

/**
 * Empty graph generator.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <V> Type of the vertices.
 */
public class EmptyGraphGenerator<V> implements GraphGenerator<V>
{
    /**
     * Indicates whether the graph is going to be directed.
     */
    private boolean directed;
    /**
     * Indicates whether the graph has been configured.
     */
    private boolean configured = false;
    /**
     * Indicates whether the graph has weights.
     */
    private boolean weighted;
    
    @SuppressWarnings("unchecked")
    @Override
    public void configure(Object... configuration) 
    {
        if(!(configuration == null) && configuration.length == 2)
        {
            boolean auxDirected = (boolean) configuration[0];
            boolean auxWeighted = (boolean) configuration[1];
            
            
            this.configure(auxDirected, auxWeighted);
        }
        else
        {
            configured = false;
        }
    }
    
    /**
     * Configures the graph
     * @param directed Whether the graph should be directed.
     * @param weighted Whether the graph should be weighted.
     */
    public void configure(boolean directed, boolean weighted)
    {
        this.directed = directed;
        this.weighted = weighted;
        this.configured = true;
    }
    
    @Override
    public Graph<V> generate() throws GeneratorNotConfiguredException, GeneratorBadConfiguredException
    {
        if(configured == false)
            throw new GeneratorNotConfiguredException("Empty graph: the generator was not configured");
        
        Graph<V> graph;
        if(directed)
            if(weighted)
                graph = new FastDirectedWeightedGraph<>();
            else
                graph = new FastDirectedUnweightedGraph<>();
        else
            if(weighted)
                graph = new FastUndirectedWeightedGraph<>();
            else
                graph = new FastUndirectedUnweightedGraph<>();
        
        return graph; 
    }
}
