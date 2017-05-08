//import com.sun.javaws.exceptions.InvalidArgumentException;

import java.util.*;

/**
 * Created by michael on 4/21/17.
 */
public class BTNode<K extends Comparable<K>, V> implements Comparable<BTNode<K,V>>, Iterable<Map.Entry<K,V>> {
	private int _degree, _keyCount, _childCount, _recordCount, _id;
	private K[] _keys;
	private K _pushUpValue;
	private BTNode<K,V>[] _children;
	private BTNode<K,V> _next, _parent;
	private V[] _records;
	private boolean _isLeaf, _isRoot, _debug;
	private BTNodeContainer<K,V> _container;
	private final int MIN = 0, MAX = 1, LEFT = 0, RIGHT = 1;
	private static int instanceCount = 0;

	// limits
	private int[] _keyCountLimits = new int[2];
	private int[] _childCountLimits = new int[2];

	@Override
	@SuppressWarnings("unchecked")
	public int compareTo(BTNode<K, V> that) {
		if(_keyCount <= 0)
			throw new IllegalStateException("compareTo");

		if(getLastKey().compareTo((K) that.getFirstKey()) < 0 ) {
			return -1;
		}

		if(getFirstKey().compareTo((K) that.getLastKey()) > 0 ) {
			return 1;
		}

		if (!false)
			throw new AssertionError("Node overlap: " + this + "              " + that);
		return 0;
	}

	public enum FillState {
		OVERFILL,
		UNDERFILL,
		OK
	}

	@Override
	public Iterator<Map.Entry<K,V>> iterator(){
		BTNode<K,V> thisNode = getRoot(this);

		while(!thisNode.isLeaf())
			thisNode = thisNode._children[0];

		return new BTNodeIterator(thisNode);
	}

	private class BTNodeIterator implements Iterator<Map.Entry<K,V>> {

		private int _nextIndex;
		private BTNode<K, V> _node;

		public BTNodeIterator(BTNode<K, V> node) {
			_node = node;
		}

		@Override
		public boolean hasNext() {
			return _node != null && _nextIndex < _node._keyCount;
		}

		@Override
		public Map.Entry<K, V> next() {
			if (!hasNext())
				throw new NoSuchElementException("BTNode.Itr.next()");

			Map.Entry<K, V> result = new Entry(_node._keys[_nextIndex], _node._records[_nextIndex]);
			if(_nextIndex + 1 >= _node._keyCount){
				_nextIndex = 0;
				_node = _node._next;
			} else {
				++_nextIndex;
			}

			return result;
		}
	}

	public class Entry implements Map.Entry<K, V> {
		private K _key;
		private V _value;

		public Entry(K key, V value) {
			if (key == null || value == null)
				throw new IllegalArgumentException("BTNode.Entry");

			_key = key;
			_value = value;
		}

		@Override
		public K getKey() {
			return _key;
		}

		@Override
		public V getValue() {
			return _value;
		}

		@Override
		public V setValue(V value) {
			throw new UnsupportedOperationException("BTNode.Entry.setFrequency()");
		}

		@Override
		public String toString(){
			String modifiedKey = (_key instanceof String) ? "\"" + _key + "\"" : _key + "";
			String modifiedValue = (_value instanceof String) ? "\"" + _value + "\"" : _value + "";
			return "{ " + " Class: BTNode.Entry, Key: " + modifiedKey + ", Value: " + modifiedValue + " }";
		}
	}


	public static <K,V> Builder builder(int degree){
		return new Builder(degree);
	}

	public static class Builder<K extends Comparable<K>,V> {
		private int _degree;
		private boolean _isLeaf, _isRoot;
		private K[] _keys;
		private V[] _records;
		private BTNode[] _children;
		private BTNodeContainer<K,V> _container;

		@SuppressWarnings("unchecked")
		public BTNode<K,V> build(){
			return new BTNode(_degree, _isLeaf, _isRoot, _keys, _children, _records, _container);
		}

		@SuppressWarnings("unchecked")
		public static BTNode buildNewRoot(Comparable key, BTNode first, BTNode second, BTNodeContainer container){
			if(key == null || first == null || second == null)
				throw new IllegalArgumentException("buildNewRoot");

			return new BTNode<>(first.getDegree(), false, true, new Comparable[]{key}, new BTNode[]{first, second}, null, container);
		}

		private Builder(int degree){
			_degree = degree;
		}

		public Builder<K,V> degree(int degree){
			_degree = degree;
			return this;
		}

		public Builder<K,V> isLeaf(boolean isLeaf){
			_isLeaf = isLeaf;
			return this;
		}

