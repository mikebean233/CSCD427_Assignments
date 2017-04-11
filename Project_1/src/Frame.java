import java.io.File;
import java.io.FileWriter;

public class Frame {
	private int pinCount = 0;
	private boolean isDirty = false;
	private StringBuilder _pageContents;
	private boolean _isEmptyFrame = false;


	public static Frame buildEmptyFrame(){
		return new Frame();
	}

	private Frame(){
		this("");
		_isEmptyFrame = true;
	}

	public Frame(String contents){
		_pageContents = new StringBuilder();
		_pageContents.append(contents);
	}

	public int getPin(){return this.pinCount;}
	public int incPin(){return ++ this.pinCount;}
	public int decPin(){return -- this.pinCount;}
	public boolean isDirty(){return isDirty;}
	public boolean isEmptyFrame(){return _isEmptyFrame;}
	public void setDirty(boolean newDirtyValue){this.isDirty = newDirtyValue;}
	public String displayPage(){return _pageContents.toString();}
	public void updatePage(String newContents){_pageContents.append(newContents);}
}
