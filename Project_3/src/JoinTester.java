public class JoinTester implements Runnable {
	private int _memSize;
	private int _blockSize;

	public static void main(String[] args){
		args = new String[]{"5", "4"};

		(new JoinTester(args)).run();
	}

	private JoinTester(String[] args) {
		parseArgs(args);
	}

	@Override
	public void run(){

	}

	private void parseArgs(String[] args){
		if(args.length != 2)
			usage();

		try{
			_blockSize = Integer.parseInt(args[0]);
			_memSize = Integer.parseInt(args[1]);
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
