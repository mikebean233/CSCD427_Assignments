import com.sun.tools.javac.util.RichDiagnosticFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;

public class HashJoin implements Join {
	private int _blockSize;
	private int _memorySize;
	private int _partitionSize;
	private String _sortKey;
	private RecordComparator _recordComparator;

	public HashJoin(int blockSize, int memorySize){
		_blockSize = blockSize;
		_memorySize = memorySize;
		_partitionSize = _memorySize / 2;
	}

	@Override
	public void join(Scheme leftScheme, Scheme rightScheme) {
		if(leftScheme == null || rightScheme == null)
			throw new IllegalArgumentException("join");

		_sortKey = Scheme.getCommonAttributes(leftScheme, rightScheme).get(0);
		_recordComparator = new RecordComparator(_sortKey);

		// Hash the partitions
		Hashtable<String, List<Record>> leftBuckets  = hash( leftScheme.getFilename(),  leftScheme.getName(), leftScheme);
		Hashtable<String, List<Record>> rightBuckets = hash(rightScheme.getFilename(), rightScheme.getName(), rightScheme);

		// Join the buckets
		List<Record> result = new ArrayList<>();
		leftBuckets.keySet().stream()
			.filter(rightBuckets::containsKey)
			.sorted(Utils::intStringCompare)
			.forEach(l -> result.addAll(join(leftBuckets.get(l), rightBuckets.get(l))));

		Utils.writeDBFile("hj.txt", result);
	}

	private List<Record> join(List<Record> leftBucket, List<Record> rightBucket){
		assert leftBucket != null && rightBucket != null;

		List<Record> result = new ArrayList<>();
		Scheme joinScheme = Scheme.buildJoinScheme(leftBucket.get(0).getScheme(), rightBucket.get(0).getScheme());

		for( Record leftRecord : leftBucket){
			result.addAll(rightBucket.stream()
				.sorted(_recordComparator)
				.map(rightRecord -> Record.joinRecords(leftRecord, rightRecord, joinScheme))
				.collect(Collectors.toList())
			);
		}

		return result;
	}


	private Hashtable<String, List<Record>> hash(String filename, String relationName, Scheme scheme){
		List<Record[]> partitions = Utils.parseDbFile(filename, scheme, _partitionSize);

		Hashtable<String, List<Record>> table = new Hashtable<>();

		for(Record[] thisPartition: partitions) {
			for (Record thisRecord : thisPartition) {
				String key = thisRecord.getAttribute(_sortKey);
				if (!table.containsKey(key))
					table.put(key, new ArrayList<>());

				table.get(key).add(thisRecord);
			}
		}

		List<List<Record>> buckets = table.keySet().stream().map(table::get).collect(Collectors.toList());

		int bucketIndex;
		for(bucketIndex = 0; bucketIndex < buckets.size(); ++bucketIndex){
			Utils.writeDBFile("hj_" + scheme.getName() + "_" + bucketIndex + ".txt", buckets.get(bucketIndex));
		}
		return table;
	}
}
