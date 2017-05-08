import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

public class BPlusTreeGeneric<K extends Comparable<K>,V> implements Map<K,V>, Iterable <Map.Entry<K,V>> {
	protected int _degree, _size;
	protected BTNodeContainer<K,V> _nodeContainer;

	@SuppressWarnings("unchecked")
	public BPlusTreeGeneric(int degree){
		if(degree < 2 )
			throw new IllegalArgumentException("BPlusTreeGeneric");

		_degree = degree;

		//_nodeContainer = new BTNode<>(degree, 0, true,true);
		_nodeContainer = new BTNodeContainer<>();
		_nodeContainer.setNode( BTNode.builder(degree).isLeaf(true).isRoot(true).container(_nodeContainer).build() );

	}

	@Override
	public int size() {
		return _nodeContainer.getSize();
	}

	@Override
	public boolean isEmpty() {
		return _nodeContainer.getSize() == 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean containsKey(Object key) {
		V value = _nodeContainer.getNode().search((K)key);
		return value != null;
	}

	@Override
	public boolean containsValue(Object value) {
		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public V get(Object key) {
		return _nodeContainer.getNode().search((Comparable<K>)key);
	}

	@Override
	public V put(K key, V value) {
		return _nodeContainer.getNode().put(key, value);
	}

	@Override
	public V remove(Object key) {
		throw new UnsupportedOperationException("remove");
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		throw new UnsupportedOperationException("putAll");
	}

	@Override
	public void clear() {

	}

	public Collection<Entry<K,V>> search(K lowerLimit, K upperLimit){
		return _nodeContainer.getNode().search(lowerLimit, upperLimit);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Set<K> keySet() {
		TreeSet<K> keys = new TreeSet<>();
		for(Entry<K,V> thisKey : this){
			keys.add(thisKey.getKey());
		}

		return keys;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Collection<V> values() {
		ArrayList<V> values = new ArrayList<>();
		for(Entry<K,V> thisValue : this){
			values.add(thisValue.getValue());
		}

		return values;
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		TreeSet<Entry<K,V>> entries = new TreeSet<>();
		for(Entry<K,V> thisEntry : this){
			entries.add(thisEntry);
		}

		return entries;
	}

	@Override
	public Iterator<Entry<K, V>> iterator() {
		return _nodeContainer.getNode().iterator();
	}
}
