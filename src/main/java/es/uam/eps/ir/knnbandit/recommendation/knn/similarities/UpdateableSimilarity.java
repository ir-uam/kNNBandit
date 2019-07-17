/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0.
 * 
 */
package es.uam.eps.ir.knnbandit.recommendation.knn.similarities;

import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.nn.sim.Similarity;

/**
 * Updateable version of similarity.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public interface UpdateableSimilarity extends Similarity
{
    /**
     * Updates the similarity between two users.
     * @param uidx Identifier of the first user.
     * @param vidx Identifier of the second user.
     * @param iidx Identifier of the item.
     * @param uval Rating of the first user for the item.
     * @param vval Rating of the second user for the item.
     */
    public void update(int uidx, int vidx, int iidx, double uval, double vval);

    /**
     * Updates the similarity.
     * @param prefData Preference data.
     */
    public void update(FastPreferenceData<?, ?> prefData);
}
