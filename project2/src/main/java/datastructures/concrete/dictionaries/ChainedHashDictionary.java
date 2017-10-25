package datastructures.concrete.dictionaries;

import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * See the spec and IDictionary for more details on what each method should do
 */
public class ChainedHashDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private IDictionary<K, V>[] chains;

    // You're encouraged to add extra fields (and helper methods) though!
    private int currentSize;
    private int maxSize;
    @SuppressWarnings("unchecked")
    public ChainedHashDictionary() {
        // Constructor
        this.maxSize = 100;  // Default maxSize is 100
        this.currentSize = 0;
        this.chains = (IDictionary<K, V>[]) new IDictionary[this.maxSize];
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain IDictionary<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private IDictionary<K, V>[] makeArrayOfChains(int size) {
        // Note: You do not need to modify this method.
        // See ArrayDictionary's makeArrayOfPairs(...) method for
        // more background on why we need this method.
        return (IDictionary<K, V>[]) new IDictionary[size];
    }
    private int hash(int hashCode) {
        return (Math.abs(hashCode) % this.maxSize);
    }
    
    @Override
    public V get(K key) {
        int hashedPos = 0;
        if (key != null) {         
            hashedPos = hash(key.hashCode());
        }
        if (this.chains[hashedPos] != null) {
            if (this.chains[hashedPos].containsKey(key)) {
                return this.chains[hashedPos].get(key);
            }
        }   
        throw new NoSuchKeyException();
    }
    
    @Override
    public void put(K key, V value) {
        int hashedPos = 0;
        if (key != null) {         
            hashedPos = hash(key.hashCode());
        }
        if (this.chains[hashedPos] == null) {
            this.chains[hashedPos] = new ArrayDictionary<K, V>();
        }
        int preLength = this.chains[hashedPos].size();
        this.chains[hashedPos].put(key, value);
        if (this.chains[hashedPos].size() > preLength) {
            this.currentSize++;
        }
        if (currentSize > 0.7 * this.maxSize) {
            // resize and rehash
            this.maxSize = this.maxSize * 2;
            IDictionary<K, V>[] tmp = this.chains;
            this.chains = makeArrayOfChains(this.maxSize);
            for (int i = 0; i < this.maxSize/2; i++) {
                if (tmp[i] != null) {
                    for (KVPair<K, V> item : tmp[i]) {
                        int newHashedPos = 0;
                        if (item.getKey() != null) {         
                            newHashedPos = hash(item.getKey().hashCode());
                        }
                        if (this.chains[newHashedPos] == null) {
                            this.chains[newHashedPos] = new ArrayDictionary<K, V>();
                        }
                        this.chains[newHashedPos].put(item.getKey(), item.getValue());
                    }
                }
            }
        }
    }

    @Override
    public V remove(K key) {
        int hashedPos = 0;
        if (key != null) {         
            hashedPos = hash(key.hashCode());
        }
        if (this.chains[hashedPos] != null) {
            if (this.chains[hashedPos].containsKey(key)) {
                V value = this.chains[hashedPos].remove(key);
                this.currentSize--;
                return value;          
            }
        }
        throw new NoSuchKeyException();
    }

    @Override
    public boolean containsKey(K key) {
        int hashedPos = 0;
        if (key != null) {         
            hashedPos = hash(key.hashCode());
        }
        if (this.chains[hashedPos] != null) {
            if (this.chains[hashedPos].containsKey(key)) {
                return true;
            }
        }   
        return false;
    }

    @Override
    public int size() {
        return this.currentSize;
    }

    @Override
    public Iterator<KVPair<K, V>> iterator() {
        // Note: you do not need to change this method
        return new ChainedIterator<>(this.chains);
    }

    /**
     * Hints:
     *
     * 1. You should add extra fields to keep track of your iteration
     *    state. You can add as many fields as you want. If it helps,
     *    our reference implementation uses three (including the one we
     *    gave you).
     *
     * 2. Before you try and write code, try designing an algorithm
     *    using pencil and paper and run through a few examples by hand.
     *
     * 3. Think about what exactly your *invariants* are. An *invariant*
     *    is something that must *always* be true once the constructor is
     *    done setting up the class AND must *always* be true both before and
     *    after you call any method in your class.
     *
     *    Once you've decided, write them down in a comment somewhere to
     *    help you remember.
     *
     *    You may also find it useful to write a helper method that checks
     *    your invariants and throws an exception if they're violated.
     *    You can then call this helper method at the start and end of each
     *    method if you're running into issues while debugging.
     *
     *    (Be sure to delete this method once your iterator is fully working.)
     *
     * Implementation restrictions:
     *
     * 1. You **MAY NOT** create any new data structures. Iterators
     *    are meant to be lightweight and so should not be copying
     *    the data contained in your dictionary to some other data
     *    structure.
     *
     * 2. You **MAY** call the `.iterator()` method on each IDictionary
     *    instance inside your 'chains' array, however.
     */
    private static class ChainedIterator<K, V> implements Iterator<KVPair<K, V>> {
        private IDictionary<K, V>[] chains;
        private int currentChain;
        private Iterator<KVPair<K, V>> currentIterator;
        public ChainedIterator(IDictionary<K, V>[] chains) {
            this.chains = chains;
            this.currentChain = 0;
            while (this.currentChain < this.chains.length) {
                if (this.chains[this.currentChain] == null) {
                    this.currentChain++;
                }
                else {
                    break;
                }                
            }
            if (this.currentChain < this.chains.length) {
                this.currentIterator = this.chains[this.currentChain].iterator();
            }
        }
        
        @Override
        public boolean hasNext() {
            if (this.currentChain >= this.chains.length) {
                return false;
            }

            if (this.currentIterator.hasNext()) {        
                return true;
            }
            else {
                int index = this.currentChain + 1;
                while (index < this.chains.length) {
                    if (this.chains[index] == null) {
                        index++;
                    }
                    else {
                        break;
                    }                
                }
                return !(index >= this.chains.length);          
            }
        }

        @Override
        public KVPair<K, V> next() {
            if (this.currentChain >= this.chains.length) {
                throw new NoSuchElementException();
            }
            else if (this.currentIterator.hasNext()) {
                return this.currentIterator.next();
            }
            else {
                this.currentChain++;
                while (currentChain < this.chains.length) {
                    if (this.chains[this.currentChain] == null) {
                        this.currentChain++;
                    }
                    else {
                        break;
                    }                
                }
                
                if (currentChain >= this.chains.length) {
                    throw new NoSuchElementException();
                }
                else {
                    this.currentIterator = this.chains[this.currentChain].iterator();
                    return this.currentIterator.next();
                }
            }
        }
    }
}
