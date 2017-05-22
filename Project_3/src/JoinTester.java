import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class JoinTester implements Runnable {
	private int _memorySize;
	private int _blockSize;

	public static void main(String[] args){
		args = new String[]{"10", "100"};

		(new JoinTester(args)).run();
	}

	private JoinTester(String[] args) {
		parseArgs(args);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void run(){
		SortMergeJoin smj = new SortMergeJoin(_blockSize, _memorySize);


		LinkedHashSet<String> studentsScheme = new LinkedHashSet<>();
		Arrays.asList(new String[]{"ID", "name", "dept_name", "credits"})
				.forEach(studentsScheme::add);

		smj.sort("student.txt", "student", studentsScheme, "ID");

	}

	private void parseArgs(String[] args){
		if(args.length != 2)
			usage();

		try{
			_blockSize = Integer.parseInt(args[0]);
			_memorySize = Integer.parseInt(args[1]);
		}
		catch(Exception ex){
			usage();
		}
	}

	private void usage(){
		System.err.println("usage: java JointTester block_size memory_size");
		System.exit(1);
	}
}
