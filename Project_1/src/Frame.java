public class Frame {
	private int pinCount = 0;
	private boolean isDirty = false;
	private String _name;


	public Frame(String name){
		_name = name;
	}

	public int getPin(){return this.pinCount;}
	public int incPin(){return ++ this.pinCount;}
	public int decPin(){return -- this.pinCount;}
	public boolean isDirty(){return isDirty;}
	public void setDirty(boolean newDirtyValue){this.isDirty = newDirtyValue;}
	public void displayPage(){}

	@Override
	public boolean equals(Object that){
		return (that != null && (that instanceof Frame) && _name.equals(((Frame)that)._name));
	}

	@Override
	public String toString(){
		return _name;
	}
}
