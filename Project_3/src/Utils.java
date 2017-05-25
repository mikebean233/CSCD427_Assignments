import java.io.File;
import java.io.PrintWriter;
import java.util.*;

public final class Utils {
	private final String NEWLINE = System.lineSeparator();

	public static void writeDBFile(String filename, Collection<Record> partition){
		if(filename == null || partition == null)
			throw new IllegalArgumentException("writeDBFile");

		writeDBFile(filename, partition.toArray(new Record[]{}));
	}

	public static void writeDBFile(String filename, Record[] partition){
		if(filename == null || partition == null)
			throw new IllegalArgumentException("writeDBFile");

		String NEWLINE = System.lineSeparator();

		try {
			File outFile = new File(filename);
			PrintWriter outFileWriter = new PrintWriter(outFile);

			Arrays.stream(partition)
					.forEach(record -> outFileWriter.append(record.getTabSeperatedOutput()).append(NEWLINE));

			outFileWriter.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}

	}

	public static List<Record[]> parseDbFile(String filename, Scheme scheme, int partitionSize){
		ArrayList<Record[]> partitions = new ArrayList<>();
		try {
			File thisFile = new File(filename);
			Scanner fileScanner = new Scanner(thisFile);
			fileScanner.reset();

			boolean done = false;
			while (!done) {
				Record[] thisPartition = new Record[partitionSize];
				int recordIndex;
				for (recordIndex = 0; recordIndex < partitionSize && !done; ++recordIndex) {
					if (fileScanner.hasNext()) {
						String[] data = fileScanner.nextLine().split("\t");
						thisPartition[recordIndex] = new Record(scheme, data);
					} else {
						done = true;
					}
				}
				if (recordIndex < partitionSize)
					thisPartition = Arrays.copyOf(thisPartition, recordIndex - 1);

				partitions.add(thisPartition);
			}
			fileScanner.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
		return partitions;
	}

	public static boolean isInteger(String input){
		try {
			int result = Integer.parseInt(input);
		}catch(NumberFormatException ex){
			return false;
		}
		return true;
	}

	public static int intStringCompare(String a, String b){
		return (isInteger(a) && isInteger(b)) ? Integer.parseInt(a) - Integer.parseInt(b) : a.compareTo(b);
	}

}
