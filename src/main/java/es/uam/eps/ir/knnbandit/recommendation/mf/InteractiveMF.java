/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0.
 * 
 */
package es.uam.eps.ir.knnbandit.recommendation.mf;

import cern.colt.matrix.DoubleMatrix1D;
import es.uam.eps.ir.knnbandit.data.preference.index.fast.FastUpdateableItemIndex;
import es.uam.eps.ir.knnbandit.data.preference.index.fast.FastUpdateableUserIndex;
import es.uam.eps.ir.knnbandit.recommendation.InteractiveRecommender;
import es.uam.eps.ir.ranksys.fast.preference.SimpleFastPreferenceData;
import es.uam.eps.ir.ranksys.mf.Factorization;
import es.uam.eps.ir.ranksys.mf.Factorizer;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import org.jooq.lambda.tuple.Tuple3;

/**
 * Interactive version of matrix factorization algorithms.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <U> User type.
 * @param <I> Item type.
 */
public class InteractiveMF<U,I> extends InteractiveRecommender<U,I>
{
    /**
     * Factorizer for obtaining the factorized matrices.
     */
    private final Factorizer<U,I> factorizer;
    /**
     * Number of latent factors to use.
     */
    private final int k;
    /**
     * Decomposition in different matrices.
     */
    private Factorization<U,I> factorization;
    /**
     * Number of hits before the recommender is updated.
     */
    private final static int LIMITCOUNTER = 100;
    /**
     * Current hit counter.
     */
    private int currentCounter = 0;

    /**
     * Constructor.
     * @param uIndex User index.
     * @param iIndex Item index.
     * @param prefData Preference data.
     * @param ignoreUnknown True if we must ignore unknown items when updating.
     * @param k Number of neighbors to use.
     * @param factorizer Factorizer for obtaining the factorized matrices.
     */
    public InteractiveMF(FastUpdateableUserIndex<U> uIndex, FastUpdateableItemIndex<I> iIndex, SimpleFastPreferenceData<U, I> prefData, boolean ignoreUnknown, int k, Factorizer<U,I> factorizer)
    {
        super(uIndex, iIndex, prefData, ignoreUnknown);
        this.factorizer = factorizer;
        this.k = (k > 0) ? k : prefData.numUsers();
        Enumeration<String> loggers = LogManager.getLogManager().getLoggerNames();
        while (loggers.hasMoreElements()) LogManager.getLogManager().getLogger(loggers.nextElement()).setLevel(Level.OFF);
        this.factorization = factorizer.factorize(k, trainData);
    }
    
    /**
     * Constructor.
     * @param uIndex User index.
     * @param iIndex Item index.
     * @param prefData Preference data.
     * @param ignoreUnknown True if we must ignore unknown items when updating.
     * @param k Number of neighbors to use.
     * @param factorizer Factorizer for obtaining the factorized matrices.
     */
    public InteractiveMF(FastUpdateableUserIndex<U> uIndex, FastUpdateableItemIndex<I> iIndex, SimpleFastPreferenceData<U, I> prefData, boolean ignoreUnknown, boolean notReciprocal, int k, Factorizer<U,I> factorizer)
    {
        super(uIndex, iIndex, prefData, ignoreUnknown, notReciprocal);
        this.factorizer = factorizer;
        this.k = (k > 0) ? k : prefData.numUsers();
        Enumeration<String> loggers = LogManager.getLogManager().getLoggerNames();
        while (loggers.hasMoreElements()) LogManager.getLogManager().getLogger(loggers.nextElement()).setLevel(Level.OFF);
        this.factorization = factorizer.factorize(k, trainData);
    }
    
    @Override
    public int next(int uidx)
    {
        IntList list = this.availability.get(uidx);
        if(list == null || list.isEmpty()) return -1;
                
        DoubleMatrix1D pu = factorization.getUserVector(prefData.uidx2user(uidx));
        if(pu == null)
        {
            return list.get(rng.nextInt(list.size()));
        }
        
        DoubleMatrix1D r = factorization.getItemMatrix().zMult(pu,null);
        double max = Double.NEGATIVE_INFINITY;
        IntList top = new IntArrayList();
        
        for(int iidx : list)
        {
            double val = r.getQuick(iidx);
            if(Double.isNaN(val)) val = Double.NEGATIVE_INFINITY;
            if(top.isEmpty() || max < val)
            {
                top = new IntArrayList();
                top.add(iidx);
                max = val;
            }
            else if(max == val)
            {
                top.add(iidx);
            }
        }
       
        int topSize = top.size();
        if(topSize == 1) return top.get(0);
        else return top.get(rng.nextInt(top.size()));
    }

    @Override
    public void updateMethod(List<Tuple3<Integer,Integer,Double>> tuples)
    {
        this.factorizer.factorize(k, trainData);
        this.currentCounter = 0;
    }
    
    @Override
    public void updateMethod(int uidx, int iidx, double value)
    {
        if(value > 0.0)
            this.currentCounter++;
        if(currentCounter >= LIMITCOUNTER)
        {
            this.currentCounter = 0;
            this.factorization = factorizer.factorize(k, trainData);
        }
    }
}
