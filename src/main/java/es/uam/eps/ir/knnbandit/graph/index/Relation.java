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

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A relation between two different sets of objects.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <W> Type of the weights.
 */
public interface Relation<W>
{
    /**
     * Gets the total number of elements of the first item in the relation.
     * @return the number of different elements of the first item in the relation.
     */
    public int numFirst();
    
    /**
     * Gets the total number of elements of the first item related to a second item.
     * @param secondIdx Identifier of the second item.
     * @return the first item count.
     */
    public int numFirst(int secondIdx);
    
    /**
     * Gets the total number of elements of the second item in the relation.
     * @return the number of different elements of the second item in the relation.
     */
    public int numSecond();
    
    /**
     * Gets the total number of elements of the second item related to a first item.
     * @param firstIdx Identifier of the first item.
     * @return the second item count.
     */
    public int numSecond(int firstIdx);
    
    /**
     * Gets all the elements of the first type.
     * @return A stream containing all the elements.
     */
    public Stream<Integer> getAllFirst();
    
    /**
     * Gets all the elements of the second type.
     * @return A stream containing all the elements.
     */
    public Stream<Integer> getAllSecond();
    
    /**
     * Gets all the identifiers of items related to a second identifier.
     * @param secondIdx Identifier of the second object.
     * @return A stream containing all the weights of the first items.
     */
    public Stream<IdxValue<W>> getIdsFirst(int secondIdx);
    
    /**
     * Gets all the identifiers of items related to a first identifier.
     * @param firstdIdx Identifier of the first object.
     * @return A stream containing all the weights of the second items.
     */
    public Stream<IdxValue<W>> getIdsSecond(int firstdIdx);
    
    /**
     * Adds a new item to the relation (if it does not previously exist).
     * @param firstIdx Identifier of the first item.
     * @return true if everything went OK, false if it did previously exist, or something went wrong.
     */
    public boolean addFirstItem(int firstIdx);
    
    /**
     * Adds a new second item to the relation (if it does not previously exist).
     * @param secondIdx Identifier of the second item.
     * @return true if everything went OK, false if it did previously exist, or something went wrong.
     */
    public boolean addSecondItem(int secondIdx);
    
    /**
     * Adds a new relation (if it does not previously exist)
     * @param firstIdx Identifier of the first object.
     * @param secondIdx Identifier of the second object.
     * @param weight Weight of the relation.
     * @return true if everything went OK, false if it did previously exist, or something went wrong.
     */
    public boolean addRelation(int firstIdx, int secondIdx, W weight);
    
    /**
     * Obtains the weight of a relation.
     * @param firstIdx Identifier of the first object.
     * @param secondIdx Identifier of the second object.
     * @return the weight if it exists, NaN if not.
     */
    public W getValue(int firstIdx, int secondIdx);
    
    public boolean containsPair(int firstIdx, int secondIdx);
    
    /**
     * Updates the weight of a pair. If the relation does not previously exist, then
     * it fails.
     * @param firstIdx Index of the first element.
     * @param secondIdx Index of the second element.
     * @param weight Weight of the link.
     * @return true if everything went ok, false if not.
     */
    public default boolean updatePair(int firstIdx, int secondIdx, W weight)
    {
        return this.updatePair(firstIdx, secondIdx, weight, false);
    }
    
    /**
     * Updates the weight of a pair.
     * @param firstIdx Index of the first element.
     * @param secondIdx Index of the second element.
     * @param weight New weight for the element.
     * @param createRelation If true, adds the new relation.
     * @return true if everything went ok, false if not.
     */
    public boolean updatePair(int firstIdx, int secondIdx, W weight, boolean createRelation);
    
    /**
     * Removes a pair.
     * @param firstIdx Index of the first element.
     * @param secondIdx Index of the second element.
     * @return true if everything went ok, false if not.
     */
    public boolean removePair(int firstIdx, int secondIdx);
    
    /**
     * Obtains the elements in first set with relations to the ones in the second.
     * @return the elements in first set with relations to the ones in the second.
     */
    public IntStream firstsWithSeconds();
    
    /**
     * Obtains the elements in second set with relations to the ones in the first.
     * @return the elements in second set with relations to the ones in the first.
     */
    public IntStream secondsWithFirsts();
    
    /**
     * Checks whether the first element has relations with some second element.
     * @param firstIdx the index of the first element.
     * @return true if it has, false otherwise.
     */
    public boolean hasSeconds(int firstIdx);
    
    /**
     * Checks whether the second element has relations with some first element.
     * @param secondIdx The index of the second element.
     * @return true if it has, false otherwise.
     */
    public boolean hasFirsts(int secondIdx);
    
    /**
     * Get the set of elements in the first set without relations with elements.
     * in the second.
     * @return the elements in the first set without relations with elements.
     * in the second.
     */
    public IntStream getIsolatedFirsts();
    
    /**
     * Get the set of elements in the second set without relations with elements.
     * in the first.
     * @return the elements in the second set without relations with elements.
     * in the first.
     */
    public IntStream getIsolatedSeconds();
    
}
