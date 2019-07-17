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
import java.util.function.Function;
import java.util.stream.Stream;

import es.uam.eps.ir.knnbandit.data.preference.index.fast.FastUpdateableItemIndex;
import es.uam.eps.ir.knnbandit.data.preference.index.fast.FastUpdateableUserIndex;
import es.uam.eps.ir.ranksys.core.preference.IdPref;
import es.uam.eps.ir.ranksys.fast.preference.AbstractFastPreferenceData;
import es.uam.eps.ir.ranksys.fast.preference.IdxPref;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;

/**
 * Abstract updateable fast preference data, implementing the FastUpdateablePreferenceData interface.
 * 
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @author Saúl Vargas (saul.vargas@uam.es)
 *
 * @param <U> User type.
 * @param <I> Item type.
 */
public abstract class AbstractFastUpdateablePreferenceData<U, I> extends AbstractFastPreferenceData<U,I> implements FastUpdateablePreferenceData<U,I>
{
    /**
     * Constructor.
     *
     * @param users User index.
     * @param items Item index.
     */
    public AbstractFastUpdateablePreferenceData(FastUpdateableUserIndex<U> users, FastUpdateableItemIndex<I> items)
    {
        this(users, items,
                (Function<IdxPref, IdPref<I>> & Serializable) p -> new IdPref<>(items.iidx2item(p)),
                (Function<IdxPref, IdPref<U>> & Serializable) p -> new IdPref<>(users.uidx2user(p)));
    }

    /**
     * Constructor.
     *
     * @param userIndex User index.
     * @param itemIndex Item index.
     * @param uPrefFun Converter from IdxPref to IdPref (preference for item).
     * @param iPrefFun Converter from IdxPref to IdPref (preference from user).
     */
    public AbstractFastUpdateablePreferenceData(FastUpdateableUserIndex<U> userIndex, FastUpdateableItemIndex<I> itemIndex, Function<IdxPref, IdPref<I>> uPrefFun, Function<IdxPref, IdPref<U>> iPrefFun) 
    {
        super(userIndex, itemIndex, uPrefFun, iPrefFun);
    }

    @Override
    public void updateAddUser(U u)
    {
        this.addUser(u);
    }
    
    @Override
    public void updateAddItem(I i)
    {
        this.addItem(i);
    }
    
    @Override
    public void update(Stream<Tuple3<U,I,Double>> tuples)
    {
        tuples.forEach(t -> 
        {
            if(this.containsUser(t.v1) && this.containsItem(t.v2))
            {
                int uidx = this.user2uidx(t.v1);
                int iidx = this.item2iidx(t.v2);
                this.updateRating(uidx, iidx, t.v3);
            }
        });
    }

    @Override
    public void update(U u, I i, double val)
    {
        if(this.containsUser(u) && this.containsItem(i))
        {
            int uidx = this.user2uidx(u);
            int iidx = this.item2iidx(i);
            this.updateRating(uidx, iidx, val); 
        }
    }
    
    @Override
    public void updateDelete(Stream<Tuple2<U,I>> tuples)
    {
        tuples.forEach(t -> 
        {
            if(this.containsUser(t.v1) && this.containsItem(t.v2))
            {
                int uidx = this.user2uidx(t.v1());
                int iidx = this.item2iidx(t.v2());

                if(uidx >= 0 && iidx >= 0)
                {
                    this.updateDelete(uidx, iidx);
                }
            }
        });
    }
    
    @Override
    public void updateDelete(U u, I i)
    {
        int uidx = this.user2uidx(u);
        int iidx = this.item2iidx(i);
            
        if(uidx >= 0 && iidx >= 0)
        {
            this.updateDelete(uidx, iidx);
        }
    }
    
    /**
     * Updates a rating value.
     * @param uidx Identifier of the user.
     * @param iidx Identifier of the item.
     * @param rating The rating.
     */
    protected abstract void updateRating(int uidx, int iidx, double rating);
    
    /**
     * Deletes a rating.
     * @param uidx Identifier of the user.
     * @param iidx Identifier of the item.
     */
    protected abstract void updateDelete(int uidx, int iidx);
}
