import java.util.Comparator;

public class RecordComparator implements Comparator<Record> {
	private String _sortKey;

	public RecordComparator(String sortKey){
		_sortKey = sortKey;
	}

	@Override
	public int compare(Record a, Record b){
		String aValue = a.getAttribute(_sortKey);
		String bValue = b.getAttribute(_sortKey);

		return Utils.intStringCompare(aValue, bValue);
	}
}
