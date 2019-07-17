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

import es.uam.eps.ir.ranksys.fast.index.SimpleFastFeatureIndex;
import java.util.stream.Stream;

/**
 * Simple implementation of FastUpdateableFeatureIndex backed by a bi-map IdxIndex.
 *
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <F> Feature type.
 */
public class SimpleFastUpdateableFeatureIndex<F> extends SimpleFastFeatureIndex<F> implements FastUpdateableFeatureIndex<F> 
{
    @Override
    public int addFeature(F f)
    {
        return this.add(f);
    }
    
    /**
     * Creates a feature index from a stream of feature objects.
     *
     * @param <F> Feature type.
     * @param features Stream of feature objects.
     * @return a fast feature index.
     */
    public static <F> SimpleFastUpdateableFeatureIndex<F> load(Stream<F> features) {
        SimpleFastUpdateableFeatureIndex<F> featureIndex = new SimpleFastUpdateableFeatureIndex<>();
        features.forEach(featureIndex::add);
        return featureIndex;
    }
}
