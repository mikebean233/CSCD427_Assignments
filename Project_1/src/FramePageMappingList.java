import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class FramePageMappingList implements Collection <FramePageMapping> {
	private int _size = 0;
	private Node _head = null;
	private Node _tail = null;
	private int _modCount = 0;


	private class Node {
		FramePageMapping _data;
		Node _next;
		Node _prev;

		public Node(){}

		public Node(FramePageMapping data){
			if(data == null)
				throw new NullPointerException();
			_data = data;
		}

		public Node(FramePageMapping data, Node prev, Node next){
			if(data == null || prev == null || next == null)
				throw new NullPointerException();

			_data = data;
			_next = next;
			_prev = prev;
		}

		@Override
		public String toString(){
			return _data.toString();
		}
	}

	private class FramePageMappingIterator implements Iterator<FramePageMapping>{
		private int _startModCount;
		private FramePageMappingList _list;
		Node _thisNode;


		public FramePageMappingIterator(FramePageMappingList list ){
			_list = list;
			_startModCount = _list._modCount;
			_thisNode = list._head._next;
		}

		@Override
		public boolean hasNext() {
			return _thisNode != _list._tail;
		}

		@Override
		public FramePageMapping next() throws ConcurrentModificationException{
			if(_list._modCount != _startModCount)
				throw new ConcurrentModificationException();

			if(!hasNext())
				throw new NoSuchElementException("next");

			FramePageMapping thisData = _thisNode._data;
			_thisNode = _thisNode._next;
			return thisData;
		}
	}

	public FramePageMappingList(){
		_head = new Node();
		_tail = new Node();

		_head._next = _tail;
		_tail._prev = _head;
	}




	@Override
	public int size() {
		return _size;
	}

	@Override
	public boolean isEmpty() {
		return _head == _tail;
	}

	@Override
	public boolean contains(Object o) {
		if(o == null || !(o instanceof FramePageMapping) || _head == _tail)
			return false;

		Node highNode = _tail._prev;
		Node lowNode  = _head._next;
		for(; lowNode != highNode && lowNode._next != highNode; lowNode = lowNode._next, highNode = highNode._prev) {
			if(lowNode._data.equals(o) || highNode._data.equals(o))
				return true;
		}
		return lowNode._data.equals(o) || highNode._data.equals(o);
	}

	@Override
	public Iterator<FramePageMapping> iterator() {
		return new FramePageMappingIterator(this);
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
	public boolean add(FramePageMapping frame) {
		Node newNode = new Node(frame, _tail._prev, _tail);
		_tail._prev._next = newNode;
		_tail._prev = newNode;

		++ _size;
		++ _modCount;
		return true;
	}

	@Override
	public boolean remove(Object o) {
		if(_head == _tail || o == null || !(o instanceof FramePageMapping))
			return false;

		Node highNode = _tail._prev;
		Node lowNode  = _head._next;
		for(; lowNode != highNode && lowNode._next != highNode; lowNode = lowNode._next, highNode = highNode._prev) {
			if(lowNode._data.equals(o))
				return removeNode(lowNode);
			if(highNode._data.equals(o))
				return removeNode(highNode);
		}

		if (lowNode._data.equals(o))
			return removeNode(lowNode);
		else
			return highNode._data.equals(o) && removeNode(highNode);
	}

	private boolean removeNode(Node target){
		if(target == null  || target == _head || target == _tail || (target._prev == null && target._next == null))
			return false;

		if(target._prev != null) target._prev._next = target._next;
		if(target._next != null) target._next._prev = target._prev;

		target._prev = null;
		target._next  = null;

		++ _modCount;
		-- _size;
		return true;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		if(c == null)
			throw new NullPointerException();

		for(Object thisFramePageMapping : c){
			if(!contains(thisFramePageMapping))
				return false;
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends FramePageMapping> c) {
		 if(c == null)
		 	throw new NullPointerException();

		boolean didChange = false;

		for(FramePageMapping thisFramePageMapping : c)
			didChange = didChange || add(thisFramePageMapping);

		return didChange;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException("removeAll");
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("retainAll");
	}

	@Override
	public void clear() {
		_head._next = _tail;
		_tail._prev = _head;
		_size = 0;
	}
}
