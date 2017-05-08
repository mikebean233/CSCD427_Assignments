public class BTNodeContainer<K extends Comparable<K>,V> {
	private BTNode<K,V> _node;
	private int _size;
	public BTNodeContainer(){}

	public void setNode(BTNode<K,V> node) {
		_node = node;
	}
	public int getSize(){return _size;}
	public int incSize(){return ++_size;}
	public int decSize(){return --_size;}
	public BTNode<K,V> getNode(){
		return _node;
	}
}
