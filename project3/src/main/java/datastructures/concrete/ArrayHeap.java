package datastructures.concrete;

import datastructures.interfaces.IPriorityQueue;
import misc.exceptions.EmptyContainerException;
import misc.exceptions.NotYetImplementedException;

import java.util.Arrays;

/**
 * See IPriorityQueue for details on what each method must do.
 */
public class ArrayHeap<T extends Comparable<T>> implements IPriorityQueue<T> {
    // See spec: you must implement a implement a 4-heap.
    private static final int NUM_CHILDREN = 4;

    // You MUST use this field to store the contents of your heap.
    // You may NOT rename this field: we will be inspecting it within
    // our private tests.
    private T[] heap;
    private int heapSize;
    private int arraySize;
    // Feel free to add more fields and constants.

    public ArrayHeap() {
        this.arraySize = 5;
        this.heap = makeArrayOfT(arraySize); // Initial size is 5.
        this.heapSize = 0;
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain elements of type T.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private T[] makeArrayOfT(int size) {
        // This helper method is basically the same one we gave you
        // in ArrayDictionary and ChainedHashDictionary.
        //
        // As before, you do not need to understand how this method
        // works, and should not modify it in any way.
        return (T[]) (new Comparable[size]);
    }

    @Override
    public T removeMin() {
        T item = null;
        this.heapSize--;
        return item;
    }

    @Override
    public T peekMin() {
       return this.heap[0]; //Index starting from 0
    }

    private void swap(int pos1, int pos2) {
        T tmp = this.heap[pos1];
        this.heap[pos1] = this.heap[pos2];
        this.heap[pos2] = tmp;
    }
    private void percolateUp() {
        // TODO: Check this method again
        // Percolate the last element up to a proper position
        int curPos = this.heapSize - 1;
        int prePos = (curPos - 1) / this.NUM_CHILDREN;  // Cal mother's index, index starting from 0
        while (prePos >= 0 || this.heap[curPos].compareTo(this.heap[prePos]) < 0) {
            swap(curPos, prePos);
            curPos = prePos;
            prePos = (curPos - 1) / this.NUM_CHILDREN;
        }

    }
    @Override
    public void insert(T item) {
        // Check whether the array is full
        if (this.heapSize == this.arraySize) {
            this.arraySize = this.arraySize*2;  // Double the array
            T[] newHeap = makeArrayOfT(this.arraySize);
            for (int i = 0; i < this.heap.length; i++) {
                newHeap[i] = this.heap[i];
            }
            this.heap = newHeap;
        }
        this.heap[this.heapSize] = item;
        this.heapSize++;
        this.percolateUp();
    }

    @Override
    public int size() {
        return this.heapSize;
    }
}
