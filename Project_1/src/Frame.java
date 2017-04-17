public class Frame {
	private int _pinCount = 0;
	private boolean _isDirty = false;
	private StringBuilder _pageContents;
	private boolean _isEmptyFrame = false;

	public static Frame buildEmptyFrame(){
		return new Frame();
	}

	public Frame(){
		_pageContents = new StringBuilder();
		_isEmptyFrame = true;
	}

	public Frame setContents(String contents) throws FrameDataLossException{
		if(_isDirty)
			throw new FrameDataLossException("Dirty frames cannot have their contents replaced");

		_isEmptyFrame = false;
		_pageContents = new StringBuilder();
		_pageContents.append(contents);
		return this;
	}

	public int getPin(){
		return _pinCount;
	}

	public Frame incPin(){
		++_pinCount;
		return this;
	}

	public Frame decPin(){
		_pinCount = Math.max(_pinCount - 1, 0);
		return this;
	}
	public boolean isDirty() {
		return _isDirty;
	}

	public boolean  isEmptyFrame(){return _isEmptyFrame;}

	public Frame setDirty(boolean value){
		_isDirty = value;
		return this;
	}

	public String displayPage(){
		return _pageContents.toString();
	}

	public Frame updatePage(String newContents){
		_pageContents.append(System.lineSeparator());
		_pageContents.append(newContents);
		setDirty(true);
		return this;
	}

	public class FrameDataLossException extends Exception {
		public FrameDataLossException(String message){
			super(message);
		}
	}
}
