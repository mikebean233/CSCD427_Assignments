import java.util.*;

public class SortMergeJoin implements Join{
	private int _blockSize; // in records
	private int _memorySize; // in blocks
	private int _partitionSize ;
	private Comparator<Record> _recordComparator;
	private String _sortKey;

	public SortMergeJoin(int blockSize, int memorySize){
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

		// Sort the partitions
		List<Record[]> leftPartitions  = sort(leftScheme.getFilename(), leftScheme.getName(), leftScheme);
		List<Record[]> rightPartitions = sort(rightScheme.getFilename(), rightScheme.getName(), rightScheme);

		// Merge the partitions
		Record[] leftRelation  = merge(leftPartitions);
		Record[] rightRelation = merge(rightPartitions);

		// Finally, do the actual join
		join(leftRelation, rightRelation);
	}

	private void join(Record[] leftRel, Record[] rightRel){
		assert leftRel != null && rightRel != null;
		assert leftRel.length > 0 && rightRel.length > 0;

		Scheme outputScheme = Scheme.buildJoinScheme(leftRel[0].getScheme(),rightRel[0].getScheme());
		List<Record> result = new ArrayList<>();

		int leftIndex = 0, rightIndex = 0;
		int leftLength = leftRel.length, rightLength = rightRel.length;

		while(leftIndex < leftLength && rightIndex < rightLength){
			Record leftRecord = leftRel[leftIndex];
			for(; rightIndex < rightLength && _recordComparator.compare(rightRel[rightIndex], leftRecord) <= 0; ++rightIndex){
				if(_recordComparator.compare(leftRecord, rightRel[rightIndex]) == 0)
					result.add(Record.joinRecords(leftRecord, rightRel[rightIndex], outputScheme));
			}

			for(; leftIndex < leftLength && rightIndex < rightLength && _recordComparator.compare(leftRel[leftIndex], rightRel[rightIndex]) < 0; ++leftIndex)
			{}
		}
		Utils.writeDBFile("smj.txt", result);
	}

	private  Record[] merge(List<Record[]> partitions){
		assert partitions != null && partitions.size() > 0;

		if(partitions.size() == 1)
			return partitions.get(0);

		int leftIndex = 0, rightIndex= 0;
		List<Record[]> temp;
		Record[] result;

		int i = 0;
		while(partitions.size() > 1) {
			int endIndex = (partitions.size() % 2 == 0) ? partitions.size() - 1: partitions.size() - 2;
			temp = new ArrayList<>();

			// Merge the partitions together one pair at a time
			for (i = 1; i <= endIndex; i += 2){
				temp.add(mergeTwo(partitions.get(i - 1), partitions.get(i)));
			}
			// If we ended up with an odd sized list, merge the last two elements
			if(partitions.size() % 2 != 0){
				temp.set(temp.size() - 1, mergeTwo(temp.get(temp.size() - 1), partitions.get(partitions.size() - 1)));
			}
			partitions = temp;
		}

		result = partitions.get(0);
		temp = null; partitions = null;

		Utils.writeDBFile("sorted_" + result[0].getName() + ".txt", result);
		return result;
	}

	private Record[] mergeTwo(Record[] leftPartition, Record[] rightPartition){
		assert leftPartition != null && rightPartition != null;

		int leftLength = leftPartition.length, rightLength = rightPartition.length;
		int leftIndex = 0, rightIndex = 0;
		int resultIndex = 0;
		Record[] result = new Record[leftLength + rightLength];

		while(leftIndex < leftLength || rightIndex < rightLength){
			for(; leftIndex < leftLength && (rightIndex >= rightLength || _recordComparator.compare(leftPartition[leftIndex], rightPartition[rightIndex]) <= 0); ++leftIndex)
				result[resultIndex++] = leftPartition[leftIndex];

			for(; rightIndex < rightLength && (leftIndex >= leftLength || _recordComparator.compare(rightPartition[rightIndex], leftPartition[leftIndex]) <= 0); ++rightIndex)
				result[resultIndex++] = rightPartition[rightIndex];
		}
		return result;
	}

	private List<Record[]> sort (String filename, String relationName, Scheme scheme){
		assert filename != null && scheme != null;
		assert scheme.containsAttribute(_sortKey);

		List<Record[]> partitions = Utils.parseDbFile(filename, scheme, _partitionSize);

		// Sort the partitions
		partitions.forEach((thisPartition) -> Arrays.sort(thisPartition, _recordComparator));

		// Write the partitions to disk
		for(int partitionIndex = 0; partitionIndex < partitions.size(); ++partitionIndex)
			Utils.writeDBFile("smj_" + relationName + "_" + partitionIndex + ".txt", partitions.get(partitionIndex));

		return partitions;
	}
}
