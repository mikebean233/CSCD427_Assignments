import java.util.LinkedList;

// TODO: Implement as a separate Chaining hash table
public class BufHashTbl {
	private int size;
	private FrameList[] data;

	public BufHashTbl(int inSize){
		if(size <= 0)
			throw new IndexOutOfBoundsException("BufHashTbl must have a size greater then zero");

		data = new FrameList[inSize];

	}



	public void insert(int frameNo, int pageNo){}
	public void lookup(int pageNo){}
	public void remove(int frameNo, int pageNo){}
}
