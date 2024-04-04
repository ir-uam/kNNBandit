/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0.
 * 
 */
package es.uam.eps.ir.knnbandit.selector;

import es.uam.eps.ir.knnbandit.data.preference.index.fast.FastUpdateableItemIndex;
import es.uam.eps.ir.knnbandit.data.preference.index.fast.FastUpdateableUserIndex;
import es.uam.eps.ir.knnbandit.recommendation.InteractiveRecommender;
import es.uam.eps.ir.knnbandit.recommendation.bandits.ItemBanditRecommender;
import es.uam.eps.ir.knnbandit.recommendation.bandits.functions.ValueFunction;
import es.uam.eps.ir.knnbandit.recommendation.bandits.functions.ValueFunctions;
import es.uam.eps.ir.knnbandit.recommendation.bandits.item.*;
import es.uam.eps.ir.knnbandit.recommendation.basic.*;
import es.uam.eps.ir.knnbandit.recommendation.knn.similarities.UpdateableSimilarity;
import es.uam.eps.ir.knnbandit.recommendation.knn.similarities.VectorCosineSimilarity;
import es.uam.eps.ir.knnbandit.recommendation.knn.similarities.stochastic.BetaStochasticSimilarity;
import es.uam.eps.ir.knnbandit.recommendation.knn.user.InteractiveUserBasedKNN;
import es.uam.eps.ir.knnbandit.recommendation.mf.InteractiveMF;
import es.uam.eps.ir.knnbandit.recommendation.mf.PZTFactorizer;
import es.uam.eps.ir.ranksys.fast.preference.SimpleFastPreferenceData;
import es.uam.eps.ir.ranksys.mf.Factorizer;
import es.uam.eps.ir.ranksys.mf.als.HKVFactorizer;
import es.uam.eps.ir.ranksys.mf.plsa.PLSAFactorizer;
import org.ranksys.formats.parsing.Parsers;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;

/**
 * Class for selecting the interactive recommendation algorithms to apply in an experiments. The class encapsulates the set
 * of algorithms, as well as some experiment settings.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <U> User type.
 * @param <I> Item type.
 */
public class AlgorithmSelector<U,I>
{
    /**
     * A map of recommenders to apply.
     */
    private final Map<String, InteractiveRecommender<U,I>> recs;
    /**
     * A cursor for reading the line configuration.
     */
    private int cursor;
    /**
     * Indicates if the selector has been previously configured.
     */
    private boolean configured;
    /**
     * User index.
     */
    private FastUpdateableUserIndex<U> uIndex;
    /**
     * Item index.
     */
    private FastUpdateableItemIndex<I> iIndex;
    /**
     * Preference data.
     */
    private SimpleFastPreferenceData<U,I> prefData;
    /**
     * True if contact recommendation algorithms must be configured, false otherwise.
     */
    private boolean contactRec;
    /**
     * True if reciprocal links should not be recommended, false otherwise.
     */
    private boolean notReciprocal;
    /**
     * Relevance threshold.
     */
    private double threshold;
    /**
     * Constructor.
     */
    public AlgorithmSelector()
    {
        recs = new HashMap<>();
        notReciprocal = false;
    }
    
    /**
     * Resets the selection.
     */
    public void reset()
    {
        this.recs.clear();
        this.uIndex = null;
        this.iIndex = null;
        this.prefData = null;
        this.contactRec = false;
        this.notReciprocal = false;
        this.configured = false;
    }
    
    /**
     * Configures the experiment.
     * @param uIndex User index.
     * @param iIndex Item index.
     * @param prefData Preference data.
     * @param threshold Relevance threshold
     */
    public void configure(FastUpdateableUserIndex<U> uIndex, FastUpdateableItemIndex<I> iIndex, SimpleFastPreferenceData<U,I> prefData, double threshold)
    {
        this.uIndex = uIndex;
        this.iIndex = iIndex;
        this.prefData = prefData;
        this.notReciprocal = false;
        this.contactRec = false;
        this.threshold = threshold;        
        this.configured = true;
    }   
    