		public Builder<K,V> isRoot(boolean isRoot){
			_isRoot = isRoot;
			return this;
		}

		public Builder<K,V> keys(K[] keys){
			_keys = keys;
			return this;
		}

		public Builder<K,V> records(V[] records){
			_records = records;
			return this;
		}

		public Builder<K,V> children(BTNode[] children) {
			_children = children;
			return this;
		}

		public Builder<K,V> container(BTNodeContainer<K,V> container){
			_container = container;
			return this;
		}
	}

	private BTNode(int degree, boolean isLeaf, boolean isRoot, K[] keys, BTNode<K,V> []children, V[] records, BTNodeContainer<K,V> container){
		this(degree, isLeaf, isRoot, container);
		if((keys != null && keys.length > _keys.length) || (children != null && children.length > _children.length) ||(records != null && records.length > _records.length))
			throw new IllegalArgumentException("BTNode");

		for(int i = 0; i < degree + 2; ++i){
			if(keys != null && i < keys.length){
				_keys[i] = keys[i];
				++_keyCount;
			}
			if(children != null && i < children.length){
				_children[i] = children[i];
				++_childCount;
			}
			if(records != null && i < records.length){
				_records[i] = records[i];
				++_recordCount;
			}
		}
	}

	@SuppressWarnings("unchecked")
	private BTNode(int degree, boolean isLeaf, boolean isRoot, BTNodeContainer<K,V> container) {
		if ( degree < 1)
			throw new IllegalArgumentException("BTNode");

		_degree = degree;
		_keys = (K[]) new Comparable[degree + 1];
		_children = new BTNode[degree + 2];
		_records = (V[]) (new Object[degree + 1]);
		_keyCount = _childCount = _recordCount = 0;
		_container = container;
		setType(isLeaf, isRoot);

		_id = instanceCount++;
	}

	public boolean setParent(BTNode<K,V> parent){
		BTNode oldParent = _parent;
		return (_parent = parent) != oldParent;
	}

	public boolean setNext(BTNode<K,V> next){
		BTNode<K,V> oldNext = _next;
		return (_next = next) != oldNext;
	}

	public BTNode<K,V> getNext(){
		return _next;
	}

	@SuppressWarnings("unchecked")
	public V search(K key) {
		BTNode<K,V> leafNode = findLeafNode(key);
		if(leafNode == null)
			return null;

		return leafNode.findRecord(key);
	}

	public Collection<Map.Entry<K,V>> search(K lowerBound, K upperBound){
		if(lowerBound == null || upperBound == null || lowerBound.compareTo(upperBound) >= 0)
			throw new IllegalArgumentException("BTNode.search");


		BTNode<K,V> thisNode = getRoot(this);
		ArrayList<Map.Entry<K,V>> result = new ArrayList<>();

		thisNode.rangeSearch(lowerBound, upperBound, result);
		return result;
	}

	private void rangeSearch(K lowerBound, K upperBound, Collection<Map.Entry<K,V>> results){
		if (_isLeaf) {
			for (int i = 0; i < _keyCount; ++i) {
				K thisKey = _keys[i];
				V thisValue = _records[i];
				if (thisKey.compareTo(lowerBound) >= 0 && thisKey.compareTo(upperBound) <= 0)
					results.add(new Entry(thisKey, thisValue));
			}
		} else {
			for(int i = 0; i < _childCount; ++i) {
				if ((i == 0 || _keys[i - 1].compareTo(upperBound) <= 0) && (i == _keyCount || _keys[i].compareTo(lowerBound) >= 0)){
					_children[i].rangeSearch(lowerBound, upperBound, results);
				}
			}
		}
	}

	private V findRecord(Comparable<K> key){
		assert _isLeaf : "An attempt was made to find a record in a non-leaf node";
		assert key != null : "findRecord cannot receive a null parameter";

		int recordIndex = findRecordIndex(key);
		if(recordIndex >= 0)
			return _records[recordIndex];

		return null;
	}

	private int findRecordIndex(Comparable<K> key){
		assert key != null : "findRecordIndex cannot receive a null parameter";

		for (int i = 0; i < _keyCount; ++i) {
			if (_keys[i].equals(key))
				return i;
		}
		return -1;
	}

	@SuppressWarnings("unchecked")
	private BTNode findLeafNode(Comparable<K> key) {
		if(_isLeaf)
			return this;
		int i;
		for(i = 0; i < _keyCount; ++i){
			if(_keys[i].equals(key))
				return _children[i + 1].findLeafNode(key);

			if(key.compareTo((K)_keys[i]) < 0)
				return _children[i].findLeafNode(key);

			if(key.compareTo((K)_keys[i]) > 0 && (_keys[i + 1] == null || key.compareTo((K)_keys[i + 1]) < 0 ) )
				return _children[i + 1].findLeafNode(key);

		}
		return _children[i + 1].findLeafNode(key);
	}

