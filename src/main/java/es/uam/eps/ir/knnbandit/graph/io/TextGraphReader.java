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
import es.uam.eps.ir.knnbandit.graph.generator.EmptyGraphGenerator;
import es.uam.eps.ir.knnbandit.graph.generator.GeneratorBadConfiguredException;
import es.uam.eps.ir.knnbandit.graph.generator.GeneratorNotConfiguredException;
import es.uam.eps.ir.knnbandit.graph.generator.GraphGenerator;
import es.uam.eps.ir.knnbandit.graph.index.Index;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.ranksys.formats.parsing.Parser;
import org.ranksys.formats.parsing.Parsers;

/**
 * Reads a graph from a file.
 * 
 * The file data format is the following:
 * 
 * nodeA nodeB (weight) (types)
 * 
 * where the weight must appear only if the graph is weighted. Otherwise, this
 * column will be ignored. The type must only appear if it is going to be read. In
 * that case, the fourth column will be read. otherwise, it will be ignored. A weight
 * column must exist in case the types appear, but it can be empty.
 * Every column apart from these ones will be ignored when loading
 * the graph. Columns are separated by a certain delimiter. By default, this delimiter
 * is a tab space.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <V> The type of the vertices.
 */
public class TextGraphReader<V> implements GraphReader<V>
{
    /**
     * Indicates if the graph to read is directed (true) or not (false).
     */
    private final boolean directed;
    /**
     * Indicates if the graph to read is weighted (true) or not (false).
     */
    private final boolean weighted;
    /**
     * Indicates if the graph to read allows autoloops (true) or not (false).
     */    
    private final boolean selfloops;
    /**
     * Parser for reading the vertices.
     */
    private final Parser<V> uParser;
    /**
     * Field delimiter.
     */
    private final String delimiter;
    
    /**
     * Constructor.
     * @param directed Indicates if the graph to read is directed (true) or not (false).
     * @param weighted Indicates if the graph to read is weighted (true) or not (false).
     * @param selfloops Indicates if the graph to read allows autoloops (true) or not (false).
     * @param delimiter Field delimiter.
     * @param uParser Parser for reading the vertices.
     */
    public TextGraphReader(boolean directed, boolean weighted, boolean selfloops, String delimiter, Parser<V> uParser)
    {
        this.directed = directed;
        this.weighted = weighted;
        this.selfloops = selfloops;
        this.delimiter = delimiter;
        this.uParser = uParser;
    }

    @Override
    public Graph<V> read(String file)
    {
        try
        {
            InputStream ios = new FileInputStream(file);
            return this.read(ios, true, false);
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
            return null;
        }
    }

    @Override
    public Graph<V> read(String file, boolean readWeights, boolean readTypes)
    {
        try
        {
            InputStream ios = new FileInputStream(file);
            return this.read(ios, readWeights, readTypes);
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
            return null;
        }    
    }

    @Override
    public Graph<V> read(InputStream stream)
    {
        return this.read(stream, true, false);
    }

    @Override
    public Graph<V> read(InputStream stream, boolean readWeights, boolean readTypes)
    {
        try{
            GraphGenerator<V> gg = new EmptyGraphGenerator<>();

            EmptyGraphGenerator<V> empty = (EmptyGraphGenerator<V>) gg;
            empty.configure(directed, weighted);

            Graph<V> graph = gg.generate();
            
            try(BufferedReader br = new BufferedReader(new InputStreamReader(stream)))
            {
                br.lines().forEach(line -> {
                    String[] splits = line.split(delimiter);
                    V source = uParser.parse(splits[0]);
                    V dest = uParser.parse(splits[1]);
                    
                    if(!source.equals(dest) || selfloops)
                    {
                        double weight = 1.0;
                        int type = 0;
                        if(weighted)
                        {
                            weight = Parsers.dp.parse(splits[2]);
                        }
                        
                        if(readTypes && weighted)
                        {
                            type = Parsers.ip.parse(splits[3]);
                        }
                        else if(readTypes)
                        {
                            type = Parsers.ip.parse(splits[2]);
                        }
                        
                        graph.addEdge(source, dest, weight, type, true);
                    }
                });
            }
            
            catch(IOException ioe)
            {
                ioe.printStackTrace();
                return null;
            }
            
            return graph;
        }
        
        catch(GeneratorNotConfiguredException | GeneratorBadConfiguredException ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public Graph<V> read(String file, boolean readWeights, boolean readTypes, Index<V> nodes)
    {
        try
        {
            InputStream ios = new FileInputStream(file);
            return this.read(ios, readWeights, readTypes, nodes);
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
            return null;
        }    
    }

    @Override
    public Graph<V> read(InputStream stream, boolean readWeights, boolean readTypes, Index<V> nodes)
    {
        try
        {
            GraphGenerator<V> gg = new EmptyGraphGenerator<>();

            EmptyGraphGenerator<V> empty = (EmptyGraphGenerator<V>) gg;
            empty.configure(directed, weighted);

            Graph<V> graph = gg.generate();
            
            nodes.getAllObjectsIds().sorted().forEach(i -> graph.addNode(nodes.idx2object(i)));
            
            try(BufferedReader br = new BufferedReader(new InputStreamReader(stream)))
            {
                br.lines().forEach(line -> {
                    String[] splits = line.split(delimiter);
                    V source = uParser.parse(splits[0]);
                    V dest = uParser.parse(splits[1]);
                    
                    if(!source.equals(dest) || selfloops)
                    {
                        double weight = 1.0;
                        int type = 0;
                        if(weighted)
                        {
                            weight = Parsers.dp.parse(splits[2]);
                        }
                        
                        if(readTypes && weighted)
                        {
                            type = Parsers.ip.parse(splits[3]);
                        }
                        else if(readTypes)
                        {
                            type = Parsers.ip.parse(splits[2]);
                        }
                        
                        graph.addEdge(source, dest, weight, type, false);
                    }
                });
            }
            
            catch(IOException ioe)
            {
                ioe.printStackTrace();
                return null;
            }
            
            return graph;
        }
        
        catch(GeneratorNotConfiguredException | GeneratorBadConfiguredException ex)
        {
            ex.printStackTrace();
            return null;
        }
    }
}