    /**
     * Configures the experiment.
     * @param uIndex User index.
     * @param iIndex Item index.
     * @param prefData Preference data.
     * @param threshold Relevance threshold
     * @param notReciprocal True if we have to avoid recommending reciprocal items.
     */
    public void configure(FastUpdateableUserIndex<U> uIndex, FastUpdateableItemIndex<I> iIndex, SimpleFastPreferenceData<U,I> prefData, double threshold, boolean notReciprocal)
    {
        this.uIndex = uIndex;
        this.iIndex = iIndex;
        this.prefData = prefData;
        this.notReciprocal = notReciprocal;
        this.contactRec = true;
        this.threshold = threshold;
        this.configured = true;
    }   
    
    /**
     * Given a string containing its configuration, obtains an interactive recommendation algorithm.
     * @param algorithm The string containing the configuration of the algorithm.
     * @return an interactive recommender.
     * @throws es.uam.eps.ir.knnbandit.selector.UnconfiguredException if the experiment is not configured.
     */
    public InteractiveRecommender<U,I> getAlgorithm(String algorithm) throws UnconfiguredException
    {
        if(!this.configured) throw new UnconfiguredException("The experiment is not configured");
        cursor = 0;
        if(!algorithm.startsWith("//")) {
            String[] split = algorithm.split("-");
            List<String> fullAlgorithm = new ArrayList<>(Arrays.asList(split));
            boolean ignoreUnknown;
            boolean unknownAlgorithm = false;
            switch (fullAlgorithm.get(0))
            {
                case AlgorithmIdentifiers.RANDOM: // Random recommendation.
                    cursor++;
                    return !this.contactRec ? new RandomRecommender(uIndex, iIndex, prefData, true)
                            : new RandomRecommender(uIndex, iIndex, prefData, true, notReciprocal);

                case AlgorithmIdentifiers.AVG: // Average rating recommendation.
                    cursor++;
                    if (fullAlgorithm.size() == cursor)
                        ignoreUnknown = false;
                    else
                        ignoreUnknown = fullAlgorithm.get(cursor).equalsIgnoreCase("ignore");
                    return !this.contactRec ? new AvgRecommender(uIndex, iIndex, prefData, ignoreUnknown)
                            : new AvgRecommender(uIndex, iIndex, prefData, ignoreUnknown, notReciprocal);

                case AlgorithmIdentifiers.POP: // Popularity recommendation.
                    cursor++;
                    return !this.contactRec ? new PopularityRecommender(uIndex, iIndex, prefData, true, threshold)
                            : new PopularityRecommender(uIndex, iIndex, prefData, true, threshold, notReciprocal);

                case AlgorithmIdentifiers.ITEMBANDIT: // Non-personalized bandits.
                    cursor++;
                    ItemBandit<U, I> itemBandit = this.getItemBandit(fullAlgorithm.subList(1, split.length), prefData.numItems());
                    if (itemBandit == null)
                    {
                        unknownAlgorithm = true;
                        break;
                    }
                    ValueFunction valFunc = ValueFunctions.identity();

                    if(fullAlgorithm.size() == cursor)
                    {
                        ignoreUnknown = false;
                    }
                    else
                    {
                        ignoreUnknown = fullAlgorithm.get(cursor).equalsIgnoreCase("ignore");
                        cursor++;
                    }

                    return !this.contactRec ? new ItemBanditRecommender(uIndex, iIndex, prefData, ignoreUnknown, itemBandit, valFunc)
                            : new ItemBanditRecommender(uIndex, iIndex, prefData, ignoreUnknown, notReciprocal, itemBandit, valFunc);

                case AlgorithmIdentifiers.USERBASEDKNN: // User-based kNN.
                    cursor++;
                    int k = Parsers.ip.parse(fullAlgorithm.get(cursor));
                    cursor++;

                    UpdateableSimilarity sim = new VectorCosineSimilarity(prefData.numUsers());
                    boolean ignoreZeroes;
                    if (fullAlgorithm.size() == cursor)
                    {
                        ignoreUnknown = true;
                        ignoreZeroes = true;
                    }
                    else if (fullAlgorithm.size() == (cursor + 1))
                    {
                        ignoreUnknown = fullAlgorithm.get(cursor).equalsIgnoreCase("ignore");
                        ignoreZeroes = true;
                        cursor++;
                    }
                    else
                    {
                        ignoreUnknown = fullAlgorithm.get(cursor).equalsIgnoreCase("ignore");
                        ignoreZeroes = fullAlgorithm.get(cursor+1).equalsIgnoreCase("ignore");
                        cursor+=2;
                    }

                    return !this.contactRec ? new InteractiveUserBasedKNN(uIndex, iIndex, prefData, ignoreUnknown, ignoreZeroes, k, sim)
                            : new InteractiveUserBasedKNN(uIndex, iIndex, prefData, ignoreUnknown, ignoreZeroes, notReciprocal, k, sim);

                case AlgorithmIdentifiers.BANDITKNN:
                    cursor++;
                    k = Parsers.ip.parse(fullAlgorithm.get(cursor));
                    cursor++;
                    double alpha = Parsers.dp.parse(fullAlgorithm.get(cursor));
                    cursor++;
                    double beta = Parsers.dp.parse(fullAlgorithm.get(cursor));

                    sim = new BetaStochasticSimilarity(prefData.numUsers(), alpha, beta);

                    if (fullAlgorithm.size() == cursor)
                    {
                        ignoreUnknown = true;
                        ignoreZeroes = true;
                    }
                    else if (fullAlgorithm.size() == (cursor + 1))
                    {
                        ignoreUnknown = fullAlgorithm.get(cursor).equalsIgnoreCase("ignore");
                        ignoreZeroes = true;
                        cursor++;
                    }
                    else
                    {
                        ignoreUnknown = fullAlgorithm.get(cursor).equalsIgnoreCase("ignore");
                        ignoreZeroes = fullAlgorithm.get(cursor+1).equalsIgnoreCase("ignore");
                        cursor+=2;
                    }
                    return !this.contactRec ? new InteractiveUserBasedKNN(uIndex, iIndex, prefData, ignoreUnknown, ignoreZeroes, k, sim)
                            : new InteractiveUserBasedKNN(uIndex, iIndex, prefData, ignoreUnknown, ignoreZeroes, notReciprocal, k, sim);

                case AlgorithmIdentifiers.MF:
                    cursor++;
                    k = new Integer(fullAlgorithm.get(cursor));
                    cursor++;
                    Factorizer<U, I> factorizer = this.getFactorizer(fullAlgorithm.subList(cursor, split.length));
                    if (factorizer == null) {
                        unknownAlgorithm = true;
                        break;
                    }

                    if(fullAlgorithm.size() == cursor)
                    {
                        ignoreUnknown = true;
                    }
                    else
                    {
                        ignoreUnknown = fullAlgorithm.get(cursor).equalsIgnoreCase("ignore");
                        cursor++;
                    }

                    return !this.contactRec ? new InteractiveMF(uIndex, iIndex, prefData, ignoreUnknown, k, factorizer)
                            : new InteractiveMF(uIndex, iIndex, prefData, ignoreUnknown, notReciprocal, k, factorizer);
                default:
                    unknownAlgorithm = true;
            }

            if(unknownAlgorithm) return null;
        }

        return null;
    }