	public boolean insertChild(K key, BTNode<K,V> child){
		if(key == null || child == null )
			throw new IllegalArgumentException("insertChild");

		// This records does not exist
		assert getFillState() != FillState.OVERFILL;
		assert !_isLeaf;
		boolean keySlotFound = false;
		boolean childSlotFound = false;
		K tempKey = key;
		BTNode<K,V> tempChild = child;

		int i = 0;
		for(; i <= _keyCount; ++i) {
			//make sure we aren't inserting a duplicate
			if(_keys[i] == key){
	        	if(!_children[i].equals(child))
					throw new IllegalStateException("insertChild");

				return false;
			}

			if(_children[i].compareTo(child) >= 0 ) {
				childSlotFound = true;
			}

			if(i < _keyCount) {
				if(_keys[i].compareTo(key) >= 0)
					keySlotFound = true;

				if (keySlotFound) {
					tempKey = (K) _keys[i];
					_keys[i] = key;
					key = tempKey;
				}
			}

			if(childSlotFound){
				tempChild = _children[i];
				_children[i] = child;
				child = tempChild;
			}
		}
		_keys[_keyCount] = tempKey;
		_children[i] = tempChild;
		_keyCount++;
		_childCount++;

		if(getFillState() == FillState.OVERFILL && !_debug){
			split();
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	public V put(K key, V value){
		BTNode<K,V> leafNode = findLeafNode(key);
		return (V)leafNode.insertRecord(key, value, false);
	}

	@SuppressWarnings("unchecked")
	public V insertRecord(K key, V value, boolean replace){
		V newValue = value;
		assert _isLeaf;
		int recordIndex = findRecordIndex(key);

			// This record already exists
			if(recordIndex >= 0){
				if(replace)
					_records[recordIndex] = value;

				return _records[recordIndex];
			}

			// This records does not exist
			assert getFillState() != FillState.OVERFILL;
			boolean foundSlot = false;
			K tempKey = key;
			V tempValue = value;
			for(int i = 0; i < _keyCount; ++i) {
				if(_keys[i].compareTo(key) > 0 )
					foundSlot = true;

				if(foundSlot){
					tempKey = (K)_keys[i];
					tempValue = _records[i];

					_keys[i] = key;
					_records[i] = value;
					key = tempKey;
					value = tempValue;
				}
			}
			_keys[_keyCount] = tempKey;
			_records[_keyCount] = tempValue;
			_keyCount++;
			_recordCount++;

			if(getFillState() == FillState.OVERFILL && !_debug){
				split();
			}

			if(_container != null)
				_container.incSize();

			return newValue;

	}

	@SuppressWarnings("unchecked")
	public BTNode[] split() {
		assert getFillState() == FillState.OVERFILL;
		BTNode<K, V> newNode = builder(_degree)
				.isLeaf(_isLeaf)
				.container(_container)
				.build();

		_isRoot = false;

		// Copy the keys and records over to the new nodes
		int pushUpIndex = (int) Math.ceil(_degree / 2.0);
		int keyInIndex = pushUpIndex;
		int childInIndex = pushUpIndex + 1;
		int recordInIndex = pushUpIndex;
		int keyCount = _keyCount, childCount = _childCount, recordCount = _recordCount;

		newNode._pushUpValue = (K) _keys[pushUpIndex];

		if (!_isLeaf) {
			_keys[keyInIndex++] = null;
			--_keyCount;
		}

		for (int i = 0; i <= _degree + 1; ++i) {
			if (keyInIndex < keyCount) {
				newNode._keys[i] = _keys[keyInIndex];
				++newNode._keyCount;

				_keys[keyInIndex] = null;
				--_keyCount;
				++keyInIndex;
			}

			if (childInIndex < childCount) {
				newNode._children[i] = _children[childInIndex];
				++newNode._childCount;

				_children[childInIndex] = null;
				--_childCount;
				++childInIndex;
			}

			if (recordInIndex < recordCount) {
				newNode._records[i] = _records[recordInIndex];
				++newNode._recordCount;

				_records[recordInIndex] = null;
				--_recordCount;
				++recordInIndex;
			}
		}

		// If this node was the root make a new root
		if (_parent == null) {
			_parent = Builder.buildNewRoot(newNode._pushUpValue, this, newNode, _container);
			if (_container != null)
				_container.setNode(_parent);

		} else {
			_parent.insertChild((K) newNode._pushUpValue, newNode);
		}

		newNode._parent = _parent;
		newNode._next = _next;
		_next = newNode;

		return new BTNode[]{this, newNode};
	}

	public boolean isLeaf(){return _isLeaf;}

	public boolean isRoot(){return _isRoot;}

	public Comparable<K> getFirstKey(){return _keys[0];}

	public Comparable<K> getLastKey(){return (_keyCount > 0) ? _keys[_keyCount - 1] : null;}

	public void setDebug(boolean debug){_debug = debug;}

	public V[] getRecords(){
		return Arrays.copyOf(_records, _records.length);
	}

	public BTNode<K,V> getParent(){return _parent;}

	public int getDegree(){return _degree;}

	public Comparable<K>[] getKeys(){
		return Arrays.copyOf(_keys, _keys.length);
	}

	public BTNode<K,V>[] getChildren(){
		return Arrays.copyOf(_children, _children.length);
	}

	public FillState getFillState(){
		FillState state = (_keyCount < _keyCountLimits[MIN]) ? FillState.UNDERFILL : (_keyCount > _keyCountLimits[MAX]) ? FillState.OVERFILL : FillState.OK;
		return state;
	}

	public FillState setType(boolean isLeaf, boolean isRoot) {
		_isLeaf = isLeaf;
		_isRoot = isRoot;
		_keyCountLimits[MIN]   = isRoot ? 1 : isLeaf ? Math.floorDiv(_degree, 2) : (int)Math.ceil(_degree / 2.0) - 1;
		_childCountLimits[MIN] = isRoot ? 1 : isLeaf ? Math.floorDiv(_degree, 2) : (int)Math.ceil(_degree / 2.0);

		_keyCountLimits[MAX] = _degree;
		_childCountLimits[MIN] = _degree + 1;

		return getFillState();
	}

	@Override
	public boolean equals(Object thatObject){
		if(thatObject == null || !(thatObject instanceof BTNode))
			return false;

		return ((BTNode) thatObject)._id == _id;
	}

	public String toString(int depth){
		StringBuilder sb = new StringBuilder();
		for(int i = depth; i > 0; --i){
			sb.append("\t");
		}
		sb.append(_id);
		sb.append(System.lineSeparator());

		if(!_isLeaf){
			for(int i = 0; i < _childCount; ++i){
				sb.append(_children[i].toString(depth + 1) );
			}
		}
		return sb.toString();
	}

	private BTNode<K,V> getRoot(BTNode<K,V> node){
		while(node._parent != null)
			node = node._parent;
		return node;
	}

	public BTNode<K,V> findNodeById(int id){
		BTNode<K,V> thisNode = getRoot(this);
		return thisNode.findNodeByIdInternal(id);
	}

	private BTNode<K,V> findNodeByIdInternal(int id){
		if(_id == id)
			return this;

		if(_isLeaf)
			return null;

		for(int i = 0; i < _childCount; ++i){
			BTNode<K,V> thisNode = _children[i];
			BTNode<K,V> thisSubtreeResult = thisNode.findNodeByIdInternal(id);
			if(thisSubtreeResult != null)
				return thisSubtreeResult;
		}
		return null;
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("{ Class: \"BTNode\", ");

		sb.append("Degree: ");
		sb.append(_degree);
		sb.append(", ");

		sb.append("IsLeaf : ");
		sb.append(_isLeaf);
		sb.append(", ");

		sb.append("IsRoot: ");
		sb.append(_isRoot);
		sb.append(", ");

		sb.append("InstanceId: ");
		sb.append(_id);
		sb.append(", ");

		sb.append("Keys: ");
		sb.append(buildJsonList(_keys));
		sb.append(", ");

		sb.append("Records: ");
		sb.append(buildJsonList(_records));
		sb.append(", ");

		sb.append("Children: ");
		sb.append(buildJsonList(_children));

		sb.append(" }");

		return sb.toString();
	}

	private String buildJsonList(List list){
		StringBuilder sb = new StringBuilder();
		int length = list.size();
		int index = 0;
		sb.append("[");
		for(Object thisItem : list){
			sb.append(" ");
			sb.append((thisItem == null) ? "null" : ((thisItem instanceof String) ? ("\"" + thisItem + "\"") : thisItem + ""));
			if(++index < length)
				sb.append(",");
		}
		sb.append(" ]");
		return sb.toString();
	}

	private String buildJsonList(Object[] list){
		if(list == null)
			return "[]";

		return buildJsonList(Arrays.asList(list));
	}

}
