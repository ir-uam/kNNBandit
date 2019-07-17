/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0.
 * 
 */
package es.uam.eps.ir.knnbandit;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * Main class for running experiments.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class Main
{
    /**
     * Name for general recommendation.
     */
    private final static String GENERAL = "generalrec";
    /**
     * Name for contact recommendation.
     */
    private final static String CONTACT = "contactrec";

    /**
     * Main method. Executes the main method in the class specified by the first
     * argument with the rest of run time arguments.
     *
     * @param args Arguments to select the class to run and arguments for its main method
     *
     */
    public static void main(String[] args)
    {
        try
        {
            String main = args[0];
            String className;
            switch(main)
            {
                case GENERAL:
                    className = "es.uam.eps.ir.knnbandit.InteractiveRecommendation";
                    break;
                case CONTACT:
                    className = "es.uam.eps.ir.knnbandit.InteractiveContactRecommendation";
                    break;
                default:
                    System.err.println("ERROR: Invalid configuration.");
                    return;
            }

            String[] executionArgs = Arrays.copyOfRange(args, 1, args.length);
            Class[] argTypes = {executionArgs.getClass()};
            Object[] passedArgs = {executionArgs};
            Class.forName(className).getMethod("main", argTypes).invoke(null, passedArgs);
        }
        catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex)
        {
            System.err.println("The run time arguments were not correct");
            ex.printStackTrace();
        }
    }
}
