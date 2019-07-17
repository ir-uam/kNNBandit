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

import es.uam.eps.ir.knnbandit.recommendation.knn.similarities.UpdateableSimilarity;
import java.util.function.IntToDoubleFunction;
import java.util.stream.Stream;
import org.ranksys.core.util.tuples.Tuple2id;

/**
 * Stochastic similarity.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public interface StochasticUpdateableSimilarity extends UpdateableSimilarity
{
    /**
     * Obtains a function that finds the exact similarities (without stochastic calculations) between an element and the rest of them.
     * @param idx The identifier of the element.
     * @return the function for obtaining similarities with the rest of elements.
     */
    public IntToDoubleFunction exactSimilarity(int idx);

    /**
     * Obtains the exact similarity (without stochastic calculations) between two elements.
     * @param idx Identifier of the first element.
     * @param idx2 Identifier of the second element.
     * @return the similarity.
     */
    public default double exactSimilarity(int idx, int idx2)
    {
        return exactSimilarity(idx).applyAsDouble(idx2);
    }

    /**
     * Obtains similar items (without stochastic calculations) for an element.
     * @param idx The identifier of the element.
     * @return an stream containing the set of similar elements and their exact similarity value.
     */
    public Stream<Tuple2id> exactSimilarElems(int idx);
}
