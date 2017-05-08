import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;


public class BPlusTreeGenericTest {
	BTRecord[] testRecords = new BTRecord[]{
			BTRecord.build(0),
			BTRecord.build(1),
			BTRecord.build(2),
			BTRecord.build(3),
			BTRecord.build(4),
			BTRecord.build(5)
	};

	HashMap<String, Object> testObjects;
	String[] testStrings;
	String[] testStringsRandom;

	public BPlusTreeGenericTest(){
		Random random = new Random();
		HashSet<Integer> chosenIndexes = new HashSet<>();

		testStrings = new String[]{"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o"};
		testStringsRandom = new String[testStrings.length];
		for(int i = 0; i < testStringsRandom.length; ++i){
			int thisIndex;
			String thisValue = testStrings[i];
			while(chosenIndexes.contains(thisIndex = random.nextInt(testStringsRandom.length)))
			{}

			chosenIndexes.add(thisIndex);
			testStringsRandom[thisIndex] = thisValue;
		}

		testObjects = new HashMap<>();
		for(String thisElement: testStrings)
			testObjects.put(thisElement, new Object());
	}

	@Test
	public void sizeTest() {
		BPlusTreeGeneric<String, Object> tree = new BPlusTreeGeneric<>(3);
		assertEquals(tree.size(), 0);

		tree.put("a", testObjects.get("a"));
		assertEquals(tree.size(), 1);

		tree.put("b", testObjects.get("a"));
		assertEquals(tree.size(), 2);

		tree.put("a", testObjects.get("c"));
		assertEquals(tree.size(), 2);
	}

	@Test
	public void isEmptyTest() {
		BPlusTreeGeneric<String, Object> tree = new BPlusTreeGeneric<>(3);
		assertTrue(tree.isEmpty());

		tree.put("a", testObjects.get("a"));
		assertFalse(tree.isEmpty());
	}

	@Test
	public void getTest() {
		Object temp = new Object();

		BPlusTreeGeneric<String, Object> tree = new BPlusTreeGeneric<>(3);
		tree.put("a", testObjects.get("a"));
		tree.put("b", testObjects.get("b"));
		tree.put("c", testObjects.get("c"));
		tree.put("d", testObjects.get("d"));

		assertEquals(tree.get("a"), testObjects.get("a"));
		assertEquals(tree.get("b"), testObjects.get("b"));
		assertEquals(tree.get("c"), testObjects.get("c"));
		assertEquals(tree.get("d"), testObjects.get("d"));
		assertEquals(tree.get("n"), null);
	}

	@Test
	public void putTest() {
		Object temp = new Object();

		BPlusTreeGeneric<String, Object> tree = new BPlusTreeGeneric<>(3);
		assertEquals(tree.put("a", testObjects.get("a")), testObjects.get("a"));
		assertEquals(tree.put("b", testObjects.get("b")), testObjects.get("b"));
		assertEquals(tree.put("c", testObjects.get("c")), testObjects.get("c"));
		assertEquals(tree.put("d", testObjects.get("d")), testObjects.get("d"));
		assertEquals(tree.put("e", testObjects.get("e")), testObjects.get("e"));
		assertEquals(tree.put("a", temp                ), testObjects.get("a"));
		assertEquals(tree.put("b", temp                ), testObjects.get("b"));
		assertEquals(tree.put("c", temp                ), testObjects.get("c"));
	}

	@Test
	public void searchTest(){
		BPlusTreeGeneric<String, Object> tree = new BPlusTreeGeneric<>(3);
		tree.put("a", testObjects.get("a"));
		tree.put("b", testObjects.get("b"));
		tree.put("c", testObjects.get("c"));
		tree.put("d", testObjects.get("d"));
		tree.put("e", testObjects.get("e"));
		tree.put("f", testObjects.get("f"));
		tree.put("g", testObjects.get("g"));

		ArrayList<String> targetKeys = new ArrayList<>();
		ArrayList<Object> targetValues = new ArrayList<>();

		for(Map.Entry<String, Object> thisEntry : tree.search("c", "f")){
			targetKeys.add(thisEntry.getKey());
			targetValues.add(thisEntry.getValue());
		}

		assertTrue(Arrays.equals(new String[]{"c", "d", "e", "f"}, targetKeys.toArray()));
		assertTrue(Arrays.equals(new Object[]{testObjects.get("c"), testObjects.get("d"), testObjects.get("e"), testObjects.get("f") }, targetValues.toArray()));


		targetKeys.clear();
		targetValues.clear();
		for(Map.Entry<String, Object> thisEntry : tree.search("a", "c")){
			targetKeys.add(thisEntry.getKey());
			targetValues.add(thisEntry.getValue());
		}

		assertTrue(Arrays.equals(new String[]{"a", "b", "c"}, targetKeys.toArray()));
		assertTrue(Arrays.equals(new Object[]{testObjects.get("a"), testObjects.get("b"), testObjects.get("c")}, targetValues.toArray()));

		targetKeys.clear();
		targetValues.clear();
		for(Map.Entry<String, Object> thisEntry : tree.search("e", "z")){
			targetKeys.add(thisEntry.getKey());
			targetValues.add(thisEntry.getValue());
		}

		assertTrue(Arrays.equals(new String[]{"e", "f", "g"}, targetKeys.toArray()));
		assertTrue(Arrays.equals(new Object[]{testObjects.get("e"), testObjects.get("f"), testObjects.get("g")}, targetValues.toArray()));





	}



	@Test
	public void containsKeyTest(){
		Object temp = new Object();

		BPlusTreeGeneric<String, Object> tree = new BPlusTreeGeneric<>(3);
		tree.put("a", testObjects.get("a"));
		tree.put("b", testObjects.get("b"));
		tree.put("c", testObjects.get("c"));
		tree.put("d", testObjects.get("d"));

		assertTrue(tree.containsKey("a"));
		assertTrue(tree.containsKey("b"));
		assertTrue(tree.containsKey("c"));
		assertTrue(tree.containsKey("d"));
		assertFalse(tree.containsKey("g4"));
	}

	@Test
	public void iteratorTest() {
		BPlusTreeGeneric<String, Integer> tree = new BPlusTreeGeneric<>(3);

		for(int i = 0; i < testStrings.length; ++i){
			tree.put(testStringsRandom[i], new Integer(i));
		}

		List<String> randomStringList  = Arrays.asList(testStringsRandom);

		Iterator<Map.Entry<String, Integer>> iterator = tree.iterator();
		int index = 0;
		while(iterator.hasNext()){
			Map.Entry<String, Integer> thisEntry = iterator.next();
			assertEquals(thisEntry.getKey(), testStrings[index]);
			assertEquals(thisEntry.getValue(), new Integer(randomStringList.indexOf(thisEntry.getKey())) );
			++index;
		}
		assertEquals(index, testStringsRandom.length);
		assertFalse(iterator.hasNext());
	}
}