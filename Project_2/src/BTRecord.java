/**
 * Created by michael on 4/24/17.
 */
public class BTRecord {
	private BTRecord _next;
	private int _value = 0;

	public BTRecord(){}

	public BTRecord getNext(){return _next;}
	public boolean hasNext(){return _next == null;}
	public void setNext(BTRecord next){_next = next;}

	public int getValue(){return _value;}
	public void setValue(int value){_value = value;}
	public int incValue(){return ++_value;}
	public int decValue(){return --_value;}
}
