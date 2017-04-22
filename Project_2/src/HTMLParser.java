import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashSet;

public class HTMLParser implements Runnable{
	Collection<String> _tokenList;
	Collection<String> _ignoreList;
	File _inputFile;

	private enum TokenCursorStatus {
		ON,
		OFF,
		END,
		BEGINNING,
		ERROR
	}

	public HTMLParser(String fileName, Collection<String> tokenList){                                this(new File(fileName), tokenList, new HashSet<>());}
	public HTMLParser(File inputFile , Collection<String> tokenList){                                this(inputFile         , tokenList, new HashSet<>());}
	public HTMLParser(String fileName, Collection<String> tokenList, Collection<String> ignoreList){ this(new File(fileName), tokenList, ignoreList);     }

	public HTMLParser(File inputFile, Collection<String> tokenList, Collection<String> ignoreList){
		if(inputFile == null || tokenList == null || ignoreList == null)
			throw new IllegalArgumentException("HTMLParser");

		_inputFile = inputFile;
		_ignoreList = ignoreList;
		_tokenList = tokenList;
	}

	@Override
	public void run() {
		parse();
	}

	// Note: This method is doing no error checking of the html
	void parse(){
		try {
			FileReader reader = new FileReader(_inputFile);
			char prevCharacter      = '\0';
			char thisCharacter      = '\0';
			boolean betweenTags     = true;
			StringBuilder thisToken = new StringBuilder();

			while((thisCharacter = (char)reader.read()) != (char)-1){

				if (thisCharacter == '<')
					betweenTags = false;

				if (prevCharacter == '>')
					betweenTags = true;

				TokenCursorStatus cursorStatus = getCursorStatus(prevCharacter, thisCharacter);

				// We are on a token
				if (betweenTags && (cursorStatus == TokenCursorStatus.BEGINNING || cursorStatus == TokenCursorStatus.ON))
					thisToken.append(thisCharacter);

				// We are just stepping off of a token
				if (cursorStatus == TokenCursorStatus.END)
					tryAddToken(thisToken);

				// Get ready for the next character
				prevCharacter = thisCharacter;

			}// end while
			tryAddToken(thisToken);

			reader.close();
		} // end try
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void tryAddToken(StringBuilder thisToken){
		if(thisToken.length() > 0){
			String tokenString = thisToken.toString();
			if(!_ignoreList.contains(tokenString))
				_tokenList.add(tokenString);
			thisToken.delete(0, thisToken.length());
		}
	}

	private TokenCursorStatus getCursorStatus(char leftChar, char rightChar){
		boolean leftWhiteSpace  = Character.isWhitespace(leftChar)  || leftChar  == '>';
		boolean rightWhiteSpace = Character.isWhitespace(rightChar) || rightChar == '<';

		if( !leftWhiteSpace && !rightWhiteSpace) return TokenCursorStatus.ON;
		if(  leftWhiteSpace &&  rightWhiteSpace) return TokenCursorStatus.OFF;
		if( !leftWhiteSpace &&  rightWhiteSpace) return TokenCursorStatus.END;
		if(  leftWhiteSpace && !rightWhiteSpace) return TokenCursorStatus.BEGINNING;

		return TokenCursorStatus.ERROR;
	}

}
