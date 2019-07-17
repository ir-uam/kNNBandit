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

import es.uam.eps.ir.knnbandit.data.preference.index.fast.FastUpdateableItemIndex;
import es.uam.eps.ir.knnbandit.data.preference.index.fast.FastUpdateableUserIndex;
import es.uam.eps.ir.knnbandit.data.preference.index.fast.SimpleFastUpdateableItemIndex;
import es.uam.eps.ir.knnbandit.data.preference.index.fast.SimpleFastUpdateableUserIndex;
import es.uam.eps.ir.knnbandit.selector.AlgorithmSelector;
import es.uam.eps.ir.knnbandit.selector.UnconfiguredException;
import es.uam.eps.ir.knnbandit.metrics.CumulativeGini;
import es.uam.eps.ir.knnbandit.metrics.CumulativeRecall;
import es.uam.eps.ir.knnbandit.metrics.CumulativeMetric;
import es.uam.eps.ir.knnbandit.recommendation.RecommendationLoop;
import es.uam.eps.ir.knnbandit.recommendation.InteractiveRecommender;
import es.uam.eps.ir.ranksys.fast.preference.SimpleFastPreferenceData;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.DoublePredicate;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;
import org.ranksys.formats.parsing.Parsers;

/**
 * Class for executing recommender systems in simulated interactive loops.
 * @author Javier Sanz-Cruzado Puig (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class InteractiveRecommendation
{
    /**
     * Executes recommendation algorithms in simulated interactive loops.
     * @param args Execution arguments:
     * <ol>
     *     <li>Algorithms: configuration file for the algorithms</li>
     *     <li>Input: preference data</li>
     *     <li>Output: folder in which to store the output</li>
     *     <li>Num. Iter: number of iterations. 0 if we want to apply until full coverage.</li>
     *     <li>Threshold: relevance threshold</li>
     *     <li>Resume: true if we want to retrieve data from previous executions, false to overwrite</li>
     *     <li>Use ratings: true if we want to use ratings, false for binary values</li>
     * </ol>
     * @throws IOException if something fails while reading / writing.
     * @throws UnconfiguredException if something fails while retrieving the algorithms.
     */
    public static void main(String[] args) throws IOException, UnconfiguredException
    {
        if(args.length < 7)
        {
            System.err.println("ERROR: Invalid arguments");
            System.err.println("Usage:");
            System.err.println("\tAlgorithms: recommender systems list");
            System.err.println("\tInputp Preference data input");
            System.err.println("\tOutput: folder in which to store the output");
            System.err.println("\tNum. Iter.: number of iterations. 0 if we want to run until we run out of recommendable items");
            System.err.println("\tThreshold: relevance threshold");
            System.err.println("\tresume: true if we want to resume previous executions, false if we want to overwrite");
            System.err.println("\tUse ratings: true if we want to take the true value of the ratings, false if we want to use binary values");
            return;
        }

        // First, read the program argumentsº.
        String algorithms = args[0];
        String input = args[1];
        String output = args[2];
        int numIter = Parsers.ip.parse(args[3]);
        double threshold = Parsers.dp.parse(args[4]);
        boolean resume = args[5].equalsIgnoreCase("true");
        boolean useRatings = args[6].equalsIgnoreCase("true");

        DoubleUnaryOperator weightFunction = useRatings ? (double x) -> x :
                                                           (double x) -> (x >= threshold ? 1.0 : 0.0);
        DoublePredicate relevance = useRatings ? (double x) -> (x >= threshold) : (double x) -> (x > 0.0);
        
        // First, we identify and find the random seed which will be used for unties.
        // This is stored in a file in the output folder named "rngseed". If it does not exist,
        // the number is created.
        if(resume)
        {
            File f = new File(output + "rngseed");
            if(f.exists())
            {
                try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f))))
                {
                    UntieRandomNumber.RNG = Parsers.ip.parse(br.readLine());
                }
            }
            else
            {
                Random rng = new Random();
                UntieRandomNumber.RNG = rng.nextInt();
            }
        }
        else
        {
            Random rng = new Random();
            UntieRandomNumber.RNG = rng.nextInt();
        }

        try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output + "rngseed"))))
        {
            bw.write("" + UntieRandomNumber.RNG);
        }
        
        // Then, we read the ratings.
        Set<Long> users = new HashSet<>();
        Set<Long> items = new HashSet<>();
        List<Tuple3<Long,Long,Double>> triplets = new ArrayList<>();
        int numrel = 0;
        
        try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(input))))
        {
            String line;
            while((line = br.readLine()) != null)
            {
                String split[] = line.split("\t");
                Long user = Parsers.lp.parse(split[0]);
                Long item = Parsers.lp.parse(split[1]);
                double val = Parsers.dp.parse(split[2]);
                
                users.add(user);
                items.add(item);
                
                double rating = weightFunction.applyAsDouble(val);
                if(relevance.test(rating)) numrel++;
                
                triplets.add(new Tuple3<>(user, item, rating));            
            }
        }
        
        FastUpdateableUserIndex<Long> uIndex = SimpleFastUpdateableUserIndex.load(users.stream());
        FastUpdateableItemIndex<Long> iIndex = SimpleFastUpdateableItemIndex.load(items.stream());
        
        SimpleFastPreferenceData<Long, Long> prefData = SimpleFastPreferenceData.load(triplets.stream(), uIndex, iIndex);

        System.out.println("USers: " + uIndex.numUsers());
        System.out.println("Items: " + iIndex.numItems());
        int numRel = numrel;

        // Initialize the metrics to compute.
        Map<String, Supplier<CumulativeMetric<Long,Long>>> metrics = new HashMap<>();
        metrics.put("recall", () -> new CumulativeRecall(prefData, numRel, 0.5));
        metrics.put("gini", () -> new CumulativeGini(items.size()));
        List<String> metricNames = new ArrayList<>(metrics.keySet());

        // Select the algorithms.
        long a = System.currentTimeMillis();
        AlgorithmSelector<Long, Long> algorithmSelector = new AlgorithmSelector<>();
        algorithmSelector.configure(uIndex, iIndex, prefData, useRatings ? threshold : 0.5);
        algorithmSelector.addFile(algorithms);
        Map<String, InteractiveRecommender<Long,Long>> recs = algorithmSelector.getRecs();
        long b = System.currentTimeMillis();
        
        System.out.println("Recommenders ready (" + (b-a) + " ms.)");
        recs.entrySet().parallelStream().forEach(re -> 
        {
            InteractiveRecommender<Long,Long> rec = re.getValue();
            Map<String, CumulativeMetric<Long,Long>> localMetrics = new HashMap<>();
            metricNames.forEach(name -> localMetrics.put(name, metrics.get(name).get()));
            RecommendationLoop<Long, Long> loop = new RecommendationLoop<>(uIndex, iIndex, rec, localMetrics, numIter,0);

            List<Tuple3<Long,Long,Long>> list = new ArrayList<>();
            String fileName = output + re.getKey() + ".txt";

            if(resume)
            {
                File f = new File(fileName);
                if(f.exists()) // if the file exists, then resume:
                {
                    try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName))))
                    {
                        String line = br.readLine();
                        int len;
                        if(line != null)
                        {
                            String[] split = line.split("\t");
                            len = split.length;

                            while((line = br.readLine()) != null)
                            {
                                split = line.split("\t");
                                if(split.length < len) break;

                                long u = Parsers.lp.parse(split[1]);
                                long i = Parsers.lp.parse(split[2]);
                                long time = Parsers.lp.parse(split[len-1]);
                                list.add(new Tuple3<>(u, i, time));
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        Logger.getLogger(InteractiveRecommendation.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output + re.getKey() + ".txt"))))
            {
                if(resume && !list.isEmpty())
                {
                    for(Tuple3<Long,Long,Long> triplet : list)
                    {
                        StringBuilder builder = new StringBuilder();
                        loop.update(new Tuple2<>(triplet.v1, triplet.v2));
                        int iter = loop.getCurrentIteration();
                        builder.append(iter);
                        builder.append("\t");
                        builder.append(triplet.v1);
                        builder.append("\t");
                        builder.append(triplet.v2);
                        Map<String, Double> metricVals = loop.getMetrics();
                        for(String name : metricNames)
                        {
                            builder.append("\t");
                            builder.append(metricVals.get(name));
                        }
                        builder.append("\t");
                        builder.append(triplet.v3);
                        builder.append("\n");
                        bw.write(builder.toString());
                    }
                }

                while(!loop.hasEnded())
                {
                    StringBuilder builder = new StringBuilder();
                    long aa = System.currentTimeMillis();
                    Tuple2<Long,Long> tuple = loop.nextIteration();
                    long bb = System.currentTimeMillis();
                    if(tuple == null) break; // The loop has finished
                    int iter = loop.getCurrentIteration();
                    builder.append(iter);
                    builder.append("\t");
                    builder.append(tuple.v1);
                    builder.append("\t");
                    builder.append(tuple.v2);
                    Map<String, Double> metricVals = loop.getMetrics();
                    for(String name : metricNames)
                    {
                        builder.append("\t");
                        builder.append(metricVals.get(name));
                    }
                    builder.append("\t");
                    builder.append((bb-aa));
                    builder.append("\n");
                    bw.write(builder.toString());
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        });
    }
}
