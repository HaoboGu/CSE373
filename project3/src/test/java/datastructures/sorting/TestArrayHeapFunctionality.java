package datastructures.sorting;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import misc.BaseTest;
import datastructures.concrete.ArrayHeap;
import datastructures.interfaces.IList;
import datastructures.interfaces.IPriorityQueue;
import misc.exceptions.EmptyContainerException;
import misc.exceptions.NoSuchKeyException;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import org.junit.Test;

/**
 * See spec for details on what kinds of tests this class should include.
 */
public class TestArrayHeapFunctionality extends BaseTest {
    protected <T extends Comparable<T>> IPriorityQueue<T> makeInstance() {
        return new ArrayHeap<>();
    }
    protected <T extends Comparable<T>> List<T> convertIList2List(IPriorityQueue<T> l){
        // Convert IList to List<T>, for easily sorting using Collections.sort 
        List<T> newList = new ArrayList<T>(); 
        for (int i = 0; i < l.size(); i++){
           newList.add(l.removeMin());
        }
        return newList;
    }
    @Test(timeout=SECOND)
    public void testBasicSize() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.insert(3);
        assertEquals(1, heap.size());
    }
    
    @Test(timeout=SECOND)
    public void testBasicSize2() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.insert(3);
        heap.insert(4);
        heap.insert(1);
        heap.insert(7);
        
        assertEquals(4, heap.size());
    }
    

    @Test(timeout=SECOND)
    public void testInsert100() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 0; i < 100; i++) {
            heap.insert(i);
        }
        assertEquals(100, heap.size());
        List<Integer> newHeap = convertIList2List(heap);
        System.out.println(heap.size());  // Check first
        Collections.sort(newHeap);
        
        for (int i = 0; i < 100; i++) {
            assertEquals(newHeap.get(i), heap.removeMin());
        }
    }
    
    @Test(timeout=SECOND)
    public void testOrderedInsertFew() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.insert(1);
        heap.insert(2);
        heap.insert(3);
        heap.insert(4);
        assertEquals(4, heap.size());
        for (int i = 0; i < 4; i++) {
            assertEquals(i, heap.removeMin());
        }
    }
    
    @Test(timeout=SECOND)
    public void testReversedOrderedInsertFew() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 0; i < 4; i++) {
            heap.insert(4-i);
        }
        assertEquals(200, heap.size());
        for (int i = 0; i < 4; i++) {
            assertEquals(i, heap.removeMin());
        }
    }
    
    @Test(timeout=SECOND)
    public void testOrderedInsert() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 0; i < 200; i++) {
            heap.insert(i);
        }
        assertEquals(200, heap.size());
        for (int i = 0; i < 200; i++) {
            assertEquals(i, heap.removeMin());
        }
    }
    
    @Test(timeout=SECOND)
    public void testReversedOrderedInsert() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 0; i < 200; i++) {
            heap.insert(200-i);
        }
        assertEquals(200, heap.size());
        for (int i = 0; i < 200; i++) {
            assertEquals(i, heap.removeMin());
        }
    }
    
   
    
    @Test(timeout=SECOND)
    public void testInsertNull() {
        IPriorityQueue<Integer> heap = this.makeInstance(); 
        heap.insert(1);
        try {
            heap.insert(null);
            fail("Insert null successfully");
        }
        catch (NoSuchKeyException ex){
            // all right
        }
    }
    
    @Test(timeout=SECOND)
    public void testSameInput() {
        IPriorityQueue<Integer> heap = this.makeInstance(); 
        heap.insert(1);
        heap.insert(2);
        heap.insert(3);
        heap.insert(2);
        heap.insert(2);
        List<Integer> newList = convertIList2List(heap);
        Collections.sort(newList);
        for (int i = 0; i < 5; i++) {
            assertEquals(newList.get(i), heap.removeMin());
        }       
    }
    
    @Test(timeout=SECOND)
    public void testManySameInput() {
        IPriorityQueue<Integer> heap = this.makeInstance(); 
        heap.insert(1);
        heap.insert(1);
        heap.insert(3);
        heap.insert(3);
        for (int i = 0; i < 100; i++) {
            heap.insert(2);
        }
        assertEquals(104, heap.size());
        List<Integer> newList = convertIList2List(heap);
        Collections.sort(newList);
        for (int i = 0; i < 104; i++) {
            assertEquals(newList.get(i), heap.removeMin());
        }     
        assertEquals(0, heap.size());
    }
    
    @Test(timeout=SECOND)
    public void testBasicRemove() {
        IPriorityQueue<Integer> heap = this.makeInstance(); 
        for (int i = 0; i < 10; i ++) {
            heap.insert(5-i);
        }
        List<Integer> newList = convertIList2List(heap);
        for (int i = 0; i < 10; i ++) {
            Integer m = heap.removeMin();
            assertEquals(9-i, heap.size());
            assertEquals(m, newList.get(i));
        }
        assertEquals(0, heap.size());
        
    }
    
    @Test(timeout=SECOND)
    public void testBasicPeek() {
        
    }
    
    @Test(timeout=SECOND)
    public void testRemoveFromEmpty() {
        
    }
    
    @Test(timeout=SECOND)
    public void testPeekFromEmpty() {
        
    }
    
    @Test(timeout=SECOND)
    public void testInsertPeekAndRemoveMany() {
        
    }
    
    @Test(timeout=SECOND)
    public void testSizeAfterRemove() {
        
    }
    
    @Test(timeout=SECOND)
    public void testSizeAfterPeek() {
        
    }

    @Test(timeout=SECOND)
    public void testOrderAfterRemove() {
        
    }
    
    @Test(timeout=SECOND)
    public void testOrderAfterRemoveMany() {
        
    }
    
    @Test(timeout=SECOND)
    public void testNegativeElements() {
        
    }
    
}
