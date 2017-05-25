import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Scheme implements Iterable<String>{
	private final String _name;
	private final LinkedHashSet<String> _scheme;


	public Scheme(List<String> scheme, String name){
		if(scheme == null || scheme.size() == 0 || name == null)
			throw new IllegalArgumentException("Scheme");

		_scheme = new LinkedHashSet<>();
		scheme.forEach(thisAttribute -> {
			if(_scheme.contains(thisAttribute))
				throw new IllegalArgumentException("Scheme: Invalid scheme, duplicate attribute \"" + thisAttribute + "\"");

			_scheme.add(thisAttribute);
		});
		_name = name;
	}

	public Scheme(String[] scheme, String name){
		this(Arrays.asList(scheme), name);
	}


	@SuppressWarnings("unchecked")
	public Scheme(LinkedHashSet<String> scheme, String name){
		if(scheme == null || name == null || name.trim().length() == 0)
			throw new IllegalArgumentException("Scheme");

		_scheme = (LinkedHashSet<String>) scheme.clone();
		_name = name;
	}

	@Override
	public Iterator<String> iterator() {return _scheme.iterator();}

	public String getName(){return _name;}

	public int size(){return _scheme.size();}

	public String getFilename(){return getName() + ".txt";}

	public boolean containsAttribute(String attribute){return _scheme.contains(attribute);}

	@SuppressWarnings("unchecked")
	public LinkedHashSet<String> getScheme(){return (LinkedHashSet<String>)_scheme.clone();}

	public static List<String>  getCommonAttributes(Scheme a, Scheme b){
		Set<String> aScheme = a.getScheme(), bScheme = b.getScheme();
		return aScheme.stream().filter(bScheme::contains).collect(Collectors.toList());
	}

	@Override
	public String toString(){
		return _scheme.toString();
	}

	public static Scheme buildJoinScheme(Scheme leftScheme, Scheme rightScheme){
		if(leftScheme == null || rightScheme == null)
			throw new IllegalArgumentException("buildJoinScheme");

		List<String> commonAttributes = getCommonAttributes(leftScheme, rightScheme);

		return new Scheme(
				Stream.concat(
					leftScheme.getScheme().stream(),
					rightScheme.getScheme().stream().filter(attr -> !commonAttributes.contains(attr))
				).collect(Collectors.toList()),
				leftScheme.getName() + "_" + rightScheme.getName()
		);

	}
}
