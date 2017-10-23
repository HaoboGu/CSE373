package datastructures.concrete.dictionaries;

import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;

/**
 * See IDictionary for more details on what this class should do
 */
public class ArrayDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private Pair<K, V>[] pairs;

    // You're encouraged to add extra fields (and helper methods) though!
    private int currentSize;
    private int maxSize;
    // Constructor with maxSize
    public ArrayDictionary(int maxSize) {
        pairs = this.makeArrayOfPairs(maxSize);
        this.currentSize = 0;
        this.maxSize = maxSize;    
    }
    // Constructor without parameters
    public ArrayDictionary() {
        // If 
        pairs = this.makeArrayOfPairs(100);
        this.currentSize = 0;
        this.maxSize = 100; // default size of dictionary is 100
    }
    
    /**
     * This method will return a new, empty array of the given size
     * that can contain Pair<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private Pair<K, V>[] makeArrayOfPairs(int arraySize) {
        // It turns out that creating arrays of generic objects in Java
        // is complicated due to something known as 'type erasure'.
        //
        // We've given you this helper method to help simplify this part of
        // your assignment. Use this helper method as appropriate when
        // implementing the rest of this class.
        //
        // You are not required to understand how this method works, what
        // type erasure is, or how arrays and generics interact. Do not
        // modify this method in any way.
        return (Pair<K, V>[]) (new Pair[arraySize]);

    }

    @Override
    public V get(K key) {
        // Search for the key, then return the value
        for (int i = 0; i < currentSize; i++) {
            if (pairs[i].key == key || pairs[i].key.equals(key)) {
                return pairs[i].value;
            }
        }
        throw new NoSuchKeyException();        
    }

    @Override
    public void put(K key, V value) {
        // If the array is full, double maxSize of the array first
        if (currentSize == maxSize) {
            maxSize = maxSize * 2;
            Pair<K, V>[] newArray = this.makeArrayOfPairs(maxSize);
            // Move all elements from the old array to the new one
            for (int i = 0; i < currentSize; i++) {
                newArray[i] = new Pair<K, V>(pairs[i].key, pairs[i].value);
            }
            // Use the new one
            pairs = newArray;
        }
        // Search the key, if it is in the array, update value
        for (int i = 0; i < currentSize; i++) {
            if (pairs[i].key == key || pairs[i].key.equals(key)) {
                pairs[i].value = value;
                return;
            }
        }
        // If cannot find the key, add a new node
        pairs[currentSize] = new Pair<K, V>(key, value);
        currentSize++;
        
    }

    @Override
    public V remove(K key) {
        for (int i = 0; i < currentSize; i++) {
            if (pairs[i].key == key || pairs[i].key.equals(key)) {
                V tmp = pairs[i].value;  // Store the value first
                // Move the last node to this position and them delete the last node
                pairs[i].key = pairs[currentSize-1].key;  
                pairs[i].value = pairs[currentSize-1].value;
                currentSize--;
                return tmp;
            }
        }
        throw new NoSuchKeyException();
    }

    @Override
    public boolean containsKey(K key) {
        // Search the whole array
        for (int i = 0; i < currentSize; i++) {
            if (pairs[i].key == key || pairs[i].key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return currentSize;
    }

    private static class Pair<K, V> {
        public K key;
        public V value;
        
        // You may add constructors and methods to this class as necessary.
        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }
        
        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
    }
}
