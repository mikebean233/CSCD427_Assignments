import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

public class BTNodeTest {
	BTRecord[] testRecords = new BTRecord[]{
			BTRecord.build(0),
			BTRecord.build(1),
			BTRecord.build(2),
			BTRecord.build(3),
			BTRecord.build(4),
			BTRecord.build(5)
	};

	public BTNodeTest(){}

	@Test
	public void insertChildTest(){

	}

	@Test
	@SuppressWarnings("unchecked")
	public void insertRecordTest(){
		BTNode<String, BTRecord> node;

		/*
		 *  ROOT NODE, NO OVERFILL
		 */

		// No Keys
		node = BTNode.builder(3).isLeaf(true).keys(new String[]{"a","b"}).records(new BTRecord[]{testRecords[1], testRecords[1], null, null}).build();
		node = BTNode.builder(3).isLeaf(true).isRoot(true).build();
		node.setDebug(true);
		assertTrue(Arrays.equals(new String[]{null, null, null, null}, node.getKeys()));
		assertTrue(Arrays.equals(new BTNode[]{null, null, null, null, null}, node.getChildren()));
		assertTrue(Arrays.equals(new BTRecord[]{null, null, null, null}, node.getRecords()));

		// out of order
		bulkInsert(Arrays.asList("z", "d", "m"), node);
		node.setDebug(true);
		assertTrue(Arrays.equals(new String[]{"d", "m", "z",null}, node.getKeys()));
		assertTrue(Arrays.equals(new BTNode[]{null, null, null, null, null}, node.getChildren()));
		assertTrue(Arrays.equals(new BTRecord[]{testRecords[1], testRecords[1], testRecords[1], null}, node.getRecords()));

		// Duplicates
		bulkInsert(Arrays.asList("z", "d", "m", "a"), node);
		node.setDebug(true);
		assertTrue(Arrays.equals(new String[]{"a", "d", "m", "z"}, node.getKeys()));
		assertTrue(Arrays.equals(new BTNode[]{null, null, null, null, null}, node.getChildren()));
		assertTrue(Arrays.equals(new BTRecord[]{testRecords[1], testRecords[2], testRecords[2], testRecords[2]}, node.getRecords()));

		/*
		 *  ROOT NODE, OVERFILL
		 */

	}

	@Test
	@SuppressWarnings("unchecked")
	public void insertTestNonLeaf(){
		BTNode<String, BTRecord> node;
		BTNode<String, BTRecord> left   = BTNode.builder(3).isLeaf(true).keys(new String[]{"a","b"}).records(new BTRecord[]{testRecords[1], testRecords[1], null, null}).build();
		BTNode<String, BTRecord> middle = BTNode.builder(3).isLeaf(true).keys(new String[]{"c","d"}).records(new BTRecord[]{testRecords[1], testRecords[1], null, null}).build();
		BTNode<String, BTRecord> right  = BTNode.builder(3).isLeaf(true).keys(new String[]{"g","h"}).records(new BTRecord[]{testRecords[1], testRecords[1], null, null}).build();


		//= new BTNode<>(3, 1, true, false, new String[]{"a", "b"}, null, new BTRecord[]{testRecords[1], testRecords[1], null, null});
		//BTNode<String, BTRecord> middle = new BTNode<>(3, 1, true, false, new String[]{"c", "d"}, null, new BTRecord[]{testRecords[1], testRecords[1], null, null});
		//BTNode<String, BTRecord> right  = new BTNode<>(3, 1, true, false, new String[]{"g", "h"}, null, new BTRecord[]{testRecords[1], testRecords[1], null, null});

		//  ---- No Parent  ----

		// insert in the middle
		node = BTNode.Builder.buildNewRoot(right.getKeys()[0],left, right, null);
		node.insertChild((String)middle.getKeys()[0], middle);
		assertTrue(Arrays.equals(new BTNode[]{left, middle, right, null, null }, node.getChildren()));
		assertTrue(Arrays.equals(new String[]{"c","g", null, null}, node.getKeys()));
		assertTrue(node.getParent() == null);

		// insert on the right
		node = BTNode.Builder.buildNewRoot(middle.getKeys()[0],left, middle, null);
		node.insertChild((String)right.getKeys()[0], right);
		assertTrue(Arrays.equals(new BTNode[]{left, middle, right, null, null }, node.getChildren()));
		assertTrue(Arrays.equals(new String[]{"c","g", null, null}, node.getKeys()));
		assertTrue(node.getParent() == null);

		// insert on the left
		node = BTNode.Builder.buildNewRoot(right.getKeys()[0],middle, right, null);
		node.insertChild((String)middle.getKeys()[0], left);
		assertTrue(Arrays.equals(new BTNode[]{left, middle, right, null, null }, node.getChildren()));
		assertTrue(Arrays.equals(new String[]{"c","g", null, null}, node.getKeys()));
		assertTrue(node.getParent() == null);

		// Has Parent

	}

