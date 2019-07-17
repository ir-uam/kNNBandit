/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0.
 * 
 */
package es.uam.eps.ir.knnbandit.data.preference.index;

import es.uam.eps.ir.ranksys.core.index.ItemIndex;
import java.util.stream.Stream;

/**
 * Updateable index for a set of items.
 * 
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <I> Item type.
 */
public interface UpdateableItemIndex<I> extends ItemIndex<I>
{
    /**
     * Adds a new item.
     * @param i The item.
     * @return the identifier of the new item.
     */
    public int addItem(I i);
        
    /**
     * Adds a set of items to the index.
     * @param items A stream containing the items to add.
     */
    public default void addItems(Stream<I> items)
    {
        items.forEach(i -> this.addItem(i));
    }
}
