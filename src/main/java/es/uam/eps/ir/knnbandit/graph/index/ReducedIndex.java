/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0.
 * 
 */
package es.uam.eps.ir.knnbandit.graph.index;

/**
 * Index that cannot be modified.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <T> Type of the objects.
 */
public interface ReducedIndex<T>
{
    /**
     * Gets the index of a given object.
     * @param i Object to obtain.
     * @return the index if the object exists, -1 if not.
     */
    public int object2idx(T i);
    /**
     * Gets the object corresponding to a certain index.
     * @param idx The index.
     * @return the object corresponding to the index.
     */
    public T idx2object(int idx);
}
