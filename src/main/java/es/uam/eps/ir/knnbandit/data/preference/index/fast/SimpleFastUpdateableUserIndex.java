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

import es.uam.eps.ir.ranksys.fast.index.SimpleFastUserIndex;
import java.util.stream.Stream;

/**
 * Simple implementation of FastUpdateableUserIndex backed by a bi-map IdxIndex.
 *
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <U> User type.
 */
public class SimpleFastUpdateableUserIndex<U> extends SimpleFastUserIndex<U> implements FastUpdateableUserIndex<U> 
{
    @Override
    public int addUser(U u) 
    {
        return this.add(u);
    }

    /**
     * Creates a user index from a stream of user objects.
     *
     * @param <U> User type.
     * @param users Stream of user objects.
     * @return a fast user index.
     */
    public static <U> SimpleFastUpdateableUserIndex<U> load(Stream<U> users) {
        SimpleFastUpdateableUserIndex<U> userIndex = new SimpleFastUpdateableUserIndex<>();
        users.forEach(userIndex::addUser);
        return userIndex;
    }
}
