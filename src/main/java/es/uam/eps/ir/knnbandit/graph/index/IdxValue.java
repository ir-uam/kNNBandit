/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0.
 * 
 */
package es.uam.eps.ir.knnbandit.graph.index;

import java.io.Serializable;

/**
 * Class for expressing weights.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <W> Type of the weights.
 */
public class IdxValue<W> implements Comparable<IdxValue>, Serializable, Cloneable
{
    /**
     * Identifier.
     */
    private final int idx;
    /**
     * Value of the weight.
     */
    private final W value;
    
    /**
     * Constructor.
     * @param idx Identifier.
     * @param value Value of the weight.
     */
    public IdxValue(int idx, W value)
    {
        this.idx = idx;
        this.value = value;
    }

    @Override
    public int compareTo(IdxValue t)
    {
        return this.idx - t.idx;
    }
    
    /**
     * Gets the identifier.
     * @return the identifier.
     */
    public int getIdx() { return this.idx; }
    
    /**
     * Gets the value of the weight.
     * @return the value of the weight.
     */
    public W getValue() { return this.value; }
    
}
