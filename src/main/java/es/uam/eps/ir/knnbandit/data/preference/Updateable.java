/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0.
 * 
 */
package es.uam.eps.ir.knnbandit.data.preference;
import java.util.stream.Stream;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;

/**
 * Preference data that allows updating over time.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <U> User type.
 * @param <I> Item type.
 */
public interface Updateable<U,I>
{
    /**
     * Updates the preference data given a set of preferences.
     * It does not add new users/items. Tuples with non-existing
     * users/items will be ignored.
     * @param tuples The tuples.
     */
    public void update(Stream<Tuple3<U, I, Double>> tuples);
    
    /**
     * Updates an individual preference.
     * @param u User.
     * @param i Item.
     * @param val Preference value.
     */
    public void update(U u, I i, double val);
    
    /**
     * Updates the preference data given a set of preferences to delete.
     * @param tuples The tuples.
     */
    public void updateDelete(Stream<Tuple2<U, I>> tuples);
    
    /**
     * Deletes an individual preference.
     * @param u User.
     * @param i Item.
     */
    public void updateDelete(U u, I i);
    
    /**
     * Adds a user.
     * @param u User.
     */
    public void updateAddUser(U u);
    
    /**
     * Adds an item.
     * @param i Item.
     */
    public void updateAddItem(I i);
}
