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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Unweighted relation between different types of objects.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 * @param <W> type of the weights.
 */
public class FastUnweightedPairwiseRelation<W> extends FastUnweightedRelation<W>
{

    /**
     * Links from the first kind of objects to the second. Indexed by the second. E.g. incident edges.
     */
    private final List<List<Integer>> firstIdxList;
    /**
     * Links from the second kind of objects to the first. Indexed by the first. E.g. outgoing edges.
     */
    private final List<List<Integer>> secondIdxList;
    
    
    /**
     * Constructor. Builds an empty autorelation.
     */
    public FastUnweightedPairwiseRelation()
    {
        firstIdxList = new ArrayList<>();
        secondIdxList = new ArrayList<>();
    }
    
    /**
     * Constructor. Builds an autorelation from previous information.
     * @param weightList List of weights.
     */
    public FastUnweightedPairwiseRelation(List<List<Integer>> weightList)
    {
        this.firstIdxList = new ArrayList<>();
        this.secondIdxList = weightList;
        
        int size = secondIdxList.size();
        for(int i = 0; i < size; ++i)
        {
            this.firstIdxList.add(new ArrayList<>());
        }
        
        for(int i = 0; i < size; ++i)
        {
            List<Integer> list = this.secondIdxList.get(i);
            for(int j = 0; j < list.size(); ++j)
            {
                this.firstIdxList.get(list.get(j)).add(i);
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
    public int numSecond()
    {
        return this.firstIdxList.size();
    }

    @Override
    public int numSecond(int firstIdx)
    {
        return this.secondIdxList.get(firstIdx).size();
    }
    
    @Override
    public Stream<Integer> getAllFirst()
    {
        List<Integer> l = new ArrayList<>();
        for(int i = 0; i < numFirst(); ++i)
        {
            l.add(i);
        }
        
        return l.stream();
    }

    @Override
    public Stream<Integer> getAllSecond()
    {
        List<Integer> list = new ArrayList<>();
        int size = this.firstIdxList.size();
        for(int i = 0; i < size; ++i)
            list.add(i);
        return list.stream();
    }
    
    @Override
    public Stream<IdxValue<W>> getIdsFirst(int secondIdx)
    {
        return this.firstIdxList.get(secondIdx).stream().map(i -> new IdxValue<>(i, null));
    }

    @Override
    public Stream<IdxValue<W>> getIdsSecond(int firstdIdx)
    {
        return this.secondIdxList.get(firstdIdx).stream().map(i -> new IdxValue<>(i, null));
    }

    
    @Override
    public boolean addFirstItem(int firstIdx)
    {
        int size = this.secondIdxList.size();
        if(firstIdx < size && this.secondIdxList.get(firstIdx) != null)
            return false;
        if(firstIdx > size)
            return false;
        
        this.secondIdxList.add(new ArrayList<>());
        
        return true;
    }
    
    @Override
    public boolean addSecondItem(int secondIdx)
    {
        int size = this.firstIdxList.size();
        if(secondIdx < size && this.firstIdxList.get(secondIdx) != null)
            return false;
        if(secondIdx > size)
            return false;
       
        this.firstIdxList.add(secondIdx, new ArrayList<>());
        
        return true;
    }

    @Override
    public boolean addRelation(int firstIdx, int secondIdx, W weight)
    {
        if(firstIdx < 0 || secondIdx < 0)
            return false;
        else if(firstIdx >= this.secondIdxList.size() || secondIdx >= this.firstIdxList.size())
            return false;
        
        Integer value = this.binarySearch(firstIdx, secondIdx, true);
        if(value == null || value > 0)
            return false;
        int idx = Math.abs(value + 1);
        this.firstIdxList.get(secondIdx).add(idx, firstIdx);
        
        value = this.binarySearch(firstIdx, secondIdx, false);
        if(value == null || value > 0)
        {
            return false;
        }
        idx = Math.abs(value + 1);
        this.secondIdxList.get(firstIdx).add(idx,secondIdx);
        
        return true;
    }

    @Override
    public W getValue(int firstIdx, int secondIdx)
    {
        return null;
    }
    
    @Override
    public boolean containsPair(int firstIdx, int secondIdx)
    {
        return this.binarySearch(firstIdx, secondIdx, true) > 0;
    }
    
    @Override
    public boolean updatePair(int firstIdx, int secondIdx, W weight, boolean createRelation)    
    {
        if(firstIdx < 0 || secondIdx < 0)
            return false;
        else if(firstIdx >= this.secondIdxList.size() || secondIdx >= this.firstIdxList.size())
            return false;
        
        Integer value = this.binarySearch(firstIdx, secondIdx, true);
        if(value == null || (value < 0 && !createRelation))
            return false;
        else if(value > 0)
            return true;
        else // the relation has to be created
        {
            int idx = Math.abs(value + 1);
            this.firstIdxList.get(secondIdx).add(idx, firstIdx);
        }
        
        value = this.binarySearch(firstIdx, secondIdx, false);
        int idx = Math.abs(value + 1);
        this.secondIdxList.get(firstIdx).add(idx, secondIdx);
        return true;
    }

    @Override
    public boolean removePair(int firstIdx, int secondIdx) 
    {
        Integer value = this.binarySearch(firstIdx, secondIdx, true);
        if(value == null || value < 0)
            return false;
        this.firstIdxList.get(secondIdx).remove(value.intValue());
        
        value = this.binarySearch(firstIdx, secondIdx, false);
        if(value == null || value < 0)
            return false;
        this.secondIdxList.get(firstIdx).remove(value.intValue());
       
        return true;
    }
    
    /**
     * Given a pair (firstIdx, secondIdx), finds it in the graph using binary search.
     * @param firstIdx The first element.
     * @param secondIdx The second element.
     * @param firstList True if the element has to be found on the list of first elements,
     * false if it has to be found on the list of second elements.
     * @return the index of the element if it exists, - (insertpoint - 1) if it does not,
     * where insertpoint is the corresponding point where the element should be added.
     */
    private Integer binarySearch(int firstIdx, int secondIdx, boolean firstList)
    {
        if(firstIdx < 0 || secondIdx < 0)
        {
            return null;
        }
        else if(firstIdx >= this.secondIdxList.size() || secondIdx >= this.firstIdxList.size())
        {
            return null;
        }
        
        int elementToAdd = firstList ? firstIdx : secondIdx;
        
        List<Integer> list = firstList ? this.firstIdxList.get(secondIdx) : this.secondIdxList.get(firstIdx);
        return Collections.binarySearch(list, elementToAdd);
    }

}
