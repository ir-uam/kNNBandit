/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0.
 * 
 */
package es.uam.eps.ir.knnbandit.utils;

import java.util.Objects;

/**
 * Class that represents a pair of objects of different type.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <U> Type of first object.
 * @param <I> Type of second object.
 */
public class Tuple2oo<U,I>
{
    /**
     * First object
     */
    private final U first;
    /**
     * Second object
     */
    private final I second;
    
    /**
     * Constructor.
     * @param first First object.
     * @param second Second object.
     */
    public Tuple2oo (U first, I second)       
    {
        this.first = first;
        this.second = second;
    }
    
    /**
     * Gets the first object of the pair.
     * @return the first object of the pair.
     */
    public U v1()
    {
        return first;
    }
    
    /**
     * Gets the second object of the pair.
     * @return the second object of the pair.
     */
    public I v2()
    {
        return second;
    }
    
    @Override
    public boolean equals(Object u)
    {
        if(u.getClass().equals(this.getClass()))
        {
            Tuple2oo<?,?> pair = (Tuple2oo<?,?>) u;
            return first.equals(pair.first) && second.equals(pair.second);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.first);
        hash = 83 * hash + Objects.hashCode(this.second);
        return hash;
    }
}


