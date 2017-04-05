public class BufMgrTester {
	public static void main(String args[]){
		if(args.length != 1){
			usage();
			System.exit(1);
		}

		int bufferPoolSize = -1;

		try{
			bufferPoolSize = Integer.parseInt(args[0]);
		}
		catch(NumberFormatException ex){
			usage();
			System.exit(2);
		}

		if(bufferPoolSize <= 0){
			usage();
			System.exit(3);
		}

		// Now that we have scrubbed out command line argument, lets perform the task at hand

	}

	private static void usage(){
		System.err.println("usage: BufMgrTester a_number");
		System.err.println("    a_number: The size of the buffer pool (must be greater than zero)");
	}
}
