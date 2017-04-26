import java.util.Collection;
import java.util.Iterator;

/**
 * Created by michael on 4/21/17.
 */
public class BPlusTree implements Collection<String> {
	private int _nodeSize;

	public BPlusTree(int nodeSize){
		if(nodeSize < 2 )
			throw new IllegalArgumentException("BPlusTree");

		_nodeSize = nodeSize;
	}

	public boolean insertWord(String thisWord){
		return add(thisWord);
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean contains(Object o) {
		return false;
	}

	@Override
	public Iterator<String> iterator() {
		return null;
	}

	@Override
	public Object[] toArray() {
		return new Object[0];
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return null;
	}

	@Override
	public boolean add(String s) {
		return false;
	}

	@Override
	public boolean remove(Object o) {
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends String> c) {
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return false;
	}

	@Override
	public void clear() {

	}
}
