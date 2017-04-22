import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

public class WordFrequency implements Runnable{
	HashSet<String> _ignoreList;
	String _inputFileName;

	TreeSet<String> tree;


	public static void main(String[] args){
		String[] testArgs = new String[]{"test.html", "one", "three", "five", "seven", "nine"};

		(new WordFrequency(testArgs)).run();
	}

	private WordFrequency(String[] args) {
		_ignoreList = new HashSet<>();
		parseArgs(args);
	}

	@Override
	public void run() {
		// Test HTMLParser
		ArrayList<String> result = new ArrayList<>();

		(new HTMLParser(_inputFileName, result, _ignoreList)).run();

		System.out.println(result);
	}

	private void parseArgs(String[] args){
		if(args.length < 1)
			usage();

		_inputFileName = args[0];

		for(int i = 1; i < args.length; ++i){
			_ignoreList.add(args[i]);
		}
	}

	private void usage(){
		System.err.println("java inputFile [ignore list]");
		System.exit(1);
	}
}
