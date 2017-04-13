import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class BufMgrTester implements Runnable{
	private int _bufferPoolSize;
	private HashMap<Integer, String> _menuMap;
	private Scanner _keyboard;
	private final Set<Integer> _menuOptions;

	private BufMgr _bufferManager;

	public BufMgrTester(int bufferPoolSize){
		_bufferPoolSize = bufferPoolSize;
		_bufferManager  = BufMgr.buildBufMgr(_bufferPoolSize, BufMgr.PageReplacementPolicy.LRU);
		_keyboard       = new Scanner(System.in);

		// Menu
		_menuMap        = new HashMap<>();
		_menuMap.put( 1, "Create pages");
		_menuMap.put( 2, "Request a Page");
		_menuMap.put( 3, "Update a Page");
		_menuMap.put( 4, "Relinquish a Page");
		_menuMap.put(-1, "quit");
		_menuOptions = _menuMap.keySet();
	}

	@Override
	public void run() {
		// Note: for update (menu option 3) assume it is the same user (don't change the pin)
		while(getMenuSelection()){}
	}

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

		// Now that we have scrubbed the command line argument, lets perform the task at hand
		(new BufMgrTester(bufferPoolSize)).run();
	}

	private boolean getMenuSelection(){
		int menuSelection = 0;
		boolean haveValidMenuItem = false;

		while(!haveValidMenuItem) {
			printMenu();
			menuSelection = getValidInt();

			if(!_menuOptions.contains(menuSelection)){
				System.err.println("The menu option you selected is not valid, try again");
			}
			else
				haveValidMenuItem = true;
		}

		switch(menuSelection){
			case  1: createPages();    break;
			case  2: requestPage();    break;
			case  3: updatePage();     break;
			case  4: relinquishPage(); break;
			case -1: quit();           break;
			default: System.err.println("Invalid menu option, how did that happen?");
		}
		return menuSelection != -1;
	}

	// Menu Option -1
	private void quit(){
		System.out.println("Goodbye!");
	}

	// Menu Option  1
	private void createPages(){
		int newPageCount = getValidInt("Enter the number of pages you would like to create");
		for(int i = 0; i < newPageCount; ++i)
			_bufferManager.createPage(i);
	}

	// Menu Option  2
	private void requestPage(){
		int pageNo = getValidInt("Enter the number of the page you would like displayed");
		_bufferManager.pin(pageNo);
		System.out.println("------ Page " + pageNo + " contents in buffer --------");
		System.out.println(_bufferManager.displayPage(pageNo));
	}

	// Menu Option  3
	private void updatePage(){
		int pageNo = getValidInt("Enter the number of the page you would like to update");
		String newContents = getLine("Enter the new contents you would like to add to page " + pageNo);
		_bufferManager.updatePage(pageNo, newContents);
		System.out.println("------ Page " + pageNo + " contents in buffer --------");
		System.out.println(_bufferManager.displayPage(pageNo));
	}

	// Menu Option  4
	private void relinquishPage(){
		int pageNo = getValidInt("Enter the number of the page you would like to relinquish");
		_bufferManager.unpin(pageNo);
	}

	private int getValidInt(){
		int intValue = 0;
		boolean haveValidInt = false;
		while(!haveValidInt){
			try{
				intValue = Integer.parseInt(_keyboard.nextLine());
				haveValidInt = true;
			}
			catch(NumberFormatException ex){
				System.err.print("The value you entered is not a valid integer, try again: ");
			}
		}
		return intValue;
	}

	private int getValidInt(String prompt){
		System.out.print(prompt + ": ");
		return getValidInt();
	}

	private String getLine(String prompt){
		System.out.print(prompt + ": ");
		return _keyboard.nextLine();
	}

	private void printMenu(){
		for(Integer thisKey : _menuOptions)
			System.out.println(thisKey + ") " + _menuMap.get(thisKey));

		System.out.print("> ");
	}

	private static void usage(){
		System.err.println("usage: java BufMgrTester a_number");
		System.err.println("    a_number: The size of the buffer pool (must be greater than zero)");
	}
}
