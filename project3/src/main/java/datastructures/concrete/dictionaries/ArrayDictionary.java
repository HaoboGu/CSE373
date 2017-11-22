package datastructures.concrete.dictionaries;

import java.util.Iterator;
import java.util.NoSuchElementException;

import datastructures.concrete.KVPair;
//import datastructures.concrete.DoubleLinkedList.Node;
import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;

/**
 * See IDictionary for more details on what this class should do
 */
public class ArrayDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private KVPair<K, V>[] pairs;

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
        this.maxSize = 10; // default size of dictionary is 10
    }
    
    /**
     * This method will return a new, empty array of the given size
     * that can contain Pair<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private KVPair<K, V>[] makeArrayOfPairs(int arraySize) {
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
        return (KVPair<K, V>[]) (new KVPair[arraySize]);

    }

    @Override
    public V get(K key) {
        // Search for the key, then return the value
        for (int i = 0; i < currentSize; i++) {
            if (pairs[i].getKey() == null) {
                if (key == null) {
                    return pairs[i].getValue();
                }
            }
            else if (pairs[i].getKey() == key || pairs[i].getKey().equals(key)) {
                return pairs[i].getValue();
            }
        }
        throw new NoSuchKeyException();        
    }

    @Override
    public void put(K key, V value) {
        // If the array is full, double maxSize of the array first
        if (currentSize >= 0.25 * maxSize) {
            maxSize = maxSize * 2;
            KVPair<K, V>[] newArray = this.makeArrayOfPairs(maxSize);
            // Move all elements from the old array to the new one
            for (int i = 0; i < currentSize; i++) {
                newArray[i] = new KVPair<K, V>(pairs[i].getKey(), pairs[i].getValue());
            }
            // Use the new one
            pairs = newArray;
        }
        // Search the key, if it is in the array, update value
        for (int i = 0; i < currentSize; i++) {
            if (pairs[i].getKey() == null) {
                if (key == null) {
                    pairs[i] = new KVPair<K, V>(pairs[i].getKey(), value);
                    return;
                }
            }
            else if (pairs[i].getKey() == key || pairs[i].getKey().equals(key)) {
                pairs[i] = new KVPair<K, V>(pairs[i].getKey(), value);
                return;
            }
        }
        // If cannot find the key, add a new node
        pairs[currentSize] = new KVPair<K, V>(key, value);
        currentSize++;
        
    }

    @Override
    public V remove(K key) {
        for (int i = 0; i < currentSize; i++) {
            if (pairs[i].getKey() == null) {
                if (key == null) {
                    V tmp = pairs[i].getValue();  // Store the value first
                    // Move the last node to this position and them delete the last node                   
                    pairs[i] = new KVPair<K, V>(pairs[currentSize-1].getKey(), pairs[currentSize-1].getValue());
                    currentSize--;
                    return tmp;
                }
            }
            else if (pairs[i].getKey() == key || pairs[i].getKey().equals(key)) {
                V tmp = pairs[i].getValue();  // Store the value first
                // Move the last node to this position and them delete the last node
                pairs[i] = new KVPair<K, V>(pairs[currentSize-1].getKey(), pairs[currentSize-1].getValue());
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
            if (pairs[i].getKey() == null) {
                return key == null;
            }
            else if (pairs[i].getKey() == key || pairs[i].getKey().equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return currentSize;
    }
    public Iterator<KVPair<K, V>> iterator() {       
        return new DictionaryIterator<K, V>(this.pairs, this.size());
    }
    private static class DictionaryIterator<K, V> implements Iterator<KVPair<K, V>> {
        // You should not need to change this field, or add any new fields.
        private KVPair<K, V>[] current;
        int index;
        int pairsSize;

        public DictionaryIterator(KVPair<K, V>[] pairs, int size){
            // You do not need to make any changes to this constructor.
            this.current = pairs;
            this.index = 0;
            this.pairsSize = size;
        }

        /**
         * Returns 'true' if the iterator still has elements to look at;
         * returns 'false' otherwise.
         */
        public boolean hasNext() {
            return !(this.index >= this.pairsSize);
        }

        /**
         * Returns the next item in the iteration and internally updates the
         * iterator to advance one element forward.
         */
        public KVPair<K, V> next() {
            if (this.hasNext()) {
                KVPair<K, V> tmp = current[index];
                index++;
                return tmp;
            }
            else {
                throw new NoSuchElementException();
            }
        }
    }
    /*    private static class Pair<K, V> {
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
  }*/

    
}
