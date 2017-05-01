/**
 * Created by michael on 4/24/17.
 */
public class BTRecord {
	private int _value = 0;
	private int _instanceId;
	private static int instanceCount = 0;


	public static BTRecord build(int value){
		return new BTRecord(value);
	}

	public static BTRecord build(){
		return new BTRecord();
	}

	public BTRecord(){
		_instanceId = instanceCount++;
	}

	public BTRecord(int value){
		this();
		_value = value;
	}

	public int getValue(){return _value;}
	public void setValue(int value){_value = value;}
	public int incValue(){return ++_value;}
	public int decValue(){return --_value;}


	@Override
	public boolean equals(Object thatObject){
		if(thatObject == null || !(thatObject instanceof BTRecord))
			return false;

		BTRecord thatRecord = (BTRecord) thatObject;
		return thatRecord._value == _value;
	}

	@Override
	public String toString(){
		return "{ Class: \"BTRecord\",  InstanceId: " + _instanceId + ", value: " + _value + " }";
	}
}
