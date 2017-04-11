public class FramePageMapping {
	private int _frameNo = 0;
	private int _pageNo = 0;

	public static FramePageMapping nullMapping(){
		return new FramePageMapping(-1, -1);
	}

	public FramePageMapping(int frameNo, int pageNo) {
		_frameNo = frameNo;
		_pageNo = pageNo;
	}

	public int getFrameNo(){return _frameNo;}
	public int getPageNo(){return _pageNo;}

	@Override
	public boolean equals(Object o){
		if (o == null || !(o instanceof FramePageMapping))
			return false;
		FramePageMapping that = (FramePageMapping) o;

		return  (!isNullMapping() && !that.isNullMapping()) && that._frameNo == this._frameNo && that._pageNo == this._pageNo;
	}

	@Override
	public int hashCode(){
		 return this._pageNo;
	}

	public boolean isNullMapping(){
		return _frameNo == _pageNo && _frameNo ==  -1;
	}
}
