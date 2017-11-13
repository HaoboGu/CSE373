package datastructures.sorting;

import misc.BaseTest;
import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;
import misc.Searcher;
import static org.junit.Assert.fail;
import misc.exceptions.EmptyContainerException;
import org.junit.Test;

/**
 * See spec for details on what kinds of tests this class should include.
 */
public class TestTopKSortFunctionality extends BaseTest {
    @Test(timeout=SECOND)
    public void testSimpleUsage() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }

        IList<Integer> top = Searcher.topKSort(5, list);
        assertEquals(5, top.size());
        for (int i = 0; i < top.size(); i++) {
            assertEquals(15 + i, top.get(i));
        }
    }

    @Test(timeout=SECOND)
    public void testEmptyInputArray() {
        IList<Integer> list = new DoubleLinkedList<>();
        try {
            Searcher.topKSort(5, list);
            fail("Fail: receive empty list");
        }catch(EmptyContainerException ex) {
            // Fine
        }
    }

    @Test(timeout=SECOND)
    public void testNegativeK() {
        int negK = -5;
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }
        try {
            Searcher.topKSort(negK, list);
            fail("Fail: receive negative k");
        }catch(EmptyContainerException ex) {
            // Fine
        }
    }

    @Test(timeout=SECOND)
    public void testZeroK() {
        int zero = 0;
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }
        try {
            Searcher.topKSort(zero, list);
            fail("Fail: receive zero k");
        }catch(EmptyContainerException ex) {
            // Fine
        }
    }

    @Test(timeout=SECOND)
    public void testKGreaterThanSize() {
        int largeK = 100;
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }
        try {
            Searcher.topKSort(largeK, list);
            fail("Fail: receive zero k");
        }catch(EmptyContainerException ex) {
            // Fine
        }
    }

    @Test(timeout=SECOND)
    public void testNullPointer() {
        IList<Integer> list = null;
        try {
            Searcher.topKSort(5, list);
            fail("Fail: receive null list");
        }catch(EmptyContainerException ex) {
            // Fine
        }
    }
}
