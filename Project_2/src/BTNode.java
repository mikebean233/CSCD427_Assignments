/**
 * Created by michael on 4/21/17.
 */
public class BTNode {
	private NodeType _type;
	private int _n;
	private int _keyCount;
	private int _childCount;
	private int _recordCount;
	private String [] _keys;
	private BTNode [] _children;
	private BTRecord [] _records;

	public enum NodeType {
		ROOT,
		INTERIOR,
		LEAF
	}

	private enum ActionType {
		ADDKEY,
		ADDCHILD,
		REMOVEKEY,
		REMOVECHILD,
		CHANGETYPE
	}

	public BTNode(NodeType type, int n){
		if(type == null || n < 2)
			throw new IllegalArgumentException("BTNode");

		_type = type;
		_n = n;
		_keyCount = 0;
		_childCount = 0;
		//_recordCount = 0;
		_keys = new String[n - 1];
		_children = new BTNode[n];
		_records = new BTRecord[n];
	}

	public void removeKey(String key) throws InvariantViolationException {
		checkInvarient(ActionType.REMOVEKEY);
	}

	public void addKey(String key) throws InvariantViolationException {
		checkInvarient(ActionType.ADDKEY);
	}

	public void removeChild(String key) throws InvariantViolationException {
		checkInvarient(ActionType.REMOVEKEY);
	}

	public void addChild(String key) throws InvariantViolationException {
		checkInvarient(ActionType.ADDKEY);
	}

	private void checkInvarient(ActionType action) throws InvariantViolationException{checkInvarient(action, _type);}
	private void checkInvarient(ActionType action, NodeType type) throws InvariantViolationException{
		int testKeyCount   = _keyCount   + ((action == ActionType.ADDKEY)   ? 1 : (action == ActionType.REMOVEKEY)  ? -1 : 0);
		int testChildCount = _childCount + ((action == ActionType.ADDCHILD) ? 1 : (action == ActionType.REMOVECHILD ? -1 : 0));

		int keyCountMin   = (type == NodeType.ROOT) ? 1 : (type == NodeType.LEAF) ? _n / 2 : (_n / 2) - 1;
		int childCountMin = (type == NodeType.ROOT) ? 1 : _n/2;
		int keyCountMax   = _n - 1;
		int childCountMax = Math.min(_n, testKeyCount + 1);

		if(testKeyCount   < keyCountMin)   throw InvariantViolationException.buildException(InvariantViolationException.ViolationType.KEY_UNDERFILL  , keyCountMin);
		if(testKeyCount   > keyCountMax)   throw InvariantViolationException.buildException(InvariantViolationException.ViolationType.KEY_OVERFILL   , keyCountMax);
		if(testChildCount < childCountMin) throw InvariantViolationException.buildException(InvariantViolationException.ViolationType.CHILD_UNDERFILL, childCountMin);
		if(testChildCount > childCountMax) throw InvariantViolationException.buildException(InvariantViolationException.ViolationType.CHILD_OVERFILL , childCountMax);
	}

	public void setType(NodeType type){
		_type = type;
	}
}
