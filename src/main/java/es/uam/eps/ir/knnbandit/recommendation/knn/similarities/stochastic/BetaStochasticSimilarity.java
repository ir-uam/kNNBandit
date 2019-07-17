/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0.
 * 
 */
package es.uam.eps.ir.knnbandit.recommendation.knn.similarities.stochastic;
import java.util.function.IntToDoubleFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import es.uam.eps.ir.knnbandit.stats.BetaDistribution;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import org.ranksys.core.util.tuples.Tuple2id;

/**
 * Stochastic similarity that uses a Beta distribution to estimate the similarity.
 * @author Javier Sanz-Cruzado Puig (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class BetaStochasticSimilarity implements StochasticUpdateableSimilarity
{
    /**
     * Current similarities (alpha values)
     */
    private final double[][] sims;
    /**
     * Norms.
     */
    private final double[] usercount;
    /**
     * Number of users.
     */
    private final int numUsers;
    /**
     * Initial alpha.
     */
    private final double alpha;
    /**
     * Initial beta
     */
    private final double beta;

    /**
     * Last visited user.
     */
    private int lastu = -1;
    /**
     * Last visited item.
     */
    private int lasti = -1;

    /**
     * Constructor.
     * @param numUsers Number of users.
     * @param alpha The alpha parameter (number of successes + 1).
     * @param beta The beta parameter (number of failures + 1).
     */
    public BetaStochasticSimilarity(int numUsers, double alpha, double beta)
    {
        this.numUsers = numUsers;
        this.sims = new double[numUsers][numUsers];
        this.usercount = new double[numUsers];
        this.alpha = alpha;
        this.beta = beta;
        for(int i = 0; i < numUsers; ++i)
        {
            this.usercount[i] = 0.0;
            for(int j = 0; j < numUsers; ++j)
            {
                this.sims[i][j] = 0.0;
            }
        }
    }

    /**
     * Constructor. Sets alpha and beta to 1.
     * @param numUsers Number of users.
     */
    public BetaStochasticSimilarity(int numUsers)
    {
        this(numUsers, 1,1);
    }

    @Override
    public IntToDoubleFunction exactSimilarity(int idx)
    {
        return (int idx2) -> 
        {
            double auxalpha = this.sims[idx][idx2] + alpha;
            double auxbeta = this.usercount[idx2] + beta;
            return auxalpha/auxbeta;
        };
    }

    @Override
    public Stream<Tuple2id> exactSimilarElems(int idx)
    {
        IntToDoubleFunction sim = this.exactSimilarity(idx);
        return IntStream.range(0, numUsers).filter(i -> i != idx).mapToObj(i -> new Tuple2id(i, sim.applyAsDouble(i))).filter(x -> x.v2 > 0.0);
    }

    @Override
    public void update(int uidx, int vidx, int iidx, double uval, double vval)
    {
        if(!Double.isNaN(vval) && uval*vval > 0)
        {
            sims[uidx][vidx] += 1.0;
            sims[vidx][uidx] += 1.0;
        }
        
        if(lastu != uidx || lasti != iidx)
        {
            lastu = uidx;
            lasti = iidx;
            if(uval > 0) this.usercount[uidx] += 1;
        }
    }

    @Override
    public IntToDoubleFunction similarity(int idx)
    {
        return (int idx2) -> 
        {
            double auxalpha = this.sims[idx][idx2];
            double auxbeta = this.usercount[idx2] - auxalpha;
            return this.betaSample(auxalpha + alpha, auxbeta + beta);
        };
    }

    @Override
    public Stream<Tuple2id> similarElems(int idx)
    {
        IntToDoubleFunction sim = this.similarity(idx);
        return IntStream.range(0, numUsers).filter(i -> i != idx).mapToObj(i -> new Tuple2id(i, sim.applyAsDouble(i))).filter(x -> x.v2 > 0.0);
    }

    /**
     * Samples from a Beta distribution.
     * @param alpha The alpha value of the Beta.
     * @param beta The beta value of the Beta.
     * @return the sampled value.
     */
    public double betaSample(double alpha, double beta)
    {
        BetaDistribution b = new BetaDistribution(alpha, beta);
        return b.sample();
    }
    
    @Override
    public void update(FastPreferenceData<?,?> prefData)
    {
        prefData.getAllUidx().forEach(uidx ->
        {
            prefData.getAllUidx().forEach(vidx ->
            {
                this.sims[uidx][vidx] = 0.0;
            });
        });

        // First, find the norms.
        prefData.getAllUidx().forEach(uidx ->
        {
            this.usercount[uidx] = prefData.getUidxPreferences(uidx).mapToDouble(iidx ->
            {
                prefData.getIidxPreferences(iidx.v1).forEach(vidx ->
                {
                    this.sims[uidx][vidx.v1] += 1.0;
                });
                return 1.0;
            }).sum();
        });
    }
}
