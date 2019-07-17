/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0.
 * 
 */
package es.uam.eps.ir.knnbandit.data.preference.fast;

import es.uam.eps.ir.knnbandit.data.preference.index.fast.FastUpdateableItemIndex;
import es.uam.eps.ir.knnbandit.data.preference.index.fast.FastUpdateableUserIndex;
import es.uam.eps.ir.ranksys.core.preference.IdPref;
import es.uam.eps.ir.ranksys.fast.preference.IdxPref;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import java.util.function.Function;
import static java.util.stream.IntStream.range;
import java.util.stream.Stream;

/**
 * Extends AbstractFastUpdateablePreferenceData and implements the data access stream-based methods using the iterator-based ones. Avoids duplicating code where iterator-based methods are preferred.
 *
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 * @param <U> User type.
 * @param <I> Item type.
 */
public abstract class IteratorsAbstractFastUpdateablePreferenceData<U, I> extends AbstractFastUpdateablePreferenceData<U, I> 
{
    /**
     * Constructor with default IdxPref to IdPref converter.
     *
     * @param userIndex User index.
     * @param itemIndex Item index.
     */
    public IteratorsAbstractFastUpdateablePreferenceData(FastUpdateableUserIndex<U> userIndex, FastUpdateableItemIndex<I> itemIndex)
    {
        super(userIndex, itemIndex);
    }

    /**
     * Constructor with custom IdxPref to IdPref converter.
     *
     * @param userIndex User index.
     * @param itemIndex Item index.
     * @param uPrefFun User IdxPref to IdPref converter.
     * @param iPrefFun Item IdxPref to IdPref converter.
     */
    public IteratorsAbstractFastUpdateablePreferenceData(FastUpdateableUserIndex<U> userIndex, FastUpdateableItemIndex<I> itemIndex, Function<IdxPref, IdPref<I>> uPrefFun, Function<IdxPref, IdPref<U>> iPrefFun)
    {
        super(userIndex, itemIndex, uPrefFun, iPrefFun);
    }

    @Override
    public Stream<? extends IdxPref> getUidxPreferences(int uidx) 
    {
        return getPreferences(numItems(uidx), getUidxIidxs(uidx), getUidxVs(uidx));
    }

    @Override
    public Stream<? extends IdxPref> getIidxPreferences(int iidx) 
    {
        return getPreferences(numUsers(iidx), getIidxUidxs(iidx), getIidxVs(iidx));
    }

    /**
     * Converts the int and double iterators to a stream of IdxPref.
     *
     * @param n Length of iterators.
     * @param idxs Iterator of user/item indices.
     * @param vs Iterator of user/item values.
     * @return Stream of IdxPref.
     */
    protected Stream<IdxPref> getPreferences(int n, IntIterator idxs, DoubleIterator vs) 
    {
        return range(0, n).mapToObj(i -> new IdxPref(idxs.nextInt(), vs.nextDouble()));
    }

    @Override
    public boolean useIteratorsPreferentially() 
    {
        return true;
    }

}
