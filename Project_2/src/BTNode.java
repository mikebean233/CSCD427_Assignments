import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by michael on 4/21/17.
 */
public class BTNode<K extends Comparable<K>, V> {
	private int _degree;
	private int _depth;
	private int _keyCount;
	private int _childCount;
	private int _recordCount;
	private int _id;
	private Comparable<K>[] _keys;
	private BTNode<K,V>[] _children;
	private BTNode<K,V> _next;
	private BTNode<K,V> _parent;
	private V[] _records;
	private boolean _isLeaf;
	private boolean _isRoot;

	private final int MIN = 0, MAX = 1;
	private static int instanceCount = 0;


	// limits
	private int[] _keyCountLimits = new int[2];
	private int[] _childCountLimits = new int[2];

	public enum FillState {
		OVERFILL,
		UNDERFILL,
		OK
	}

	public BTNode(int degree, int depth, boolean isLeaf, boolean isRoot, K[] keys, BTNode<K,V> []children, V[] records){
		this(degree, depth, isLeaf, isRoot);
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

	@SuppressWarnings("Unchecked")
	public BTNode(int degree, int depth, boolean isLeaf, boolean isRoot) {
		if ( degree < 1)
			throw new IllegalArgumentException("BTNode");

		_degree = degree;
		_depth = depth;
		_keys = new Comparable[degree + 1];
		_children = new BTNode[degree + 2];
		_records = (V[]) (new Object[degree + 1]);
		_keyCount = _childCount = _recordCount = 0;
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

	public V search(K key) {
		BTNode<K,V> leafNode = findLeafNode(key);
		if(leafNode == null)
			return null;

		return leafNode.findRecord(key);
	}

	private V findRecord(K key){
		assert _isLeaf : "An attempt was made to find a record in a non-leaf node";
		assert key != null : "findRecord cannot receive a null parameter";

		int recordIndex = findRecordIndex(key);
		if(recordIndex > 0)
			return _records[recordIndex];

		return null;
	}

	private int findRecordIndex(K key){
		assert key != null : "findRecordIndex cannot receive a null parameter";

		for (int i = 0; i < _keyCount; ++i) {
			if (_keys[i].equals(key))
				return i;
		}
		return -1;
	}



	private BTNode findLeafNode(K key) {
		if(_isLeaf)
			return this;

		for(int i = 0; i <= _keyCount; ++i){
			if( ( i < 1 || _keys[i - 1].compareTo(key) <= 0) && ( i >= _keyCount - 1 || _keys[i].compareTo(key) > 0))
				return _children[i].findLeafNode(key);
		}
		return null;
	}

	@SuppressWarnings("Unchecked")
	public V insert(K key, V value, boolean replace){
		V newValue = value;
		if(_isLeaf){
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





			return newValue;
		}

		return null;
	}

	@SuppressWarnings("Unchecked")
	public BTNode[] split() {
		assert getFillState() == FillState.OVERFILL;
		BTNode<K, V> newNode = new BTNode<>(_degree, _depth, _isLeaf, false);

		// Copy the keys and records over to the new nodes
		int pushUpIndex = (int) Math.ceil(_degree / 2.0);
		int outIndex = 0;
		int i = 0;
		for (; i <= _degree; ++i) {
			if(i >= pushUpIndex){
				outIndex = i - pushUpIndex;
				newNode._keys[outIndex] = _keys[i];
				newNode._children[outIndex] = _children[i];
				newNode._records[outIndex] = _records[i];
				_keys[i] = null;
				_children[i] = null;
				_records[i] = null;

				++newNode._keyCount;
				++newNode._childCount;
				++newNode._recordCount;
				--_keyCount;
				--_childCount;
				--_recordCount;
			}
		}
		newNode._children[i - pushUpIndex] = _children[i];
		_children[i] = null;
		++newNode._childCount;
		--_childCount;

		newNode._next = _next;
		_next = newNode;
		newNode._parent = _parent;

		return new BTNode[]{this, newNode};
	}

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
