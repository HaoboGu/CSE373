package datastructures.concrete;

import datastructures.interfaces.IList;
import misc.exceptions.EmptyContainerException;
// import misc.exceptions.NotYetImplementedException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Note: For more info on the expected behavior of your methods, see
 * the source code for IList.
 */
public class DoubleLinkedList<T> implements IList<T> {
    // You may not rename these fields or change their types.
    // We will be inspecting these in our private tests.
    // You also may not add any additional fields.
    private Node<T> front;

    private Node<T> back;
    private int size;

    public DoubleLinkedList() {
        this.front = null;
        this.back = null;
        this.size = 0;
    }

    @Override
    public void add(T item) {
        if (size == 0) { 
            // if list is empty, directly add the node and set front/back
            Node<T> node = new Node<T>(null, item, null);
            front = node;
            back = node;
            size++;	
        }
        else {
            // initialize node with prev=back and next=null
            Node<T> node = new Node<T>(back, item, null);
            // set previous back's next and then update back
            back.next = node;
            back = node;
            size++;
        }
    }

    @Override
    public T remove() {
        if (size == 0) {
            // nothing to remove
            throw new EmptyContainerException();
        }
        else {
            // store last element's data in tmp
            T tmp = back.data;
            // size equals 1, list becomes empty after remove operation
            if (size == 1){
                front = null;
                back = null;
            }
            else {
                // size > 1, update back and set new back's next to null
                back = back.prev;
                back.next = null;
            }
            size--;
            return tmp;
        }
    }

    @Override
    public T get(int index) {
        if (index < 0 || index>=size) {
            // index out of bounds
            throw new IndexOutOfBoundsException();
        }
        else {
            // initial a pointer
            Node<T> pointer = front;
            // search along the linde list from the front to back
            for (int i = 0; i < index; i++) {
                pointer = pointer.next;
            }
            return pointer.data;
        }

    }

    @Override
    public void set(int index, T item) {
        if (index < 0 || index>=size) {
            throw new IndexOutOfBoundsException();
        }
        //		else if (item == null) {
        //			return ;
        //		}
        else if (size == 1) {
            Node<T> replace = new Node<T>(front.prev, item, front.next);
            front = replace;
            back = replace;
        }
        else if (index == 0) {
            // set the first node, so directly use front to initialize new node
            Node<T> replace = new Node<T>(front.prev, item, front.next);
            // update next node's prev
            front.next.prev = replace;
            // update front
            front = replace;
        }
        else if (index == size-1) {
            // set the last node, so directly use back to initialize new node
            Node<T> replace = new Node<T>(back.prev, item, back.next);
            // update previous node's next
            back.prev.next = replace;
            // update back
            back = replace;
        }
        else {
            // find the node to replace from front to back
            Node<T> pointer = front;
            for (int i = 0; i < index; i++) {
                pointer = pointer.next;
            }
            // initialize the new node's prev and next using current node
            Node<T> replace = new Node<T>(pointer.prev, item, pointer.next);
            // update previou node's next and next node's prev
            pointer.prev.next = replace;
            pointer.next.prev = replace;
        }
    }

    @Override
    public void insert(int index, T item) {
        if (index < 0 || index >= size + 1) {
            throw new IndexOutOfBoundsException();
        }
        if (size == 0) {
            // the list is empty
            Node<T> current = new Node<T>(null, item, null);
            front = current;
            back = current;
            size++;
            return;
        }
        if (index == 0) {
            // insert to head of the list
            Node<T> current = new Node<T>(null, item, front);
            front.prev = current;
            front = current;
            size++;
            return;
     
        }
        if (index == size) {
            // insert to tail of the list
            Node<T> current = new Node<T>(back, item, null);
            back.next = current;
            back = current;
            size++;
            return;
        }
        
        // find the insert position from front to back
        if (index > 0.5*size) {
            Node<T> pointer = back;
            for (int i = 0; i < size - index; i++) {
                pointer = pointer.prev;
            }
            Node<T> current = new Node<T>(pointer, item, pointer.next);
            pointer.next.prev = current;
            pointer.next = current;
            size++;
        }
        else {
            Node<T> pointer = front;
            for (int i = 0; i < index; i++) {
                pointer = pointer.next;
            }
            Node<T> current = new Node<T>(pointer.prev, item, pointer);
            // insert between pointer and pointer.prev
            pointer.prev.next = current;
            pointer.prev = current;
            size++; 
        }    
    }

    @Override
    public T delete(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        else if (size == 1) {
            // delete the only element in list
            T value = front.data;
            front = null;
            back = null;
            size--;
            return value;
        }
        else if (index == 0){
            // delete the first node in list
            T value = front.data;
            front = front.next;
            front.prev = null;
            size--;
            return value;
        }
        else if (index == size-1) {
            // delete the last node in list
            T value = back.data;
            back = back.prev;
            back.next = null;
            size--;
            return value;
        }
        else {
            // find the node to be deleted from front to back
            Node<T> pointer = front;
            for (int i = 0; i < index; i++) {
                pointer = pointer.next;
            }
            // store the data to be deleted in tmp
            T tmp = pointer.data;
            // delete the pointer
            pointer.prev.next = pointer.next;
            pointer.next.prev = pointer.prev;
            pointer = null; // release the memory
            size--;
            return tmp;
        }
    }


    @Override
    public int indexOf(T item) {
        int i = 0;
        Node<T> current = front;
        while (current != null) {
            if (current.data == item || current.data.equals(item)) {
                return i;
            }
            else {
                i++;
                current = current.next;
            }
        }
        return -1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean contains(T other) {
        Node<T> current = front;
        while (current != null) {
            if (current.data == other || current.data.equals(other)) {
                return true;
            } else {
                current = current.next;
            }
        }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        // Note: we have provided a part of the implementation of
        // an iterator for you. You should complete the methods stubs
        // in the DoubleLinkedListIterator inner class at the bottom
        // of this file. You do not need to change this method.
        return new DoubleLinkedListIterator<>(this.front);
    }

    private static class Node<E> {
        // You may not change the fields in this node or add any new fields.
        public final E data;
        public Node<E> prev;
        public Node<E> next;

        public Node(Node<E> prev, E data, Node<E> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }

        public Node(E data) {
            this(null, data, null);
        }

        // Feel free to add additional constructors or methods to this class.

    }

    private static class DoubleLinkedListIterator<T> implements Iterator<T> {
        // You should not need to change this field, or add any new fields.
        private Node<T> current;

        public DoubleLinkedListIterator(Node<T> current) {
            // You do not need to make any changes to this constructor.
            this.current = current;
        }

        /**
         * Returns 'true' if the iterator still has elements to look at;
         * returns 'false' otherwise.
         */
        public boolean hasNext() {
            return !(current == null);
        }

        /**
         * Returns the next item in the iteration and internally updates the
         * iterator to advance one element forward.
         *
         * @throws NoSuchElementException if we have reached the end of the iteration and
         *         there are no more elements to look at.
         */
        public T next() {
            if (this.hasNext()) {
                T tmp = current.data;
                current = current.next;
                return tmp;
            }
            else {
                throw new NoSuchElementException();
            }
        }
    }
}
