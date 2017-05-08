import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class BPlusTree extends BPlusTreeGeneric<String, BTRecord>{

	public BPlusTree(int degree) {
		super(degree);
	}

	private BTRecord put(){
		return new BTRecord();
	}

	public boolean insertWord(String word) {
		int sizeBefore = _nodeContainer.getSize();
		put(word, new BTRecord(0)).incFrequency();
		return _nodeContainer.getSize() == sizeBefore;
	}

	public boolean containsWord(String word){
		return containsKey(word);
	}

	public int getCount(String key){
		BTRecord count = get(key);
		return (count != null) ? count.getFrequency() : 0;
	}

	public Collection<String> getWords(){
		return keySet();
	}

	public Collection<String> rangeMatch(String lowerBound, String upperBound) {
		Collection<Map.Entry<String, BTRecord>> matches = search(lowerBound, upperBound);
		ArrayList<String> words = new ArrayList<>();

		for(Entry<String, BTRecord> thisEntry : matches){
			words.add(thisEntry.getKey());
		}

		return words;
	}

	public BTNode<String, BTRecord> findNodeById(int id){
		return _nodeContainer.getNode().findNodeById(id);
	}

	@Override
	public String toString(){
		return _nodeContainer.getNode().toString(0);
	}
}
