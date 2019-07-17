/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0.
 * 
 */
package es.uam.eps.ir.knnbandit.graph.edges;

import es.uam.eps.ir.knnbandit.graph.index.IdxValue;

/**
 * Class that represents the type of the edges. Each type is represented as
 * an integer. Value 0 is considered a default valid value, and -1 as a default
 * valid value. Every other value has the interpretation the user wants to give
 * it.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class EdgeType extends IdxValue<Integer>
{    
    /**
     * Constructor.
     * @param idx Identifier of the adjacent node.
     * @param value value of the type.
     */
    public EdgeType(int idx, Integer value)
    {
        super(idx, value);
    }
    
    /**
     * Constructor. Sets the type as the default value.
     * @param idx Identifier of the adjacent node.
     */
    public EdgeType(int idx)
    {
        this(idx, getDefaultValue());
    }
    
    /**
     * Default value for the error type.
     * @return The default value for the error type.
     */
    public static int getErrorType()
    {
        return -1;
    }
    
    /**
     * Default valid type value.
     * @return The default valid type value.
     */
    public static int getDefaultValue()
    {
        return 0;
    }
    
}
