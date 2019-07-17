/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0.
 * 
 */
package es.uam.eps.ir.knnbandit.data.preference.index.fast;

import es.uam.eps.ir.knnbandit.data.preference.index.UpdateableFeatureIndex;
import es.uam.eps.ir.ranksys.fast.index.FastFeatureIndex;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Fast and updateable version of a FeatureIndex, where features are internally represented with numerical indices from 0 (inclusive) to the number of indexed features (exclusive).
 *
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <F> Feature type.
 */
public interface FastUpdateableFeatureIndex<F> extends UpdateableFeatureIndex<F>, FastFeatureIndex<F>
{
    @Override
    public default boolean containsFeature(F f) 
    {
        return feature2fidx(f) >= 0;
    }

    @Override
    public default Stream<F> getAllFeatures() 
    {
        return IntStream.range(0, numFeatures()).mapToObj(fidx -> fidx2feature(fidx));
    }
}
