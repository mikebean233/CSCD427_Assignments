import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class BufMgr {
	private BufHashTbl _pageTable;
	private Frame[] _bufferTable;
	private PageReplacementPolicy _policy = PageReplacementPolicy.Clock;
	private Stack<Integer> _emptyFrames = new Stack<>();
	private LinkedList<Integer> _pageQueue = new LinkedList<>();

	private static ArrayList<PageReplacementPolicy> supportedPolicies = new ArrayList<>();

	public enum PageReplacementPolicy {
		LRU,
		MRU,
		Clock
	}

	public static BufMgr buildBufMgr(int bufferTableSize, PageReplacementPolicy policy){
		if(supportedPolicies.isEmpty()){
			supportedPolicies.add(PageReplacementPolicy.LRU);
			supportedPolicies.add(PageReplacementPolicy.MRU);
		}

		if(!supportedPolicies.contains(policy)){
			throw new UnsupportedOperationException("the " + policy + " replacement policy not supported");
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

	public Frame pin(Integer pageNo){
		int frameNo = _pageTable.lookup(pageNo);

		// If this frame is not in our page table, add it using the page file
		if(frameNo == -1)
			readPage(pageNo);

		Frame thisFrame = getFrame(true, pageNo);


		// Since this page will no longer have a pin count of zero, it doesn't need to be in our queue anymore
		if(_pageQueue.contains(pageNo))
			_pageQueue.remove(pageNo);

		return thisFrame.incPin();
	}

	private Frame getFrame(boolean createIfNotExist, int pageNo){
		int frameNo = _pageTable.lookup(pageNo);

		if(frameNo >= 0)
			return _bufferTable[frameNo];
		else if(!createIfNotExist)
			return Frame.buildEmptyFrame();

		if(_emptyFrames.empty() ){
			return executeReplacementPolicy(pageNo);
		}
		else {
			int thisFrameNo = _emptyFrames.pop();
			Frame thisFrame = new Frame();//readPage(pageNo));

			_pageTable.insert(thisFrameNo, pageNo);
			_bufferTable[thisFrameNo] = thisFrame;
			return thisFrame;
		}
	}

	private Frame executeReplacementPolicy(int pageNo){
		// If there are no pages in our page queue, then we don't have any with a pin of zero and can't evict anything
		if(_pageQueue.isEmpty()){
			System.err.println("failed to evict frame, there are no frames without active users");
			return Frame.buildEmptyFrame();
		}

		int evictedPage = -1;
		switch(_policy){
			case LRU: evictedPage = _pageQueue.removeFirst(); break;
			case MRU: evictedPage = _pageQueue.removeLast();  break;
			default:
				// This should never happen given that our builder checks for this already...
				throw new UnsupportedOperationException("the " + _policy + " replacement policy is not supported");
		}

		return replacePage(evictedPage, pageNo);
	}

	private Frame replacePage(int oldPage, int newPage){
		Frame oldFrame = getFrame(false, oldPage);

		if(oldFrame.isEmptyFrame())
			throw new IllegalStateException("replacePage");

		int frameNumber = _pageTable.lookup(oldPage);

		// Write our evicted frame to the file system then remove it from our page table
		writePage(oldPage);
		_pageTable.remove(oldPage);

		// Create our new frame from disk contents, then add it to our buffer table


		_pageTable.insert(frameNumber, newPage);
		_bufferTable[frameNumber] = Frame.buildEmptyFrame();
		_pageQueue.add(newPage);

		System.out.println("Evicting page " + oldPage + " and replacing it with page " + newPage);
		return readPage(newPage);
	}

	public Frame unpin(int pageNo){
		Frame thisFrame = getFrame(false, pageNo);
		thisFrame.decPin();

		if(!thisFrame.isEmptyFrame() && thisFrame.getPin() == 0)
			_pageQueue.add(pageNo);

		return getFrame(false, pageNo);
	}

	// Technically should be the responsibility of the disk manager
	public void createPage(int pageNo){
		writeFile(getPageFile(pageNo), "This is page " + pageNo);
	}

	public String displayPage(int pageNo){
		return getFrame(false, pageNo).displayPage();
	}

	public boolean updatePage(int pageNo, String newContent){
		return !getFrame(false, pageNo).updatePage(newContent).isEmptyFrame();
	}

	private File getPageFile(int pageNo){
		return new File(pageNo + ".txt");
	}

	private boolean writeFile(File file, String contents){
		try {
			if (!file.exists())
				file.createNewFile();

			FileWriter writer = new FileWriter(file);
			writer.write(contents);
			writer.close();
			return true;
		}
		catch(Exception ex){
			ex.printStackTrace(System.err);
		}
		return false;
	}

	public Frame readPage(int pageNo){
		File file = getPageFile(pageNo);

		StringBuilder contents = new StringBuilder();

		try {
			if (!file.exists())
				writeFile(file, "This is page " + pageNo);

			Scanner scanner = new Scanner(file);

			while(scanner.hasNextLine())
				contents.append(scanner.nextLine());

			if(scanner.hasNext())
				contents.append(scanner.next());

			scanner.close();

			 return getFrame(true, pageNo).setContents(contents.toString());
		}
		catch(Exception ex){
			ex.printStackTrace(System.err);
		}
		return Frame.buildEmptyFrame();
	}

	private void writePage(int pageNo){
		Frame thisFrame = getFrame(false, pageNo);

		if(!thisFrame.isEmptyFrame() && thisFrame.isDirty())
			writeFile(getPageFile(pageNo), thisFrame.displayPage());

		thisFrame.setDirty(false);
	}
}
