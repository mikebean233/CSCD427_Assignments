import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class BufMgr {
	private BufHashTbl _pageTable;
	private Frame[] _bufferTable;
	private int _frameCount = 0;
	private int _usedFrameCount = 0;
	private PageReplacementPolicy _policy = PageReplacementPolicy.Clock;
	private Stack<Integer> _emptyFrames = new Stack<>();


	private static ArrayList<PageReplacementPolicy> supportedPolicies = new ArrayList<>();

	public static enum PageReplacementPolicy {
		LRU,
		MRU,
		Clock
	}

	public static BufMgr buildBufMgr(int bufferTableSize, PageReplacementPolicy policy){
		if(supportedPolicies.isEmpty()){
			supportedPolicies.add(PageReplacementPolicy.LRU);
		}

		if(!supportedPolicies.contains(policy)){
			throw new UnsupportedOperationException(policy + " replacement policy not supported");
		}

		return new BufMgr(bufferTableSize, policy);
	}

	private BufMgr(int frameCount, PageReplacementPolicy policy){
		_policy = policy;
		_pageTable = new BufHashTbl(frameCount);
		_bufferTable = new Frame[frameCount];

		for(int i = 0; i < frameCount; ++i) {
			_bufferTable[i] = Frame.buildEmptyFrame();
			_emptyFrames.push(i);
		}
	}

	public boolean pin(int pageNo){
		int frameNo = _pageTable.lookup(pageNo);

		// If this frame is in the buffer table already, just increase the pin count
		if(frameNo >= 0){
			_bufferTable[frameNo].incPin();
			return true;
		}

		// If this frame isn't in the buffer table and our table is full, defer to the page replacement policy
		if(_usedFrameCount == _frameCount){
			switch(_policy){
				case LRU:


					break;
				case MRU:  return false; // Not supported
				case Clock:return false; // Not supported
			}
		}
		// If this frame isn't in the buffer table and our table is not full, use an empty slot
		else {
			int thisFrameNo = _emptyFrames.pop();
			Frame thisFrame = new Frame(readPage(pageNo));

			_pageTable.insert(thisFrameNo, pageNo);
			_bufferTable[thisFrameNo] = thisFrame;

		}

		return true;
	}

	public void unpin(int pageNo){

	}

	// Technically should be the responsibility of the disk manager
	public void createPage(){
		int pageNo = ++_frameCount;
		writePage(pageNo, "This is page " + pageNo);
	}

	public void writePage(int pageNo){
		//
		String bufferedData = "test Data";

		writePage(pageNo, bufferedData);
	}

	// Calls frames display method
	public void displayPage(){}

	// Calls frames update method
	public void updatePage(int pageId, String newContent){

	}

	private File getPageFile(int pageNo){
		return new File(pageNo + ".txt");
	}

	public String readPage(int pageNo){
		File file = getPageFile(pageNo);

		StringBuilder contents = new StringBuilder();

		try {
			if (!file.exists())
				file.createNewFile();

			Scanner scanner = new Scanner(file);

			while(scanner.hasNextLine())
				contents.append(scanner.nextLine());

			if(scanner.hasNext())
				contents.append(scanner.next());

			scanner.close();
		}
		catch(Exception ex){
			ex.printStackTrace(System.err);
		}
		return contents.toString();
	}

	private void writePage(int pageNo, String data){
		File file = getPageFile(pageNo);

		try {
			if (!file.exists())
				file.createNewFile();

			FileWriter writer = new FileWriter(file);
			writer.write(data);
			writer.close();
		}
		catch(Exception ex){
			ex.printStackTrace(System.err);
		}
	}

}
