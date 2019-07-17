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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Fast implementation for a weighted auto relation. Represented items have indexes between 0 and N-1, where N
 * is the number of items.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <W> Type of the weights.
 */
public class FastWeightedAutoRelation<W> extends FastWeightedRelation<W> implements AutoRelation<W>
{
    /**
     * Constructor. Builds an empty autorelation.
     */
    public FastWeightedAutoRelation()
    {
        super();
    }
    
    /**
     * Constructor. Builds an autorelation from previous information.
     * @param weightsList List of weights.
     */
    public FastWeightedAutoRelation(List<List<IdxValue<W>>> weightsList)
    {
        super(new ArrayList<>(), weightsList);
        
        for(int i = 0; i < weightsList.size(); ++i)
        {
            this.firstIdxList.add(new ArrayList<>());
        }
        
        for(int i = 0; i < weightsList.size(); ++i)
        {
            List<IdxValue<W>> list = weightsList.get(i);
            for(int j = 0; j < list.size(); ++j)
            {
                this.firstIdxList.get(list.get(j).getIdx()).add(new IdxValue<>(i, list.get(j).getValue()));
            }
        }
        
        // Sorts the lists.
        firstIdxList.parallelStream()
                .filter(l -> l != null)
                .forEach(l -> l.sort(Comparator.naturalOrder()));
        secondIdxList.parallelStream()
                .filter(l -> l != null)
                .forEach(l -> l.sort(Comparator.naturalOrder()));
    }

    @Override
    public boolean remove(int idx)
    {
        if(idx < 0 || idx >= this.numFirst())
        {
            return false;
        }
        
        int count = this.firstIdxList.get(idx).size() + this.secondIdxList.get(idx).size();
        if(this.containsPair(idx, idx)) count -= 1;
        
        // First, we remove the lists corresponding to the element to delete.
        this.firstIdxList.remove(idx);
        this.secondIdxList.remove(idx);
        
        // Then, we run over the rest of the lists, and update the elements by
        // reducing sizes.
        int totalc = IntStream.range(0, this.numFirst()).map(i -> 
        {
            List<IdxValue<W>> auxFList = new ArrayList<>();
            List<IdxValue<W>> fList = this.firstIdxList.get(i);
            int auxCount = IntStream.range(0, fList.size()).map(j -> 
            {
                int c = 0;
                if(fList.get(j).getIdx() < idx)
                {
                    auxFList.add(fList.get(j));
                }
                else if(fList.get(j).getIdx() > idx)
                {
                    auxFList.add(new IdxValue<>(fList.get(j).getIdx()-1, fList.get(j).getValue()));
                }
                else
                {
                    c++;
                }
                
                return c;
            }).sum();
            this.firstIdxList.set(i, auxFList);
            
            List<IdxValue<W>> auxSList = new ArrayList<>();
            List<IdxValue<W>> sList;
            sList = this.secondIdxList.get(i);
            auxCount += IntStream.range(0, sList.size()).map(j -> 
            {
                int c = 0;
                if(sList.get(j).getIdx() < idx)
                {
                    auxSList.add(sList.get(j));
                }
                else if(sList.get(j).getIdx() > idx)
                {
                    auxSList.add(new IdxValue<>(sList.get(j).getIdx()-1, sList.get(j).getValue()));
                }
                else
                {
                    c++;
                }
                
                return c;
            }).sum();
            
            this.secondIdxList.set(i, auxSList);
            return auxCount;
        }).sum();
        
        return (count == totalc);
    }
    
    @Override
    public IntStream getIsolated()
    {
        return IntStream.range(0, this.numFirst()).filter(i -> 
        {
            return this.firstIdxList.get(i).isEmpty() && this.secondIdxList.get(i).isEmpty();
        });
    }
    
    @Override
    public IntStream firstsWithSeconds()
    {
        return IntStream.range(0, this.numFirst()).filter( i-> 
        {
            return !this.secondIdxList.get(i).isEmpty();
        });
    }
    
    @Override
    public IntStream secondsWithFirsts()
    {
        return IntStream.range(0, this.numFirst()).filter( i-> 
        {
            return !this.firstIdxList.get(i).isEmpty();
        });
    }
}
