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
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Fast implementation of a weighted relation.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <W> type of the weights.
 */
public abstract class FastWeightedRelation<W> implements Relation<W>
{
    /**
     * Links from the first kind of objects to the second. Indexed by the second. E.g. incident edges.
     */
    protected final List<List<IdxValue<W>>> firstIdxList;
    /**
     * Links from the second kind of objects to the first. Indexed by the first. E.g. outgoing edges.
     */
    protected final List<List<IdxValue<W>>> secondIdxList;
    
    /**
     * Constructor. Builds an empty weighted relation.
     */
    public FastWeightedRelation()
    {
        firstIdxList = new ArrayList<>();
        secondIdxList = new ArrayList<>();
    }
    
    /**
     * Constructor.
     * @param firstIdxList Links from the first kind of objects to the second. Indexed by the second. E.g. incident edges.
     * @param secondIdxList Links from the second kind of objects to the first. Indexed by the first. E.g. outgoing edges.
     */
    public FastWeightedRelation(List<List<IdxValue<W>>> firstIdxList, List<List<IdxValue<W>>> secondIdxList)
    {
        this.firstIdxList = firstIdxList;
        this.secondIdxList = secondIdxList;
    }
    
    @Override
    public int numFirst()
    {
        return this.secondIdxList.size();
    }

    @Override
    public int numFirst(int secondIdx)
    {
        return this.firstIdxList.get(secondIdx).size();
    }

    
    @Override
    public int numSecond(int firstIdx)
    {
        return this.secondIdxList.get(firstIdx).size();
    }

    @Override
    public Stream<Integer> getAllFirst()
    {
        List<Integer> list = new ArrayList<>();
        int n = this.numFirst();
        for(int i = 0; i < n; ++i)
            list.add(i);
        return list.stream();
    }

    @Override
    public Stream<IdxValue<W>> getIdsFirst(int secondIdx)
    {
        return this.firstIdxList.get(secondIdx).stream();
    }

    @Override
    public Stream<IdxValue<W>> getIdsSecond(int firstIdx)
    {
        return this.secondIdxList.get(firstIdx).stream();
    }

    @Override
    public boolean addFirstItem(int firstIdx)
    {
        int size = this.secondIdxList.size();
        if(firstIdx < size && this.secondIdxList.get(firstIdx) != null)
            return false;
        if(firstIdx > size)
            return false;
       
        this.firstIdxList.add(firstIdx, new ArrayList<>());
        this.secondIdxList.add(firstIdx, new ArrayList<>());
        
        return true;
    }

    @Override
    public boolean addRelation(int firstIdx, int secondIdx, W weight)
    {
        if(firstIdx < 0 || secondIdx < 0)
            return false;
        else if(firstIdx >= this.secondIdxList.size() || secondIdx >= this.firstIdxList.size())
            return false;

        Integer value = this.binarySearch(firstIdx, secondIdx, weight,true);
        if(value == null || value >= 0)
            return false;
        int idx = Math.abs(value + 1);
        this.firstIdxList.get(secondIdx).add(idx, new IdxValue<>(firstIdx, weight));
        
        value = this.binarySearch(firstIdx, secondIdx, weight, false);
        if(value == null || value >= 0)
            return false;
        idx = Math.abs(value + 1);
        this.secondIdxList.get(firstIdx).add(idx, new IdxValue<>(secondIdx, weight));
        return true;
    }

    @Override
    public W getValue(int firstIdx, int secondIdx)
    {
        if(firstIdx < 0 || secondIdx < 0) return null;
        int idx = this.binarySearch(firstIdx, secondIdx, null, true);
        if(idx < 0) return null;
        else return this.firstIdxList.get(secondIdx).get(idx).getValue();
    }
    
    @Override
    public boolean containsPair(int firstIdx, int secondIdx)
    {
        if(firstIdx < 0 || secondIdx < 0)
            return false;
        else if(firstIdx >= this.secondIdxList.size() || secondIdx >= this.firstIdxList.size())
            return false;
        
        return this.binarySearch(firstIdx, secondIdx, null, true) >= 0;
    }
    