    /**
     * Adds a single algorithm to the selector.
     * @param algorithm The String name of the algorithm.
     * @throws es.uam.eps.ir.knnbandit.selector.UnconfiguredException
     */
    public void addAlgorithm(String algorithm) throws UnconfiguredException
    {
        if(!this.configured) throw new UnconfiguredException("AlgorithmSelector");

        InteractiveRecommender<U,I> rec = this.getAlgorithm(algorithm);
        if(rec != null)
        {
            this.recs.put(algorithm, rec);
        }
    }
    
    /**
     * Adds a set of algorithms.
     * @param file File containing the configuration of the algorithms.
     * @throws IOException
     * @throws es.uam.eps.ir.knnbandit.selector.UnconfiguredException
     */
    public void addFile(String file) throws IOException, UnconfiguredException
    {
        if(!this.configured) throw new UnconfiguredException("AlgorithmSelector");

        try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file))))
        {
            String line;
            while((line = br.readLine()) != null)
            {
                this.addAlgorithm(line);
            }
        }       
    }
    
    /**
     * Obtains the selection of recommenders.
     * @return the selection of recommenders.
     */
    public Map<String, InteractiveRecommender<U,I>> getRecs()
    {
        return this.recs;
    }
    
    /**
     * Get an item bandit.
     * @param split A list containing the configuration.
     * @param numItems The number of items in the system.
     * @return the corresponding item bandit if everything is ok, null otherwise.
     */
    private ItemBandit<U,I> getItemBandit(List<String> split, int numItems)
    {
        ItemBandit<U,I> ib;
        switch(split.get(0))
        {
            case ItemBanditIdentifiers.EGREEDY:
                double epsilon = new Double(split.get(1));
                cursor+=2;
                EpsilonGreedyUpdateFunction updateFunc = this.getUpdateFunction(split.subList(2, split.size()));
                ib = new EpsilonGreedyItemBandit<>(epsilon, numItems, updateFunc);
                break;
            case ItemBanditIdentifiers.UCB1:
                ib = new UCB1ItemBandit(numItems);
                cursor++;
                break;
            case ItemBanditIdentifiers.UCB1TUNED:
                ib = new UCB1TunedItemBandit(numItems);
                cursor++;
                break;
            case ItemBanditIdentifiers.THOMPSON:
                double alpha = new Double(split.get(1));
                double beta = new Double(split.get(2));
                ib = new ThompsonSamplingItemBandit(numItems, alpha, beta);
                cursor+=3;
                break;
            case ItemBanditIdentifiers.ETGREEDY:
                alpha = new Double(split.get(1));
                cursor+=2;
                updateFunc = this.getUpdateFunction(split.subList(2, split.size()));
                ib = new EpsilonTGreedyItemBandit<>(alpha, numItems, updateFunc);
                break;
            default:
                cursor++;
                return null;
        }
        return ib;
    }

    /**
     * Obtains a function to update an Epsilon-greedy algorithm.
     * @param split Strings containing the configuration.
     * @return the update function if everything is OK, null otherwise.
     */
    private EpsilonGreedyUpdateFunction getUpdateFunction(List<String> split)
    {
        switch(split.get(0))
        {
            case EpsilonGreedyUpdateFunctionIdentifiers.STATIONARY:
                cursor++;
                return EpsilonGreedyUpdateFunctions.stationary();
            case EpsilonGreedyUpdateFunctionIdentifiers.NONSTATIONARY:
                cursor++;
                cursor++;
                return EpsilonGreedyUpdateFunctions.nonStationary(new Double(split.get(1)));
            case EpsilonGreedyUpdateFunctionIdentifiers.USEALL:
                cursor++;
                return EpsilonGreedyUpdateFunctions.useall();
            case EpsilonGreedyUpdateFunctionIdentifiers.COUNT:
                cursor++;
                return EpsilonGreedyUpdateFunctions.count();
            default:
                cursor++;
                return null;
        }
    }

    /**
     * Obtains a MF Factorizer.
     * @param split Strings containing the configuration.
     * @return the factorizer if everything is OK, null otherwise.
     */
    private Factorizer<U, I> getFactorizer(List<String> split)
    {
        cursor++;
        Factorizer<U,I> factorizer = null;
        switch(split.get(0))
        {
            case FactorizerIdentifiers.IMF:
                double alphaHKV = Parsers.dp.parse(split.get(1));new Double(split.get(1));
                double lambdaHKV = new Double(split.get(2));
                int numIterHKV = new Integer(split.get(3));
                cursor+=3;
                DoubleUnaryOperator confidence = (double x) -> 1 + alphaHKV*x;
                factorizer = new HKVFactorizer<>(lambdaHKV, confidence, numIterHKV);
                break;
            case FactorizerIdentifiers.FASTIMF:
                double alphaPZT = new Double(split.get(1));
                double lambdaPZT = new Double(split.get(2));
                int numIterpzt = new Integer(split.get(3));
                boolean usesZeroes = split.get(4).equalsIgnoreCase("true");
                cursor+=4;
                confidence = (double x) -> 1 + alphaPZT*x;
                factorizer = new PZTFactorizer<>(lambdaPZT, lambdaPZT, confidence, numIterpzt, usesZeroes);
                break;
            case FactorizerIdentifiers.PLSA:
                int numIterPLSA = new Integer(split.get(1));
                cursor++;
                factorizer = new PLSAFactorizer<>(numIterPLSA);
                break;
            default:
                return null;
        }

        return factorizer;
    }
}
