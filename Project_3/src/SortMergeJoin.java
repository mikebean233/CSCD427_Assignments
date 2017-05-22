import java.io.File;
import java.util.*;

public class SortMergeJoin {
	private int _blockSize; // in records
	private int _memorySize; // in blocks
	private int _partitionSize;

	public SortMergeJoin(int blockSize, int memorySize){
		_blockSize = blockSize;
		_memorySize = memorySize;
		_partitionSize = memorySize / blockSize;
	}

	public void sort (String filename, String relationName, LinkedHashSet<String> scheme, final String sortKey){
		if(filename == null || scheme == null || sortKey == null)
			throw new IllegalArgumentException("sort: Null parameters");

		if(!scheme.contains(sortKey))
			throw new IllegalArgumentException("sort: sort key is does not exist in scheme");

		ArrayList<DBRecord[][]> partitions = new ArrayList<>();

		try{
			File thisFile = new File(filename);
			Scanner fileScanner = new Scanner(thisFile);
			fileScanner.reset();



			// Store the original partitions/blocks/records into our list
			boolean done = false;
			while(!done) {
				DBRecord[][] thisPartition = new DBRecord[_partitionSize][];
				int blockIndex;
				for (blockIndex = 0; blockIndex < _partitionSize && !done; ++blockIndex) {
					thisPartition[blockIndex] = new DBRecord[_blockSize];
					int recordIndex;
					for (recordIndex = 0; recordIndex < _blockSize && !done; ++recordIndex) {
						if (fileScanner.hasNext()) {
							String[] data = fileScanner.nextLine().split("\t");
							thisPartition[blockIndex][recordIndex] = new DBRecord(scheme, data);
						} else {
							done = true;
						}
					}
					if(recordIndex < _blockSize)
						thisPartition[blockIndex] = Arrays.copyOf(thisPartition[blockIndex], recordIndex - 1);
				}
				if(blockIndex < _partitionSize)
					thisPartition = Arrays.copyOf(thisPartition, blockIndex - 1);

				partitions.add(thisPartition);
			}
			fileScanner.close();

			// Sort the partitions
			partitions.forEach((thisPartition) -> {


				// Sort the records
				Arrays.stream(thisPartition)
					.forEach( thisBlock-> {
						System.out.println("=========== new Block =============");
						System.out.println("===== Before Sort =====");
						Arrays.stream(thisBlock).forEach(System.out::println);

						Arrays.sort(thisBlock, (a,b)-> a.getAttribute(sortKey).compareTo(b.getAttribute(sortKey)));

						System.out.println("===== After Sort =====");
						Arrays.stream(thisBlock).forEach(System.out::println);
						System.out.println("=======================================");

					});

				// Then sort the blocks
				Arrays.sort(thisPartition, (a,b)-> a[0].getAttribute(sortKey).compareTo(b[0].getAttribute(sortKey)));
			});

			// then sort the blocks
			// Write the partitions to their output files
			int partitionIndex= 0;
			for(DBRecord[][] thisPartision : partitions){
				//File outFile = new File("smj_" + relationName + "_" + (partitionIndex++) + ".txt");
				Arrays.stream(thisPartision)
					.filter(thisBlock -> thisBlock != null)
					.forEach(thisBlock -> {
						Arrays.stream(thisBlock)
							.filter(thisRecord -> thisRecord != null)
							.forEach( (thisRecord) -> {
						System.out.println(thisRecord);
						});
					});
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
			System.exit(1);
		}
	}
}