    @Override
    public boolean updatePair(int firstIdx, int secondIdx, W weight, boolean createRelation)
    {
        if(firstIdx < 0 || secondIdx < 0)
            return false;
        else if(firstIdx >= this.secondIdxList.size() || secondIdx >= this.firstIdxList.size())
            return false;
        
        Integer value = this.binarySearch(firstIdx, secondIdx, weight, true);
        int idx;
        if(value == null || (value < 0 && !createRelation))
        {
            return false;
        }
        else if(value < 0)
        {
            idx = Math.abs(value + 1);
            this.firstIdxList.get(secondIdx).add(idx, new IdxValue<>(firstIdx, weight));
        }
        else
        {
            this.firstIdxList.get(secondIdx).set(value, new IdxValue<>(firstIdx, weight));
        }
               
        value = this.binarySearch(firstIdx, secondIdx, weight, false);
        if(value == null || (value < 0 && !createRelation))
        {
            return false;
        }
        else if(value < 0)
        {
            idx = Math.abs(value + 1);
            this.secondIdxList.get(firstIdx).add(idx, new IdxValue<>(secondIdx, weight));
        }
        else
        {
            this.secondIdxList.get(firstIdx).set(value, new IdxValue<>(secondIdx, weight));
        }
        
        return true;
    }

    @Override
    public boolean removePair(int firstIdx, int secondIdx) 
    {
        if(firstIdx < 0 || secondIdx < 0)
            return false;
        else if(firstIdx >= this.secondIdxList.size() || secondIdx >= this.firstIdxList.size())
            return false;
        
        Integer value = this.binarySearch(firstIdx, secondIdx, null, true);
        if(value == null || value < 0)
            return false;
        this.firstIdxList.get(secondIdx).remove(value.intValue());
        
        value = this.binarySearch(firstIdx, secondIdx, null, false);
        if(value == null || value < 0)
            return false;
        this.secondIdxList.get(firstIdx).remove(value.intValue());
        
        return true;
    }
    
    /**
     * Given a pair (firstIdx, secondIdx), finds it in the graph using binary search.
     * @param firstIdx The first element.
     * @param secondIdx The second element.
     * @param weight The weight value.
     * @param firstList True if the element has to be found on the list of first elements,
     * false if it has to be found on the list of second elements.
     * @return the index of the element if it exists, - (insertpoint - 1) if it does not,
     * where insertpoint is the corresponding point where the element should be added.
     */
    private Integer binarySearch(int firstIdx, int secondIdx, W weight, boolean firstList)
    {
        if(firstIdx < 0 || secondIdx < 0)
        {
            return null;
        }
        else if(firstIdx >= this.secondIdxList.size() || secondIdx >= this.firstIdxList.size())
        {
            return null;
        }
        
        IdxValue<W> elementToAdd = firstList ? new IdxValue<>(firstIdx, weight) : new IdxValue<>(secondIdx,weight);
        
        List<IdxValue<W>> list = firstList ? this.firstIdxList.get(secondIdx) : this.secondIdxList.get(firstIdx);
        return Collections.binarySearch(list, elementToAdd);
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
        return IntStream.range(0, this.numSecond()).filter( i-> 
        {
            return !this.firstIdxList.get(i).isEmpty();
        });
    }
    
        @Override
    public boolean hasSeconds(int firstIdx) 
    {
        if(firstIdx < 0 || this.numFirst() <= firstIdx)
            return false;
        return !this.secondIdxList.get(firstIdx).isEmpty();
    }

    @Override
    public boolean hasFirsts(int secondIdx) {
        if(secondIdx < 0 || this.numSecond() <= secondIdx)
            return false;
        return !this.firstIdxList.get(secondIdx).isEmpty();
    }

    @Override
    public IntStream getIsolatedFirsts() 
    {
        return IntStream.range(0, this.numFirst()).filter( i-> 
        {
            return this.secondIdxList.get(i).isEmpty();
        });    
    }

    @Override
    public IntStream getIsolatedSeconds() 
    {
        return IntStream.range(0, this.numSecond()).filter( i-> 
        {
            return this.firstIdxList.get(i).isEmpty();
        });
    }
}
