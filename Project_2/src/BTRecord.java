/**
 * Created by michael on 4/24/17.
 */
public class BTRecord {
	private int _frequency = 0;
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

	public BTRecord(int frequency){
		this();
		_frequency = frequency;
	}

	public int getFrequency(){return _frequency;}
	public void setFrequency(int value){_frequency = value;}
	public int incFrequency(){return ++_frequency;}
	public int decFrequency(){return --_frequency;}

	@Override
	public boolean equals(Object thatObject){
		if(thatObject == null || !(thatObject instanceof BTRecord))
			return false;

		BTRecord thatRecord = (BTRecord) thatObject;
		return thatRecord._frequency == _frequency;
	}

	@Override
	public String toString(){
		return "{ Class: \"BTRecord\",  InstanceId: " + _instanceId + ", frequency: " + _frequency + " }";
	}
}
