/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0.
 * 
 */
package es.uam.eps.ir.knnbandit.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import org.jooq.lambda.tuple.Tuple3;

/**
 * Methods and algorithms for combining ordered lists.
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class OrderedListCombiner 
{
    /**
     * Merges two ordered lists, so that the order is preserved.
     * @param <T> The type of the elements in the lists.
     * @param firstList The first list.
     * @param secondList The second list.
     * @param comp A comparator for determining the order of the elements.
     * @param combiner A function for combining two elements in case they are present in both lists. 
     * The first argument of the function receives an element of
     * the first list, and the second an element of the second.
     * @return the merged list.
     */
    public static <T> List<T> mergeLists(List<T> firstList, List<T> secondList, Comparator<T> comp, BiFunction<T,T,T> combiner)
    {
        return OrderedListCombiner.mergeLists(firstList.iterator(), secondList.iterator(), comp, combiner);
    }
    
    /**
     * Merges two ordered streams, so that the order is preserved.
     * @param <T> The type of the elements in the lists.
     * @param firstStream The first stream.
     * @param secondStream The second stream.
     * @param comp A comparator for determining the order of the elements.
     * @param combiner A function for combining two elements in case they are present in both streams. 
     * The first argument of the function receives an element of
     * the first stream, and the second an element of the second stream.
     * @return the merged list.
     */
    public static <T> List<T> mergeLists(Stream<T> firstStream, Stream<T> secondStream, Comparator<T> comp, BiFunction<T,T,T> combiner)
    {
        return OrderedListCombiner.mergeLists(firstStream.iterator(), secondStream.iterator(),comp, combiner);
    }
    
    /**
     * Merges two ordered lists/streams, represented by iterators, so that the order is preserved.
     * @param <T> The type of the elements in the lists.
     * @param firstIterator The first iterator.
     * @param secondIterator The second iterator.
     * @param comp A comparator for determining the order of the elements.
     * @param combiner A function for combining two elements in case they are present in both lists/streams. 
     * The first argument of the function receives an element of
     * the first list/stream, and the second an element of the second list/stream.
     * @return the merged list.
     */
    public static <T> List<T> mergeLists(Iterator<T> firstIterator, Iterator<T> secondIterator, Comparator<T> comp, BiFunction<T,T,T> combiner)
    {
        List<T> combination = new ArrayList<>();
        T lastVisited = null;
        int lastVisitedQueue = 0;
        
        Comparator<Tuple3<T, Integer, Iterator<T>>> comparator = (Tuple3<T, Integer, Iterator<T>> o1, Tuple3<T, Integer, Iterator<T>> o2) -> comp.compare(o1.v1(), o2.v1());
        PriorityQueue<Tuple3<T, Integer, Iterator<T>>> queue = new PriorityQueue<>(2, comparator);
        
        if(firstIterator.hasNext()) queue.add(new Tuple3<>(firstIterator.next(), 1, firstIterator));
        if(secondIterator.hasNext()) queue.add(new Tuple3<>(secondIterator.next(), 2, secondIterator));
        
        while(!queue.isEmpty())
        {
            Tuple3<T, Integer, Iterator<T>> tuple = queue.poll();
            if(lastVisited == null || comp.compare(lastVisited, tuple.v1()) != 0)
            {
                combination.add(tuple.v1());
                lastVisited = tuple.v1();
                lastVisitedQueue = tuple.v2();
            }
            else
            {
                if(tuple.v2 > lastVisitedQueue)
                {
                    combination.set(combination.size()-1, combiner.apply(lastVisited, tuple.v1()));
                }
                else
                {
                    combination.set(combination.size()-1, combiner.apply(tuple.v1(), lastVisited));
                }
            }
            
            if(tuple.v3().hasNext()) 
            {
                queue.add(new Tuple3<>(tuple.v3().next(), tuple.v2(), tuple.v3()));
            }
        }
        
        return combination;
    }
    
    /**
     * Finds the size of the union of two ordered lists.
     * @param <T> The type of the elements in the lists.
     * @param firstList The first list.
     * @param secondList The second list.
     * @param comp A comparator for determining the order of the elements.
     * @return the size of the merged list.
     */
    public static <T> int mergeListsSize(List<T> firstList, List<T> secondList, Comparator<T> comp)
    {
        return OrderedListCombiner.mergeListsSize(firstList.iterator(), secondList.iterator(), comp);
    }
    
    /**
     * Finds the size of the union of two ordered streams.
     * @param <T> The type of the elements in the streams.
     * @param firstStream The first stream.
     * @param secondStream The second stream.
     * @param comp A comparator for determining the order of the elements.
     * @return the size of the merged list.
     */
    public static <T> int mergeListsSize(Stream<T> firstStream, Stream<T> secondStream, Comparator<T> comp)
    {
        return OrderedListCombiner.mergeListsSize(firstStream.iterator(), secondStream.iterator(),comp);
    }
    
    /**
     * Finds the size of the union of two ordered lists/streams, represented by iterators.
     * @param <T> The type of the elements in the lists.
     * @param firstIterator The first iterator.
     * @param secondIterator The second iterator.
     * @param comp A comparator for determining the order of the elements.
     * @return the size of the union list.
     */
    public static <T> int mergeListsSize(Iterator<T> firstIterator, Iterator<T> secondIterator, Comparator<T> comp)
    {
        T lastVisited = null;
        
        int counter = 0;
        
        Comparator<Tuple3<T, Integer, Iterator<T>>> comparator = (Tuple3<T, Integer, Iterator<T>> o1, Tuple3<T, Integer, Iterator<T>> o2) -> comp.compare(o1.v1(), o2.v1());
        PriorityQueue<Tuple3<T, Integer, Iterator<T>>> queue = new PriorityQueue<>(2, comparator);
        
        if(firstIterator.hasNext()) queue.add(new Tuple3<>(firstIterator.next(), 1, firstIterator));
        if(secondIterator.hasNext()) queue.add(new Tuple3<>(secondIterator.next(), 2, secondIterator));
        
        while(!queue.isEmpty())
        {
            Tuple3<T, Integer, Iterator<T>> tuple = queue.poll();
            if(lastVisited == null || comp.compare(lastVisited, tuple.v1()) != 0)
            {
                ++counter;
                lastVisited = tuple.v1();
            }
                        
            if(tuple.v3().hasNext()) 
            {
                queue.add(new Tuple3<>(tuple.v3().next(), tuple.v2(), tuple.v3()));
            }
        }
        
        return counter;
    }
    
    /**
     * Intersects two ordered lists, preserving the order.
     * @param <T> The type of the elements in the lists.
     * @param firstList The first list.
     * @param secondList The second list.
     * @param comp A comparator for determining the order of the elements.
     * @param combiner A function for combining two elements. The first argument of the function is the one in the first list,
     * whereas the second argument is the element in the second list.
     * @return the intersection of the lists.
     */
    public static <T> List<T> intersectLists(List<T> firstList, List<T> secondList, Comparator<T> comp, BiFunction<T,T,T> combiner)
    {
        return OrderedListCombiner.intersectLists(firstList.iterator(), secondList.iterator(), comp, combiner);
    }
    
    /**
     * Intersects two ordered streams, preserving the order.
     * @param <T> the type of the elements in the lists.
     * @param firstStream The first stream.
     * @param secondStream The second stream.
     * @param comp A comparator for determining the order of the elements.
     * @param combiner A function for combining two elements. The first argument of the function is the one in the first stream,
     * whereas the second argument is the element in the second stream.
     * @return the intersection of the streams.
     */
    public static <T> List<T> intersectLists(Stream<T> firstStream, Stream<T> secondStream, Comparator<T> comp, BiFunction<T,T,T> combiner)
    {
        return OrderedListCombiner.intersectLists(firstStream.iterator(), secondStream.iterator(), comp, combiner);
    }
    
    /**
     * Intersects two ordered streams/lists, represented by iterators, preserving the order.
     * @param <T> The type of the elements in the lists.
     * @param firstIter The first iterator.
     * @param secondIter The second iterator.
     * @param comp A comparator for determining the order of the elements.
     * @param combiner A function for combining two elements. The first argument of the function is the one in the first stream,
     * whereas the second argument is the element in the second stream.
     * @return the intersection of the streams/lists.
     */
    public static <T> List<T> intersectLists(Iterator<T> firstIter, Iterator<T> secondIter, Comparator<T> comp, BiFunction<T,T,T> combiner)
    {
        List<T> intersection = new ArrayList<>();
        
        if(!firstIter.hasNext() || !secondIter.hasNext())
        {
            return intersection;
        }
        else
        {
            T first = firstIter.next();
            T second = secondIter.next();
            boolean flag = true;
            do
            {
                int comparison = comp.compare(first, second);
                if(comparison == 0)
                {
                    intersection.add(combiner.apply(first, second));
                    
                    if(firstIter.hasNext() && secondIter.hasNext())
                    {
                        first = firstIter.next();
                        second = secondIter.next();
                    }
                    else
                    {
                        flag = false;
                    }
                }
                else if(comparison < 0) // first < second
                {
                    if(firstIter.hasNext())
                    {
                        first = firstIter.next();
                    }
                    else
                    {
                        flag = false;
                    }
                }
                else // if (comparison > 0) first > second
                {
                    if(secondIter.hasNext())
                    {
                        second = secondIter.next();
                    }
                    else
                    {
                        flag = false;
                    }
                }
            }
            while(flag);
            
            return intersection;
        }
    }
    
    /**
     * Finds the size of the intersection of two ordered lists.
     * @param <T> the type of the elements in the lists.
     * @param firstList The first list.
     * @param secondList The second list.
     * @param comp A comparator for determining the order of the elements.
     * @return the size of the intersection of the lists.
     */
    public static <T> int intersectListsSize(List<T> firstList, List<T> secondList, Comparator<T> comp)
    {
        return OrderedListCombiner.intersectListsSize(firstList.iterator(), secondList.iterator(), comp);
    }
    
    /**
     * Finds the size of the intersection of two ordered streams.
     * @param <T> The type of the elements in the lists.
     * @param firstStream The first stream.
     * @param secondStream The second stream.
     * @param comp A comparator for determining the order of the elements.
     * @return the size of the intersection of the streams.
     */
    public static <T> int intersectListsSize(Stream<T> firstStream, Stream<T> secondStream, Comparator<T> comp)
    {
        return OrderedListCombiner.intersectListsSize(firstStream.iterator(), secondStream.iterator(), comp);
    }
    
    /**
     * Finds the size of the intersection of two ordered streams/lists.
     * @param <T> The type of the elements.
     * @param firstIter The iterator representing the first list/stream.
     * @param secondIter The iterator representing the second list/stream.
     * @param comp The comparator.
     * @return the size of the intersection.
     */
    public static <T> int intersectListsSize(Iterator<T> firstIter, Iterator<T> secondIter, Comparator<T> comp)
    {
        int counter = 0;
        if(!firstIter.hasNext() || !secondIter.hasNext())
        {
            return counter;
        }
        else
        {
            T first = firstIter.next();
            T second = secondIter.next();
            boolean flag = true;
            do
            {
                int comparison = comp.compare(first, second);
                if(comparison == 0)
                {
                    counter++;
                    
                    if(firstIter.hasNext() && secondIter.hasNext())
                    {
                        first = firstIter.next();
                        second = secondIter.next();
                    }
                    else
                    {
                        flag = false;
                    }
                }
                else if(comparison < 0) // first < second
                {
                    if(firstIter.hasNext())
                    {
                        first = firstIter.next();
                    }
                    else
                    {
                        flag = false;
                    }
                }
                else // if (comparison > 0) first > second
                {
                    if(secondIter.hasNext())
                    {
                        second = secondIter.next();
                    }
                    else
                    {
                        flag = false;
                    }
                }
            }
            while(flag);
            
            return counter;
        }
    }
    
    /**
     * Checks whether the intersection of two ordered lists is empty or not.
     * @param <T> The type of the elements.
     * @param firstList The first list.
     * @param secondList The second list.
     * @param comp A comparator.
     * @return true if the intersection has elements, false otherwise.
     */
    public static <T> boolean intersectionHasElements(List<T> firstList, List<T> secondList, Comparator<T> comp)
    {
        return OrderedListCombiner.intersectionHasElements(firstList.iterator(), secondList.iterator(), comp);
    }
    
    /**
     * Checks whether the intersection of two ordered lists is empty or not.
     * @param <T> The type of the elements.
     * @param firstList A stream containing the first list.
     * @param secondList A stream containing the second list.
     * @param comp A comparator.
     * @return true if the intersection has elements, false otherwise.
     */
    public static <T> boolean intersectionHaslements(Stream<T> firstList, Stream<T> secondList, Comparator<T> comp)
    {
        return OrderedListCombiner.intersectionHasElements(firstList.iterator(), secondList.iterator(), comp);
    }
    
    /**
     * Checks whether the intersection of two ordered lists is empty or not.
     * @param <T> The type of the elements.
     * @param firstList The iterator of the first list.
     * @param secondList The iterator of the second list.
     * @param comp A comparator.
     * @return true if the intersection has elements, false otherwise.
     */
    public static <T> boolean intersectionHasElements(Iterator<T> firstList, Iterator<T> secondList, Comparator<T> comp)
    {
        if(!firstList.hasNext() || !secondList.hasNext()) return false;
        else
        {
            T first = firstList.next();
            T second = secondList.next();
            
            do
            {
                int comparison = comp.compare(first, second);
                if(comparison == 0) return true;
                else if(comparison < 0)
                {
                    if(firstList.hasNext()) first = firstList.next();
                    else return false;
                }
                else
                {
                    if(secondList.hasNext()) second = secondList.next();
                    else return false;
                }
            }
            while(true);
        }
    }

    /**
     * Finds the union and intersection of two ordered lists, preserving the order.
     * @param <T> The type of the elements in the lists.
     * @param firstList The first list.
     * @param secondList The second list.
     * @param comp A comparator for determining the order of the elements.
     * @param combiner A function for combining two elements. The first argument of the function is the one in the first stream,
     * whereas the second argument is the element in the second stream.
     * @return a pair containing the union of the streams/lists in the first element, and the intersection in the other.
     */
    public static <T> Pair<List<T>> mergeAndIntersectLists(List<T> firstList, List<T> secondList, Comparator<T> comp, BiFunction<T,T,T> combiner)
    {
        return OrderedListCombiner.mergeAndIntersectLists(firstList.iterator(), secondList.iterator(), comp, combiner);
    }
    
    /**
     * Finds the union and intersection of two ordered streams, preserving the order.
     * @param <T> The type of the elements in the streams.
     * @param firstList The first streams.
     * @param secondList The second streams.
     * @param comp A comparator for determining the order of the elements.
     * @param combiner A function for combining two elements. The first argument of the function is the one in the first stream,
     * whereas the second argument is the element in the second stream.
     * @return a pair containing the union of the streams/lists in the first element, and the intersection in the other.
     */
    public static <T> Pair<List<T>> mergeAndIntersectLists(Stream<T> firstList, Stream<T> secondList, Comparator<T> comp, BiFunction<T,T,T> combiner)
    {
        return OrderedListCombiner.mergeAndIntersectLists(firstList.iterator(), secondList.iterator(), comp, combiner);
    }
    
    /**
     * Finds the union and intersection of two ordered streams/lists, represented by iterators, preserving the order.
     * @param <T> The type of the elements in the lists.
     * @param firstIterator The first iterator.
     * @param secondIterator The second iterator.
     * @param comp A comparator for determining the order of the elements.
     * @param combiner A function for combining two elements. The first argument of the function is the one in the first stream,
     * whereas the second argument is the element in the second stream.
     * @return a pair containing the union of the streams/lists in the first element, and the intersection in the other.
     */
    public static <T> Pair<List<T>> mergeAndIntersectLists(Iterator<T> firstIterator, Iterator<T> secondIterator, Comparator<T> comp, BiFunction<T,T,T> combiner)
    {
        List<T> combination = new ArrayList<>();
        List<T> intersection = new ArrayList<>();
        T lastVisited = null;
        int lastVisitedQueue = 0;
        
        Comparator<Tuple3<T, Integer, Iterator<T>>> comparator = (Tuple3<T, Integer, Iterator<T>> o1, Tuple3<T, Integer, Iterator<T>> o2) -> comp.compare(o1.v1(), o2.v1());
        PriorityQueue<Tuple3<T, Integer, Iterator<T>>> queue = new PriorityQueue<>(2, comparator);
        
        if(firstIterator.hasNext()) queue.add(new Tuple3<>(firstIterator.next(), 1, firstIterator));
        if(secondIterator.hasNext()) queue.add(new Tuple3<>(secondIterator.next(), 2, secondIterator));
        
        while(!queue.isEmpty())
        {
            Tuple3<T, Integer, Iterator<T>> tuple = queue.poll();
            if(lastVisited == null || comp.compare(lastVisited, tuple.v1()) != 0)
            {
                combination.add(tuple.v1());
                lastVisited = tuple.v1();
                lastVisitedQueue = tuple.v2();
            }
            else
            {
                if(tuple.v2 > lastVisitedQueue)
                {
                    combination.set(combination.size()-1, combiner.apply(lastVisited, tuple.v1()));
                    intersection.add(combiner.apply(lastVisited, tuple.v1));
                }
                else
                {
                    combination.set(combination.size()-1, combiner.apply(tuple.v1(), lastVisited));
                    intersection.add(combiner.apply(lastVisited, tuple.v1));
                }
            }
            
            if(tuple.v3().hasNext()) 
            {
                queue.add(new Tuple3<>(tuple.v3().next(), tuple.v2(), tuple.v3()));
            }
        }
        
        return new Pair<>(combination, intersection);
    }
    
    /**
     * Finds the union and intersection of two ordered lists, preserving the order.
     * @param <T> The type of the elements in the lists.
     * @param firstList The first list.
     * @param secondList The second list.
     * @param comp A comparator for determining the order of the elements.
     * @return a pair containing the union of the streams/lists in the first element, and the intersection in the other.
     */
    public static <T> Pair<Integer> mergeAndIntersectListsSize(List<T> firstList, List<T> secondList, Comparator<T> comp)
    {
        return OrderedListCombiner.mergeAndIntersectListsSize(firstList.iterator(), secondList.iterator(), comp);
    }
    
    /**
     * Finds the union and intersection of two ordered streams, preserving the order.
     * @param <T> The type of the elements in the streams.
     * @param firstList The first streams.
     * @param secondList The second streams.
     * @param comp A comparator for determining the order of the elements.
     * @return a pair containing the union of the streams/lists in the first element, and the intersection in the other.
     */
    public static <T> Pair<Integer> mergeAndIntersectListsSize(Stream<T> firstList, Stream<T> secondList, Comparator<T> comp)
    {
        return OrderedListCombiner.mergeAndIntersectListsSize(firstList.iterator(), secondList.iterator(), comp);
    }
    
    /**
     * Finds the union and intersection of two ordered streams/lists, represented by iterators, preserving the order.
     * @param <T> The type of the elements in the lists.
     * @param firstIterator The first iterator.
     * @param secondIterator The second iterator.
     * @param comp A comparator for determining the order of the elements.
     * @return a pair containing the union of the streams/lists in the first element, and the intersection in the other.
     */
    public static <T> Pair<Integer> mergeAndIntersectListsSize(Iterator<T> firstIterator, Iterator<T> secondIterator, Comparator<T> comp)
    {
        T lastVisited = null;
        int combination = 0;
        int intersection = 0;
        
        Comparator<Tuple3<T, Integer, Iterator<T>>> comparator = (Tuple3<T, Integer, Iterator<T>> o1, Tuple3<T, Integer, Iterator<T>> o2) -> comp.compare(o1.v1(), o2.v1());
        PriorityQueue<Tuple3<T, Integer, Iterator<T>>> queue = new PriorityQueue<>(2, comparator);
        
        if(firstIterator.hasNext()) queue.add(new Tuple3<>(firstIterator.next(), 1, firstIterator));
        if(secondIterator.hasNext()) queue.add(new Tuple3<>(secondIterator.next(), 2, secondIterator));
        
        while(!queue.isEmpty())
        {
            Tuple3<T, Integer, Iterator<T>> tuple = queue.poll();
            if(lastVisited == null || comp.compare(lastVisited, tuple.v1()) != 0)
            {
                ++combination;
                lastVisited = tuple.v1();
            }
            else
            {
                ++intersection;
            }
            
            if(tuple.v3().hasNext()) 
            {
                queue.add(new Tuple3<>(tuple.v3().next(), tuple.v2(), tuple.v3()));
            }
        }
        
        return new Pair<>(combination, intersection);
    }
}
