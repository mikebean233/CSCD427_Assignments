public class JoinTester implements Runnable {
	private int _memorySize;
	private int _blockSize;

	public static void main(String[] args){
		//args = new String[]{"10", "100"};
		(new JoinTester(args)).run();
	}

	private JoinTester(String[] args) {
		parseArgs(args);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void run(){
		Scheme students = new Scheme(new String[]{"ID", "name", "dept_name", "credits"}, "student");
		Scheme takes    = new Scheme(new String[]{"ID", "course_id", "sec_id", "semester", "year", "grade"}, "takes");

		Join smj = new SortMergeJoin(_blockSize, _memorySize);
		Join hj  = new HashJoin(_blockSize, _memorySize);

		smj.join(students, takes);
		hj.join(students, takes);
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
