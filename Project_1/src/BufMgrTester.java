import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class BufMgrTester implements Runnable{
	private int _bufferPoolSize;
	private HashMap<Integer, String> menuMap;
	private Scanner keyboard;
	private final Set<Integer> menuOptions ;


	public BufMgrTester(int bufferPoolSize){
		_bufferPoolSize = bufferPoolSize;
		FrameList list = new FrameList();
		keyboard = new Scanner(System.in);
		menuMap  = new HashMap<>();
		menuMap.put( 1, "Create pages");
		menuMap.put( 2, "Request a Page");
		menuMap.put( 3, "Update a Page");
		menuMap.put( 4, "Relinquish a Page");
		menuMap.put(-1, "quit");
		menuOptions = menuMap.keySet();
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

			if(!menuOptions.contains(menuSelection)){
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
	}
	// Menu Option  2
	private void requestPage(){
		int pageId = getValidInt("Enter the number of the page you would like displayed");
	}
	// Menu Option  3
	private void updatePage(){
		int pageId = getValidInt("Enter the number of the page you would like to update");
	}
	// Menu Option  4
	private void relinquishPage(){
		int pageId = getValidInt("Enter the number of the page you would like to relinquish");
	}

	private int getValidInt(){
		int intValue = 0;
		boolean haveValidInt = false;
		while(!haveValidInt){
			try{
				intValue = Integer.parseInt(keyboard.nextLine());
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

	private void printMenu(){
		for(Integer thisKey : menuOptions)
			System.out.println(thisKey + ") " + menuMap.get(thisKey));

		System.out.print("> ");
	}


	private static void usage(){
		System.err.println("usage: java BufMgrTester a_number");
		System.err.println("    a_number: The size of the buffer pool (must be greater than zero)");
	}
}
