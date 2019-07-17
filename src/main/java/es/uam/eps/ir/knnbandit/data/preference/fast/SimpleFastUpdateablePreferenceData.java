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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static java.util.Comparator.comparingInt;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import es.uam.eps.ir.knnbandit.data.preference.index.fast.FastUpdateableItemIndex;
import es.uam.eps.ir.knnbandit.data.preference.index.fast.FastUpdateableUserIndex;
import es.uam.eps.ir.ranksys.core.preference.IdPref;
import es.uam.eps.ir.ranksys.fast.preference.IdxPref;
import org.jooq.lambda.function.Function4;
import org.jooq.lambda.tuple.Tuple3;
import org.jooq.lambda.tuple.Tuple4;

/**
 * Simple implementation of FastPreferenceData backed by nested lists.
 *
 * @param <U> User type.
 * @param <I> Item type.
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class SimpleFastUpdateablePreferenceData<U, I> extends StreamsAbstractFastUpdateablePreferenceData<U, I> implements FastUpdateablePointWisePreferenceData<U, I>, Serializable 
{
    /**
     * Current number of preferences.
     */
    private int numPreferences;
    /**
     * User preferences.
     */
    private final List<List<IdxPref>> uidxList;
    /**
     * Item preferences.
     */
    private final List<List<IdxPref>> iidxList; 
    
    /**
     * Constructor with default IdxPref to IdPref converter.
     *
     * @param numPreferences Initial number of total preferences.
     * @param uidxList List of lists of preferences by user index.
     * @param iidxList List of lists of preferences by item index.
     * @param uIndex User index.
     * @param iIndex Item index.
     */
    protected SimpleFastUpdateablePreferenceData(int numPreferences, List<List<IdxPref>> uidxList, List<List<IdxPref>> iidxList,
                                                 FastUpdateableUserIndex<U> uIndex, FastUpdateableItemIndex<I> iIndex)
    {
        this(numPreferences, uidxList, iidxList, uIndex, iIndex,
                (Function<IdxPref, IdPref<I>> & Serializable) p -> new IdPref<>(iIndex.iidx2item(p)),
                (Function<IdxPref, IdPref<U>> & Serializable) p -> new IdPref<>(uIndex.uidx2user(p)));
    }

    /**
     * Constructor with custom IdxPref to IdPref converter.
     *
     * @param numPreferences Initial number of total preferences.
     * @param uidxList List of lists of preferences by user index.
     * @param iidxList List of lists of preferences by item index.
     * @param uIndex User index.
     * @param iIndex Item index.
     * @param uPrefFun User IdxPref to IdPref converter.
     * @param iPrefFun Item IdxPref to IdPref converter.
     */
    protected SimpleFastUpdateablePreferenceData(int numPreferences, List<List<IdxPref>> uidxList, List<List<IdxPref>> iidxList,
            FastUpdateableUserIndex<U> uIndex, FastUpdateableItemIndex<I> iIndex,
            Function<IdxPref, IdPref<I>> uPrefFun, Function<IdxPref, IdPref<U>> iPrefFun) 
    {
        super(uIndex, iIndex, uPrefFun, iPrefFun);
        this.uidxList = uidxList;
        this.iidxList = iidxList;
        this.numPreferences = numPreferences;
        uidxList.parallelStream()
                .filter(l -> l != null)
                .forEach(l -> l.sort(comparingInt(IdxPref::v1)));
        iidxList.parallelStream()
                .filter(l -> l != null)
                .forEach(l -> l.sort(comparingInt(IdxPref::v1)));
    }

    @Override
    public int numUsers(int iidx) 
    {
        if (iidxList.get(iidx) == null) 
        {
            return 0;
        }
        return iidxList.get(iidx).size();
    }

    @Override
    public int numItems(int uidx) 
    {
        if (uidxList.get(uidx) == null) 
        {
            return 0;
        }
        return uidxList.get(uidx).size();
    }

    @Override
    public Stream<IdxPref> getUidxPreferences(int uidx) 
    {
        if (uidxList.get(uidx) == null) 
        {
            return Stream.empty();
        } 
        else 
        {
            return uidxList.get(uidx).stream();
        }
    }

    @Override
    public Stream<IdxPref> getIidxPreferences(int iidx) 
    {
        if (iidxList.get(iidx) == null) 
        {
            return Stream.empty();
        } 
        else 
        {
            return iidxList.get(iidx).stream();
        }
    }

    @Override
    public int numPreferences() 
    {
        return numPreferences;
    }

    @Override
    public IntStream getUidxWithPreferences() 
    {
        return IntStream.range(0, numUsers())
                        .filter(uidx -> uidxList.get(uidx) != null);
    }

    @Override
    public IntStream getIidxWithPreferences() 
    {
        return IntStream.range(0, this.numItems())
                        .filter(iidx -> iidxList.get(iidx) != null);
    }

    @Override
    public int numUsersWithPreferences() 
    {
        return (int) uidxList.stream()
                             .filter(iv -> iv != null)
                             .count();
    }

    @Override
    public int numItemsWithPreferences() 
    {
        return (int) iidxList.stream()
                             .filter(iv -> iv != null)
                             .count();
    }

    @Override
    public Optional<IdxPref> getPreference(int uidx, int iidx) 
    {
        List<IdxPref> uList = uidxList.get(uidx);
        if(uList == null) return Optional.empty();
        Comparator<IdxPref> comp = (x,y) -> x.v1 - y.v1;
        int position = Collections.binarySearch(uList, new IdxPref(iidx, 1.0), comp);
        
        if(position >= 0)
        {
            return Optional.of(uList.get(position));
        }
        
        return Optional.empty();
    }

    @Override
    public Optional<? extends IdPref<I>> getPreference(U u, I i) 
    {
        if(this.containsUser(u) && this.containsItem(i))
        {
            Optional<? extends IdxPref> pref = getPreference(user2uidx(u), item2iidx(i));

            if (!pref.isPresent()) 
            {
                return Optional.empty();
            } 
            else 
            {
                return Optional.of(uPrefFun.apply(pref.get()));
            }
        }
        else
        {
            return Optional.empty();
        }
    }
    
    @Override
    public int addUser(U u) 
    {
        int uidx = ((FastUpdateableUserIndex<U>)this.ui).addUser(u);
        if(this.uidxList.size() == uidx) // If the user is really new
        {
            this.uidxList.add(null);
        }
        return uidx;
    }

    @Override
    public int addItem(I i) 
    {
        int iidx = ((FastUpdateableItemIndex<I>)this.ii).addItem(i);
        if(this.iidxList.size() == iidx) // If the item is really new
        {
            this.iidxList.add(null);
        }
        return iidx;
    }

    @Override
    public void updateRating(int uidx, int iidx, double rating) 
    {        
        // If the user or the item are not in the preference data, do nothing.
        if(uidx < 0 || this.uidxList.size() <= uidx || iidx < 0 || this.iidxList.size() <= iidx)
        {
            return;
        }
        
        // Update the value for the user.
        if(this.uidxList.get(uidx) == null) // If the user does not have preferences.
        {
            List<IdxPref> idxPrefList = new ArrayList<>();
            idxPrefList.add(new IdxPref(iidx, rating));
            this.uidxList.set(uidx, idxPrefList);
            this.numPreferences++;
        }
        else // If the user has at least one preference.
        {
            // Update the preference for the user.
            boolean addPref = this.updatePreference(iidx, rating, this.uidxList.get(uidx));
            if(addPref) this.numPreferences++;
        }
        
        // Update the value for the item.
        if(this.iidxList.get(iidx) == null) // If the item does not have ratings.
        {
            List<IdxPref> idxPrefList = new ArrayList<>();
            idxPrefList.add(new IdxPref(uidx, rating));
            this.iidxList.set(iidx,idxPrefList);
        }
        else // If the item has been rated by at least one user.
        {
            this.updatePreference(uidx, rating, this.iidxList.get(iidx));
        }    
    }
    
    /**
     * Updates a preference.
     * @param idx The identifier of the preference to add.
     * @param value The rating value.
     * @param list The list in which we want to update the preference.
     * @return true if the rating was added, false if it was just updated.
     */
    private boolean updatePreference(int idx, double value, List<IdxPref> list)
    {
        if(list.size() == this.numItems())
        {
            System.err.print("");
        }
        IdxPref newIdx = new IdxPref(idx, value);
    
        // Use binary search to find the rating.
        Comparator<IdxPref> comp = (x,y) -> x.v1 - y.v1;
        int position = Collections.binarySearch(list, newIdx, comp);
        
        if(position < 0) // The rating does not exist.
        {
            position = Math.abs(position+1);
            list.add(position, newIdx);
            return true;
        }
        else // The rating did already exist.
        {
            list.set(position, newIdx);
            return false;
        }
    }   

    @Override
    protected void updateDelete(int uidx, int iidx)
    {
        // If the user or the item are not in the preference data, do nothing.
        if(uidx < 0 || this.uidxList.size() <= uidx || iidx < 0 || this.iidxList.size() <= iidx)
        {
            return;
        }
        
        // First, delete from the uidxList.
        if(this.updateDelete(iidx, this.uidxList.get(uidx)))
        {
            // Then, delete from the iidxList.
            this.updateDelete(uidx, this.iidxList.get(iidx));
            this.numPreferences--;
        }
    }
    
    /**
     * Deletes a rating from the data.
     * @param idx Identifier of the element to delete.
     * @param list List from where the element has to be removed.
     * @return true if the element was removed, false otherwise.
     */
    private boolean updateDelete(int idx, List<IdxPref> list) 
    {
        // If the list is empty, do nothing.
        if(list == null) return false;
        
        // Search for the position of the element to remove.
        IdxPref newIdx = new IdxPref(idx, 1.0);
        Comparator<IdxPref> comp = (x,y) -> x.v1 - y.v1;
        int position = Collections.binarySearch(list, newIdx, comp);
        
        // If it exists.
        if(position >= 0)
        {
            list.remove(position);
            return true;
        }
        
        return false;
    }
    
    /**
     * Loads a SimpleFastPreferenceData from a stream of user-item-value triples.
     *
     * @param <U> User type.
     * @param <I> Item type.
     * @param tuples Stream of user-item-value triples.
     * @param uIndex User index.
     * @param iIndex Item index.
     * @return an instance of SimpleFastPreferenceData containing the data from the input stream.
     */
    public static <U, I> SimpleFastUpdateablePreferenceData<U, I> load(Stream<Tuple3<U, I, Double>> tuples, FastUpdateableUserIndex<U> uIndex, FastUpdateableItemIndex<I> iIndex) 
    {
        return load(tuples.map(t -> t.concat((Void) null)),
                (uidx, iidx, v, o) -> new IdxPref(iidx, v),
                (uidx, iidx, v, o) -> new IdxPref(uidx, v),
                uIndex, iIndex,
                (Function<IdxPref, IdPref<I>> & Serializable) p -> new IdPref<>(iIndex.iidx2item(p)),
                (Function<IdxPref, IdPref<U>> & Serializable) p -> new IdPref<>(uIndex.uidx2user(p)));
    }

    /**
     * Loads a SimpleFastPreferenceData from a stream of user-item-value-other tuples. It can accomodate other information, thus you need to provide sub-classes of IdxPref IdPref accomodating for this new information.
     *
     * @param <U> User type.
     * @param <I> Item type.
     * @param <O> Additional information type.
     * @param tuples Stream of user-item-value-other tuples.
     * @param uIdxPrefFun Converts a tuple to a user IdxPref.
     * @param iIdxPrefFun Converts a tuple to a item IdxPref.
     * @param uIndex User index.
     * @param iIndex Item index.
     * @param uIdPrefFun User IdxPref to IdPref converter.
     * @param iIdPrefFun Item IdxPref to IdPref converter.
     * @return an instance of SimpleFastPreferenceData containing the data from the input stream.
     */
    public static <U, I, O> SimpleFastUpdateablePreferenceData<U, I> load(Stream<Tuple4<U, I, Double, O>> tuples,
            Function4<Integer, Integer, Double, O, ? extends IdxPref> uIdxPrefFun,
            Function4<Integer, Integer, Double, O, ? extends IdxPref> iIdxPrefFun,
            FastUpdateableUserIndex<U> uIndex, FastUpdateableItemIndex<I> iIndex,
            Function<IdxPref, IdPref<I>> uIdPrefFun,
            Function<IdxPref, IdPref<U>> iIdPrefFun) 
    {
        AtomicInteger numPreferences = new AtomicInteger();

        List<List<IdxPref>> uidxList = new ArrayList<>();
        for (int uidx = 0; uidx < uIndex.numUsers(); uidx++) {
            uidxList.add(null);
        }

        List<List<IdxPref>> iidxList = new ArrayList<>();
        for (int iidx = 0; iidx < iIndex.numItems(); iidx++) {
            iidxList.add(null);
        }

        tuples.forEach(t -> {
            int uidx = uIndex.user2uidx(t.v1);
            int iidx = iIndex.item2iidx(t.v2);

            numPreferences.incrementAndGet();

            List<IdxPref> uList = uidxList.get(uidx);
            if (uList == null) {
                uList = new ArrayList<>();
                uidxList.set(uidx, uList);
            }
            uList.add(uIdxPrefFun.apply(uidx, iidx, t.v3, t.v4));

            List<IdxPref> iList = iidxList.get(iidx);
            if (iList == null) {
                iList = new ArrayList<>();
                iidxList.set(iidx, iList);
            }
            iList.add(iIdxPrefFun.apply(uidx, iidx, t.v3, t.v4));
        });

        return new SimpleFastUpdateablePreferenceData<>(numPreferences.intValue(), uidxList, iidxList, uIndex, iIndex, uIdPrefFun, iIdPrefFun);
    }

}
