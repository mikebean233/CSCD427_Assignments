public class BufMgr {
	public BufMgr(){}

	public void pin(){}
	public void unpin(){}

	// Technically should be the responsibility of the disk manager
	public void createPage(){}
	public void readPage(int pageNo){}
	public void writePage(){}
	// Calls frames display method
	public void displayPage(){}

	// Calls frames update method
	public void updatePage(int pageId, String newContent){

	}
}
