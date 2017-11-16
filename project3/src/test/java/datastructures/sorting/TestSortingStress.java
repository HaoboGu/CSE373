package datastructures.sorting;

import misc.BaseTest;
import misc.Searcher;

import org.junit.Test;

import datastructures.concrete.ArrayHeap;
import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;
import datastructures.interfaces.IPriorityQueue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * See spec for details on what kinds of tests this class should include.
 */
public class TestSortingStress extends BaseTest {
    protected <T extends Comparable<T>> IPriorityQueue<T> makeInstance() {
        return new ArrayHeap<>();
    }
    
    protected <T extends Comparable<T>> List<T> convertHeap2List(IPriorityQueue<T> pq){
        // Convert heap to List<T>, for easily sorting using Collections.sort 
        List<T> newList = new ArrayList<T>();
        int length = pq.size();
        for (int i = 0; i < length; i++){
            newList.add(pq.removeMin());
        }
        for (T item:newList){
            pq.insert(item);
        }
        return newList;
    }
    
    protected <T extends Comparable<T>> List<T> convertIList2List(IList<T> inputList) {
        List<T> newList = new ArrayList<T>();
        for (T item:inputList) {
            newList.add(item);
        }
        return newList;
    }
    
    @Test(timeout=10*SECOND)
    public void testInsertMany() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 0; i < 5000000; i++) {
            heap.insert(5000000-2*i);
        }
        List<Integer> orderedList = this.convertHeap2List(heap);
        Collections.sort(orderedList);
        for (Integer item:orderedList) {
            assertEquals(item, heap.removeMin());
        }
    }
    
    @Test(timeout=15*SECOND)
    public void testStressTopK() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 5000000; i++) {
            list.add(-2*i);
        }
        IList<Integer> orderedList = Searcher.topKSort(2000000, list);
        List<Integer> newList = convertIList2List(list);
        assertEquals(2000000, orderedList.size());
        Collections.sort(newList);
        Collections.reverse(newList);
        List<Integer> goldStandard = newList.subList(0, 2000000);
        Collections.reverse(goldStandard);
        Iterator<Integer> i1 = orderedList.iterator();
        Iterator<Integer> i2 = goldStandard.iterator();
        
        for (int i = 0; i < 2000000; i++) {
            assertEquals(i2.next(), i1.next());
        }
    }
    
    @Test(timeout=15*SECOND)
    public void testStressTopLargeK() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 5000000; i++) {
            list.add(-2*i);
        }
        IList<Integer> orderedList = Searcher.topKSort(5000000, list);
        List<Integer> newList = convertIList2List(list);
        assertEquals(5000000, orderedList.size());
        Collections.sort(newList);
        Collections.reverse(newList);
        List<Integer> goldStandard = newList.subList(0, 5000000);
        Collections.reverse(goldStandard);
        Iterator<Integer> i1 = orderedList.iterator();
        Iterator<Integer> i2 = goldStandard.iterator();
        
        for (int i = 0; i < 5000000; i++) {
            assertEquals(i2.next(), i1.next());
        }
    }
}
