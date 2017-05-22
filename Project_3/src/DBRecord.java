import java.util.*;

public class DBRecord {
	private Hashtable<String, String> _data;
	private LinkedHashSet<String> _scheme;

	@SuppressWarnings("unchecked")
	public DBRecord(LinkedHashSet<String> scheme, String[] data){
		if(scheme == null || data == null || scheme.size() != data.length)
			throw new IllegalArgumentException("DBRecord");

		_scheme = (LinkedHashSet<String>)scheme.clone();

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
		_scheme.forEach(result::add);
		return result;
	}

	public boolean containsAttribute(String attribute){
		return _scheme.contains(attribute);
	}

	@Override
	public String toString(){
		return _data.toString();
	}

}
