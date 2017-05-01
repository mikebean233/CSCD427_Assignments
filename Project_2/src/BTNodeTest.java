import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Created by michael on 4/24/17.
 */
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
	public void insertTestLeaf(){


		/*
		 *  ROOT NODE, NO OVERFILL
		 */

		// No Keys
		BTNode<String, BTRecord> node = new BTNode<>(3, 0, true, true);
		assertTrue(Arrays.equals(new String[]{null, null, null, null}, node.getKeys()));
		assertTrue(Arrays.equals(new BTNode[]{null, null, null, null, null}, node.getChildren()));
		assertTrue(Arrays.equals(new BTRecord[]{null, null, null, null}, node.getRecords()));

		// out of order
		bulkInsert(Arrays.asList("z", "d", "m"), node);
		assertTrue(Arrays.equals(new String[]{"d", "m", "z",null}, node.getKeys()));
		assertTrue(Arrays.equals(new BTNode[]{null, null, null, null, null}, node.getChildren()));
		assertTrue(Arrays.equals(new BTRecord[]{testRecords[1], testRecords[1], testRecords[1], null}, node.getRecords()));

		// Duplicates
		bulkInsert(Arrays.asList("z", "d", "m", "a"), node);
		assertTrue(Arrays.equals(new String[]{"a", "d", "m", "z"}, node.getKeys()));
		assertTrue(Arrays.equals(new BTNode[]{null, null, null, null, null}, node.getChildren()));
		assertTrue(Arrays.equals(new BTRecord[]{testRecords[1], testRecords[2], testRecords[2], testRecords[2]}, node.getRecords()));

		/*
		 *  ROOT NODE, OVERFILL
		 */




	}

	@Test
	public void insertTestNonLeaf(){
		BTNode<String, BTRecord> node;



		// No Parent



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
		node = new BTNode<>(3,0,true, true);
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
		node = new BTNode<>(5,0,true, true);
		bulkInsert(Arrays.asList("c", "d", "e", "g", "i", "k"),node);

		nodes = node.split();
		assertTrue(Arrays.equals(new String[]{"c", "d", "e", null, null, null}, nodes[LEFT].getKeys()));
		assertTrue(Arrays.equals(new BTRecord[]{testRecords[1], testRecords[1], testRecords[1], null, null, null }, nodes[LEFT].getRecords()));

		assertTrue(Arrays.equals(new String[]{"g", "i", "k", null, null, null}, nodes[RIGHT].getKeys()));
		assertTrue(Arrays.equals(new BTRecord[]{testRecords[1], testRecords[1], testRecords[1], null, null, null}, nodes[LEFT].getRecords()));

		// --- Non-Leaf Node ---
		BTNode<String, BTRecord> [] leafs = new BTNode[]{
				new BTNode<String, BTRecord>(3, 2, true, false, new String[]{"a", "b"}, null, new BTRecord[]{testRecords[1], testRecords[1], null, null}),
				new BTNode<String, BTRecord>(3, 2, true, false, new String[]{"c", "d"}, null, new BTRecord[]{testRecords[1], testRecords[1], null, null}),
				new BTNode<String, BTRecord>(3, 2, true, false, new String[]{"e", "f"}, null, new BTRecord[]{testRecords[1], testRecords[1], null, null}),
				new BTNode<String, BTRecord>(3, 2, true, false, new String[]{"g", "h"}, null, new BTRecord[]{testRecords[1], testRecords[1], null, null}),
				new BTNode<String, BTRecord>(3, 2, true, false, new String[]{"i", "j"}, null, new BTRecord[]{testRecords[1], testRecords[1], null, null})
		};

		node = new BTNode<String, BTRecord>(
				3,
				1,
				false,
				false,
				new String[]{"c", "e", "g", "i"},
				leafs,
			null
		);

		nodes = node.split();
		assertTrue(Arrays.equals(  new String[]{        "c",         "e", null, null      }, nodes[LEFT].getKeys()));
		assertTrue(Arrays.equals(  new BTNode[]{leafs[0], leafs[1], null, null, null}, nodes[LEFT].getChildren()));
		assertTrue(Arrays.equals(new BTRecord[]{       null,        null, null, null      }, nodes[LEFT].getRecords()));
		assertEquals(node.getParent(), nodes[LEFT].getParent());
		assertEquals(node.getDegree(), nodes[LEFT].getDegree());

		assertTrue(Arrays.equals(  new String[]{     "g",      "i",     null, null      }, nodes[RIGHT].getKeys()));
		assertTrue(Arrays.equals(  new BTNode[]{leafs[2], leafs[3], leafs[4], null, null}, nodes[RIGHT].getChildren()));
		assertTrue(Arrays.equals(new BTRecord[]{    null,     null,     null, null      }, nodes[RIGHT].getRecords()));
		assertEquals(node.getParent(), nodes[RIGHT].getParent());
		assertEquals(node.getDegree(), nodes[RIGHT].getDegree());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void fillTest(){
		BTNode<String, BTRecord> node = new BTNode(3, 0, true, true);
		assertTrue(node.getFillState() == BTNode.FillState.UNDERFILL);

		node.insert("c", BTRecord.build(1), false);
		assertTrue(node.getFillState() == BTNode.FillState.OK);


		node.insert("a", BTRecord.build(1), false);
		node.insert("b", BTRecord.build(1), false);

		assertTrue(node.getFillState() == BTNode.FillState.OK);
		node.insert("d", BTRecord.build(), false);
		assertTrue(node.getFillState() == BTNode.FillState.OVERFILL);
	}

	private void bulkInsert(Collection<String> values, BTNode <String, BTRecord> node){
		assert values != null && node != null;

		for(String thisValue : values){
			BTRecord thisRecord = node.insert(thisValue, BTRecord.build(), false);
			if(thisRecord != null)
				thisRecord.incValue();
		}
	}
}
