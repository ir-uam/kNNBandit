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

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Bi-map-like structure to back fast version of indexes. It keeps to maps: id-to-index and index-to-id.
 * Value of indexes go from 0 (included) to the number of elements (excluded).
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @param <T> Type of the element.
 */
public class GenericIndex<T> implements Serializable 
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
     * Constructor.
     */
    public GenericIndex()
    {
        t2imap = new Object2IntOpenHashMap<>();
        t2imap.defaultReturnValue(-1);
        i2tmap = new ArrayList<>();
    }
    
    /**
     * Adds an element to the structure, if it is not in the 
     * @param t The element to add.
     * @return the index for the corresponding element.
     */
    public int add(T t)
    {
        int idx = t2imap.getInt(t);
        if(idx == t2imap.defaultReturnValue())
        {
            idx = t2imap.size();
            t2imap.put(t, idx);
            i2tmap.add(t);
        }
        return idx;
    }
    
    /**
     * Gets the index of the object.
     * @param t The object.
     * @return the index of the object if it exists, -1 if not.
     */
    public int getIdx(T t)
    {
        return t2imap.getInt(t);
    }
    
    /**
     * Gets the object corresponding to the given index.
     * @param idx The index.
     * @return the object in that index.
     */
    public T getObject(int idx)
    {
        return i2tmap.get(idx);
    }
    
    /**
     * Checks if the corresponding object is included in the index.
     * @param t The object.
     * @return true if it exists, false if not.
     */
    public boolean containsId(T t)
    {
        return t2imap.containsKey(t);
    }
    
    /**
     * Gets the size of the index,
     * @return the size of the index.
     */
    public int size()
    {
        return t2imap.size();
    }
    
    /**
     * Gets a stream of objects in the index
     * @return the stream.
     */
    public Stream<T> getIds()
    {
        return t2imap.keySet().stream();
    }
}
