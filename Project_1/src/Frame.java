public class Frame {
	private int pinCount = 0;
	private boolean isDirty = false;

	public Frame(){}

	public int getPin(){return this.pinCount;}
	public int incPin(){return ++ this.pinCount;}
	public int decPin(){return -- this.pinCount;}
	public boolean isDirty(){return isDirty;}
	public void setDirty(boolean newDirtyValue){this.isDirty = newDirtyValue;}
	public void displayPage(){}
}
