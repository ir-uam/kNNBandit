/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0.
 * 
 */
package es.uam.eps.ir.knnbandit.graph;


/**
 * Class for expressing weights.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <I> The type of the identifier.
 * @param <W> The type of the weight.
 */
public class Weight<I, W>
{
    /**
     * Identifier of the weight.
     */
    private final I idx;
    /**
     * Value of the weight.
     */
    private final W weight;
    
    /**
     * Constructor.
     * @param idx Identifier.
     * @param weight Value.
     */
    public Weight(I idx, W weight)
    {
        this.idx = idx;
        this.weight = weight;
    }
    
    /**
     * Gets the identifier of the weight.
     * @return the identifier of the weight.
     */
    public I getIdx() { return this.idx; }
    
    /**
     * Gets the value of the weight.
     * @return the value of the weight.
     */
    public W getValue() { return this.weight; }
    
}