	@Test
	@SuppressWarnings("unchecked")
	public void splitTest(){
		final int LEFT = 0, RIGHT = 1;
		BTNode<String, BTRecord> node;
		BTNode<String, BTRecord>[] nodes;
		BTNode<String, BTRecord>[] children;

		// --- Leaf Node ---

		// n = 3
		node = BTNode.builder(3).isLeaf(true).isRoot(true).build(); //new BTNode<>(3,0,true, true);
		node.setDebug(true);
		bulkInsert(Arrays.asList("c", "d", "e", "g"),node);

		nodes = node.split();
		assertTrue(Arrays.equals(new String[]{"c", "d", null, null}, nodes[LEFT].getKeys()));
		assertTrue(Arrays.equals(new BTRecord[]{testRecords[1], testRecords[1], null, null }, nodes[LEFT].getRecords()));
		assertEquals(node.getParent(), nodes[LEFT].getParent());
		assertEquals(node.getDegree(), nodes[LEFT].getDegree());

		assertTrue(Arrays.equals(new String[]{"e", "g", null, null}, nodes[RIGHT].getKeys()));
		assertTrue(Arrays.equals(new BTRecord[]{testRecords[1], testRecords[1], null, null }, nodes[RIGHT].getRecords()));
		assertEquals(node.getParent(), nodes[RIGHT].getParent());
		assertEquals(node.getDegree(), nodes[RIGHT].getDegree());

		// n = 5
		node = BTNode.builder(5).isRoot(true).isLeaf(true).build(); //new BTNode<>(5,0,true, true);
		node.setDebug(true);
		bulkInsert(Arrays.asList("c", "d", "e", "g", "i", "k"),node);

		nodes = node.split();
		assertTrue(Arrays.equals(new String[]{"c", "d", "e", null, null, null}, nodes[LEFT].getKeys()));
		assertTrue(Arrays.equals(new BTRecord[]{testRecords[1], testRecords[1], testRecords[1], null, null, null }, nodes[LEFT].getRecords()));

		assertTrue(Arrays.equals(new String[]{"g", "i", "k", null, null, null}, nodes[RIGHT].getKeys()));
		assertTrue(Arrays.equals(new BTRecord[]{testRecords[1], testRecords[1], testRecords[1], null, null, null}, nodes[LEFT].getRecords()));

		// --- Non-Leaf Node ---
		BTNode<String, BTRecord> [] leafs = new BTNode[]{
				//new BTNode<String, BTRecord>(3, 2, true, false, new String[]{"a", "b"}, null, new BTRecord[]{testRecords[1], testRecords[1], null, null}),

				BTNode.builder(3).isLeaf(true).keys(new String[]{"a","b"}).records(new BTRecord[]{testRecords[1], testRecords[1], null, null}).build(),

				BTNode.builder(3).isLeaf(true).keys(new String[]{"c","d"}).records(new BTRecord[]{testRecords[1], testRecords[1], null, null}).build(),
				BTNode.builder(3).isLeaf(true).keys(new String[]{"e","f"}).records(new BTRecord[]{testRecords[1], testRecords[1], null, null}).build(),
				BTNode.builder(3).isLeaf(true).keys(new String[]{"g","h"}).records(new BTRecord[]{testRecords[1], testRecords[1], null, null}).build(),
				BTNode.builder(3).isLeaf(true).keys(new String[]{"i","j"}).records(new BTRecord[]{testRecords[1], testRecords[1], null, null}).build()



				//new BTNode<String, BTRecord>(3, 2, true, false, new String[]{"c", "d"}, null, new BTRecord[]{testRecords[1], testRecords[1], null, null}),
				//new BTNode<String, BTRecord>(3, 2, true, false, new String[]{"e", "f"}, null, new BTRecord[]{testRecords[1], testRecords[1], null, null}),
				//new BTNode<String, BTRecord>(3, 2, true, false, new String[]{"g", "h"}, null, new BTRecord[]{testRecords[1], testRecords[1], null, null}),
				//new BTNode<String, BTRecord>(3, 2, true, false, new String[]{"i", "j"}, null, new BTRecord[]{testRecords[1], testRecords[1], null, null})
		};

		/*
		node = new BTNode<String, BTRecord>(
				3,
				1,
				false,
				false,
				new String[]{"c", "e", "g", "i"},
				leafs,
			null
		);
*/

		node = BTNode.builder(3).keys(new String[]{"c", "e", "g", "i"}).children(leafs).build();


		nodes = node.split();
		assertTrue(Arrays.equals(  new String[]{     "c",      "e",     null, null      }, nodes[LEFT].getKeys()));
		assertTrue(Arrays.equals(  new BTNode[]{leafs[0], leafs[1], leafs[2], null, null      }, nodes[LEFT].getChildren()));
		assertTrue(Arrays.equals(new BTRecord[]{    null,     null,     null, null      }, nodes[LEFT].getRecords()));

		assertTrue(Arrays.equals(  new String[]{     "i",     null, null, null      }, nodes[RIGHT].getKeys()));
		assertTrue(Arrays.equals(  new BTNode[]{leafs[3], leafs[4], null, null, null}, nodes[RIGHT].getChildren()));
		assertTrue(Arrays.equals(new BTRecord[]{    null,     null, null, null      }, nodes[RIGHT].getRecords()));

		assertEquals(nodes[LEFT].getParent(), nodes[RIGHT].getParent());
		assertNotEquals(nodes[LEFT].getParent(), null);
		assertEquals(nodes[LEFT].getDegree(), nodes[RIGHT].getDegree());
		assertEquals(nodes[LEFT].getNext(), nodes[RIGHT]);

		BTNode parent = nodes[LEFT].getParent();

		assertTrue(Arrays.equals( new String[]{"g", null, null, null}, parent.getKeys()));
		assertTrue(Arrays.equals(new BTNode[]{nodes[LEFT], nodes[RIGHT], null, null, null}, parent.getChildren()));
		assertFalse(parent.isLeaf());
		assertTrue(parent.isRoot());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void fillRecordTest(){
		BTNode<String, BTRecord> node = BTNode.builder(3).isLeaf(true).isRoot(true).build();//new BTNode(3, 0, true, true);
		node.setDebug(true);
		assertTrue(node.getFillState() == BTNode.FillState.UNDERFILL);
		assertNull(node.getFirstKey());
		assertNull(node.getLastKey());

		node.insertRecord("c", BTRecord.build(1), false);
		assertTrue(node.getFillState() == BTNode.FillState.OK);
		assertEquals(node.getFirstKey(), "c");
		assertEquals(node.getLastKey(), "c");

		node.insertRecord("a", BTRecord.build(1), false);
		node.insertRecord("b", BTRecord.build(1), false);

		assertTrue(node.getFillState() == BTNode.FillState.OK);
		node.insertRecord("d", BTRecord.build(), false);
		assertTrue(node.getFillState() == BTNode.FillState.OVERFILL);
		assertEquals(node.getFirstKey(), "a");
		assertEquals(node.getLastKey(), "d");
	}

	private void bulkInsert(Collection<String> values, BTNode <String, BTRecord> node){
		assert values != null && node != null;

		for(String thisValue : values){
			node.put(thisValue, BTRecord.build(0)).incFrequency();
		}
	}
}
