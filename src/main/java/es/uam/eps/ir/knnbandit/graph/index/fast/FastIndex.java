/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0.
 * 
 */
package es.uam.eps.ir.knnbandit.graph.index.fast;

import es.uam.eps.ir.knnbandit.graph.index.Index;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Fast implementation of a generic index.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <T> Type of the objects.
 */
public class FastIndex<T> implements Index<T>
{
    /**
     * Integer value for every object.
     */
    private final Object2IntMap<T> t2imap;
    /**
     * Object for each integer value.
     */
    private final List<T> i2tmap;
    /**
     * Number of objects.
     */
    private int numObjects;
    
    /**
     * Constructor.
     */
    public FastIndex()
    {
        t2imap = new Object2IntOpenHashMap<>();
        t2imap.defaultReturnValue(-1);
        i2tmap = new ArrayList<>();
    }
    
    @Override
    public boolean containsObject(T i)
    {
        return t2imap.containsKey(i);
    }

    @Override
    public int numObjects()
    {
        return this.numObjects;
    }

    @Override
    public Stream<T> getAllObjects()
    {
        return i2tmap.stream();
    }

    @Override
    public int object2idx(T i)
    {
        return t2imap.getInt(i);
    }

    @Override
    public T idx2object(int idx)
    {
        if(idx > i2tmap.size() || idx < 0)
            return null;
        return i2tmap.get(idx);
    }

    @Override
    public int addObject(T t)
    {
        int idx = t2imap.getInt(t);
        if(idx == t2imap.defaultReturnValue())
        {
            idx = t2imap.size();
            t2imap.put(t, idx);
            i2tmap.add(t);
            ++this.numObjects;
        }
        return idx;
    }

    @Override
    public int removeObject(T t)
    {
        int idx = t2imap.getInt(t);
        if(idx == -1) return -1;
        for(int i = idx + 1; i < this.numObjects; ++i)
        {
            T aux = i2tmap.get(i);
            t2imap.put(aux, t2imap.get(aux)-1);
        }
        t2imap.remove(t);
        i2tmap.remove(idx);
        this.numObjects--;
        return idx;
    }
    
    @Override
    public IntStream getAllObjectsIds()
    {
        return IntStream.range(0, this.numObjects());
    }    
}
