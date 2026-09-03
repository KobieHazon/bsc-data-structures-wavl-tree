import java.util.Arrays;
import java.util.TreeMap;

public class RunWavlChecks {
    public static void main(String[] args) {
        testEmptyTree();
        testInsertSearchAndSelect();
        testDeleteAgainstTreeMap();
        System.out.println("All WAVL checks passed");
    }

    private static void testEmptyTree() {
        WAVLTree tree = new WAVLTree();
        check(tree.empty(), "new tree is empty");
        checkEquals(0, tree.size(), "empty size");
        checkEquals(null, tree.min(), "empty min");
        checkEquals(null, tree.max(), "empty max");
        checkEquals(null, tree.search(10), "empty search");
        checkEquals(null, tree.select(1), "empty select");
        check(Arrays.equals(new int[0], tree.keysToArray()), "empty keys array");
        check(Arrays.equals(new String[0], tree.infoToArray()), "empty values array");
    }

    private static void testInsertSearchAndSelect() {
        WAVLTree tree = new WAVLTree();
        TreeMap<Integer, String> expected = new TreeMap<>();
        int[] keys = {5, 2, 8, 1, 3, 7, 9, 6, 4};
        for (int key : keys) {
            int operations = tree.insert(key, "v" + key);
            check(operations >= 0, "insert returns non-negative rebalance count for key " + key);
            expected.put(key, "v" + key);
            assertMatches(tree, expected);
        }
        checkEquals(-1, tree.insert(3, "duplicate"), "duplicate insert fails");
        checkEquals("v3", tree.search(3), "duplicate insert keeps old value");
        checkEquals("v1", tree.select(1), "select minimum");
        checkEquals("v5", tree.select(5), "select middle");
        checkEquals("v9", tree.select(9), "select maximum");
        checkEquals(null, tree.select(0), "select below range");
        checkEquals(null, tree.select(10), "select above range");
    }

    private static void testDeleteAgainstTreeMap() {
        WAVLTree tree = new WAVLTree();
        TreeMap<Integer, String> expected = new TreeMap<>();
        for (int key : new int[] {20, 10, 30, 5, 15, 25, 35, 12, 17, 27}) {
            tree.insert(key, "v" + key);
            expected.put(key, "v" + key);
        }
        for (int key : new int[] {20, 5, 35, 15, 12, 10, 17, 25, 27, 30}) {
            int operations = tree.delete(key);
            check(operations >= 0, "delete returns non-negative rebalance count for key " + key);
            expected.remove(key);
            assertMatches(tree, expected);
        }
        check(tree.empty(), "tree is empty after deleting all keys");
        checkEquals(-1, tree.delete(999), "missing delete fails");
    }

    private static void assertMatches(WAVLTree tree, TreeMap<Integer, String> expected) {
        checkEquals(expected.isEmpty(), tree.empty(), "empty flag");
        checkEquals(expected.size(), tree.size(), "tree size");
        check(Arrays.equals(expected.keySet().stream().mapToInt(Integer::intValue).toArray(), tree.keysToArray()),
                "keys are sorted and complete");
        check(Arrays.equals(expected.values().toArray(new String[0]), tree.infoToArray()),
                "values follow sorted-key order");
        checkEquals(expected.isEmpty() ? null : expected.firstEntry().getValue(), tree.min(), "minimum value");
        checkEquals(expected.isEmpty() ? null : expected.lastEntry().getValue(), tree.max(), "maximum value");
        int index = 1;
        for (String value : expected.values()) {
            checkEquals(value, tree.select(index), "select " + index);
            index++;
        }
        for (Integer key : expected.keySet()) {
            checkEquals(expected.get(key), tree.search(key), "search key " + key);
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void checkEquals(Object expected, Object actual, String message) {
        if (expected == null) {
            if (actual != null) {
                throw new AssertionError(message + ": expected null but got " + actual);
            }
            return;
        }
        if (!expected.equals(actual)) {
            throw new AssertionError(message + ": expected " + expected + " but got " + actual);
        }
    }
}
