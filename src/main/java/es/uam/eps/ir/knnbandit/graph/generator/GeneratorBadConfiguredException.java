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

/**
 * Exception for bad configured generators.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class GeneratorBadConfiguredException extends Exception
{
    /**
     * Constructs a GeneratorBadConfiguredException with the given detail message.
     * @param message The detail message of the GeneratorBadConfiguredException.
     */
    public GeneratorBadConfiguredException(String message) 
    {
        super(message);
    }

    /**
     * Constructs a GeneratorBadConfiguredException with the given root cause.
     * @param cause The root cause of the GeneratorBadConfiguredException.
     */
    public GeneratorBadConfiguredException(Throwable cause) 
    {
        super(cause);
    }

    /**
     * Constructs a GeneratorBadConfiguredException with the given detail message and root cause.
     * @param message The detail message of the GeneratorBadConfiguredException.
     * @param cause The root cause of the GeneratorBadConfiguredException.
     */
    public GeneratorBadConfiguredException(String message, Throwable cause) 
    {
        super(message, cause);
    }
}
