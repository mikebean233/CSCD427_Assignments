// TODO: Implement as a separate Chaining hash table
public class BufHashTbl {
	private int _size;
	private FramePageMappingList[] _map;


	public BufHashTbl(int size){
		if((_size = size) <= 0)
			throw new IndexOutOfBoundsException("BufHashTbl must have a size greater then zero");

		_map = new FramePageMappingList[size];
		for(int i = 0; i < _size; ++i)
			_map[i] = new FramePageMappingList();
	}

	public void insert(int frameNo, int pageNo){
		FramePageMapping thisMapping = new FramePageMapping(frameNo, pageNo);
		int tableEntryIndex = getTableEntryIndex(pageNo);
		_map[tableEntryIndex].add(thisMapping);
	}

	private int getTableEntryIndex(int pageNo){
		return pageNo % _size;
	}

	// Returns the frame Number
	public int lookup(int pageNo){
		return getMappingByPage(pageNo).getFrameNo();
	}

	private FramePageMapping getMappingByPage(int pageNo){
		FramePageMappingList list = _map[getTableEntryIndex(pageNo)];

		for(FramePageMapping thisMapping : list)
			if(thisMapping.getPageNo() == pageNo)
				return thisMapping;

		return FramePageMapping.nullMapping();
	}

	public boolean remove(int pageNo){
		FramePageMappingList list = _map[getTableEntryIndex(pageNo)];
		FramePageMapping thisMapping = getMappingByPage(pageNo);

		if(!thisMapping.isNullMapping()) {
			list.remove(thisMapping);
			return true;
		}
		return false;
	}

	@Override
	public String toString(){
		StringBuilder stringBuilder = new StringBuilder();

		for(int i = 0; i < _map.length; ++i){
			stringBuilder.append(i);
			stringBuilder.append(": ");
			stringBuilder.append(_map[i].toString());
			stringBuilder.append(System.lineSeparator());
		}
		return stringBuilder.toString();
	}
}
