import java.util.*;
import java.util.stream.Collectors;

public class Record {
	private Hashtable<String, String> _data;
	private Scheme _scheme;

	@SuppressWarnings("unchecked")
	public Record(Scheme scheme, List<String> data){
		this(scheme, data.toArray(new String[]{}));
	}

	@SuppressWarnings("unchecked")
	public Record(Scheme scheme, String[] data){
		if(scheme == null || data == null || scheme.size() != data.length)
			throw new IllegalArgumentException("Record");

		_scheme = scheme;
		_data = new Hashtable<>();
		int dataIndex = 0;

		for(String thisAttribute: scheme){
			_data.put(thisAttribute, data[dataIndex++]);
		}
	}

	public Hashtable<String,String> getData(){
		return _data;
	}

	public String getAttribute(String attribute){
		return _data.get(attribute);
	}

	public Collection<String> getOrderedData(){
		Collection<String> result = new ArrayList<>();
		_scheme.forEach(thisKey -> result.add(_data.get(thisKey)));
		return result;
	}

	public String getTabSeperatedOutput(){
		return getOrderedData().stream().collect( Collectors.joining("\t"));
	}

	public boolean containsAttribute(String attribute){return _scheme.containsAttribute(attribute);}

	@Override
	public String toString(){
		return _data.toString();
	}

	public String getName(){return _scheme.getName();}

	public Scheme getScheme(){return _scheme;}

	public static Record joinRecords(Record leftRecord, Record rightRecord, Scheme scheme){
		assert leftRecord != null && rightRecord != null && scheme != null;

		List<String> data = new ArrayList<>();

		for (String attr : scheme.getScheme()) {
			String leftValue = leftRecord.getAttribute(attr);
			String rightValue = rightRecord.getAttribute(attr);
			if (leftValue != null && rightValue != null && !leftValue.equals(rightValue))
				throw new IllegalArgumentException("Record.joinRecords: Incompatible records, " + leftRecord.getName() + "." + attr + "->" + leftValue + "   " + rightRecord.getName() + "." + attr + "->" + rightValue);

			data.add((leftValue == null) ? rightValue : leftValue);
		}

		return new Record(scheme, data);
	}
}
