import java.util.*;

public class WordFrequency implements Runnable{
	private HashSet<String> _ignoreList;
	private BPlusTree _tree;
	private String _inputFileName;
	private Scanner _kbScanner;
	private Hashtable<String, MenuItem> _menuItems;
	private Collection<String> _menuKeys;


	private interface MenuItem{
		String description();
		boolean action();
	}

	public static void main(String[] args){
		//args = new String[]{"test.html", "one", "three", "five", "seven", "nine"};

		(new WordFrequency(args)).run();
	}

	private WordFrequency(String[] args) {
		_ignoreList = new HashSet<>();
		parseArgs(args);
		_kbScanner = new Scanner(System.in);
		_menuItems = new Hashtable<>();

		/*
		 *   Build our menu items
		 */

		_menuItems.put("1", new MenuItem() {
			@Override
			public String description() {
				return "Print all words in alphabetical order";
			}

			@Override
			public boolean action() {
				System.out.println(Arrays.toString(_tree.getWords().toArray()));
				return true;
			}
		});

		_menuItems.put("2", new MenuItem() {
			@Override
			public String description() {
				return "Display the B+ tree";
			}

			@Override
			public boolean action() {
				System.out.println(_tree);
				return true;
			}
		});

		_menuItems.put("3", new MenuItem() {
			@Override
			public String description() {
				return "Select a node to display";
			}

			@Override
			public boolean action() {
				int nodeId = getInteger("Enter a node id: ");
				BTNode<String, BTRecord> node = _tree.findNodeById(nodeId);
				System.out.println((node != null) ? node : "There is no node with id " + nodeId + " in the tree");
				return true;
			}
		});

		_menuItems.put("4", new MenuItem() {
			@Override
			public String description() {
				return "Insert a word";
			}

			@Override
			public boolean action() {
				_tree.insertWord(getString("New word: "));
				return true;
			}
		});

		_menuItems.put("5", new MenuItem() {
			@Override
			public String description() {
				return "Search for a word";
			}

			@Override
			public boolean action() {
				String thisWord = getString("Enter the word to search for: ");
				int count = _tree.getCount(thisWord);
				System.out.println((count > 0) ? thisWord + " count is " + count : thisWord + " is not in the tree");
				return true;
			}
		});

		_menuItems.put("6", new MenuItem() {
			@Override
			public String description() {
				return "Perform a range search";
			}

			@Override
			public boolean action() {
				String lowerBound = getString("Enter the lower bound for the search: ");
				String upperBound = getString("Enter the upper bound for the search: ");
				while(lowerBound.compareTo(upperBound) >= 0){
					System.err.println("The lower bound must be less than the lower bound!");
					lowerBound = getString("Enter the lower bound for the search: ");
					upperBound = getString("Enter the upper bound for the search: ");
				}

				System.out.println("Matching words: " + Arrays.toString(_tree.rangeMatch(lowerBound, upperBound).toArray()));
				return true;
			}
		});

		_menuItems.put("-1", new MenuItem() {
			@Override
			public String description() {
				return "Quit";
			}

			@Override
			public boolean action() {
				System.out.println("Goodbye");
				return false;
			}
		});

		_menuKeys = _menuItems.keySet();
	}

	@Override
	public void run() {
		// Test HTMLParser
		_tree = new BPlusTree(3);

		(new HTMLParser(_inputFileName, _tree, _ignoreList)).run();

		while(doMenu())
		{}

		_kbScanner.close();
	}

	private boolean doMenu(){
		printMenu();
		String selection;
		while(!_menuKeys.contains(selection = getString("> "))){
			System.err.println(selection + " is not a valid menu option, try again");
			printMenu();
		}
		return _menuItems.get(selection).action();
	}

	private void printMenu(){
		for(String thisKey: _menuKeys){
			System.out.println(thisKey + ") " + _menuItems.get(thisKey).description());
		}
	}


	private String getString(String prompt){
		System.out.print(prompt);
		return _kbScanner.nextLine();
	}

	private int getInteger(String prompt){
		int result = -1;
		boolean success = false;
		while(!success){
			try{
				System.out.print(prompt);
				result = Integer.parseInt(_kbScanner.nextLine());
				success = true;
			} catch(Exception ex){
				System.err.println("that was not a number, try again");
			}
		}

		return result;
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
		System.err.println("usage: java inputFile [ignore list]");
		System.exit(1);
	}
}
