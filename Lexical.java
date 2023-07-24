import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/*
 * **************************************************
 * LEXICAL ANALYZER
 * read input file and construct tokens
 * Read file for each token when parser needs one
 * **************************************************
 */

class Lexical {
	private String line = null, type = null;
	private BufferedReader br;
	private String token;
	private ArrayList<String> letters = new ArrayList<String>();	//Collection of allowed letters
	private ArrayList<String> digits = new ArrayList<String>();	//Collection of allowed digits
	private ArrayList<String> operators = new ArrayList<String>();	//Collection of allowed operators
	private ArrayList<String> spaces = new ArrayList<String>();	//Collection of space and horizontal tab
	private ArrayList<String> punctions = new ArrayList<String>();	//Punctuations
	public ArrayList<String> reserved = new ArrayList<String>();	//Reserved tokens of grammar
	
	//Constructor to open file for reading

	Lexical(){ };
	
	Lexical(String fileName) {
		try {
			br = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//Initialize the allowed LETTERS, DIGITS and OPERATOS
		char ch = 'a';
		for (int i = 0 ; i < 26 ; i++)
			letters.add(""+ch++);
		ch = 'A';
		for (int i = 0 ; i < 26 ; i++)
			letters.add(""+ch++);
		for (int i = 0 ; i < 10 ; i++)
			digits.add(""+i);
		String[] s = new String[] {"+", "-", "*", "<", ">", "&", ".", "@", "/", ":", "=", "~", "|", "$", "!", "#", "%", 
				"^", "_", "[", "]", "{", "}", "\"", "`", "?"};

		operators.addAll(Arrays.asList(s));
		s = new String[] {" ", "\t", "\n"};
		spaces.addAll(Arrays.asList(s));
		s = new String[] {"(", ")", ";", ","};
		punctions.addAll(Arrays.asList(s));
		s = new String[] {"let", "in", "fn", "where", "aug", "or", "not", "gr", "ge", "ls", "le", "eq", "ne", "within", "and", "rec"};
		reserved.addAll(Arrays.asList(s));
	}


	boolean isOperatorSymbol(String token) {
		return this.operators.contains(token);
	}
	
	//Read a line from FILE
	
	private String readFile() {
		try {
			return br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	//Parse the current line and select a token to return. If EndOfLine reached, make line = null for next line reading.
	
	private String parse() {
		StringBuilder sb = new StringBuilder();	
		boolean cont;
		do {
			cont = false;
			if (line == null)
				return "";
			if (line.equals("")) {
				line = readFile();
				cont = true;
			}
			//IDENTIFIER
			else if (letters.contains(line.charAt(0)+"")) {
				type = "<IDENTIFIER>";	//TYPE can be needed by the parser corresponding to a token
				while (true) {
					sb.append(line.charAt(0)+"");	//Append that character to the current token
					line = line.substring(1);	//Trim the first character from the LINE for next reading
					if (line.length() == 0) {
						line = null;
						break;
					}
					if (letters.contains(line.charAt(0)+""))
						continue;
					else if (digits.contains(line.charAt(0)+""))
						continue;
					else if (line.charAt(0) == '_')
						continue;
					else
						break;
				}
			}
			//INTEGER
			else if (digits.contains(line.charAt(0)+"")) {
				type = "<INTEGER>";
				while (true) {
					sb.append(line.charAt(0)+"");
					line = line.substring(1);
					if (line.length() == 0) {
						line = null;
						break;
					}
					if (digits.contains(line.charAt(0)+""))
						continue;
					else
						break;
				}
			}
			//COMMENT
			else if (line.charAt(0) == '/' && line.charAt(1) == '/') {
				type = "<DELETE>";
				line = readFile();
				if (line == null)
					return "";
				cont = true;
			}
			//OPERATOR
			else if (operators.contains(line.charAt(0)+"")) {
				type = "<OPERATOR>";
				while (true) {
					sb.append(line.charAt(0)+"");
					line = line.substring(1);
					if (line.length() == 0) {
						line = null;
						break;
					}
					if (operators.contains(line.charAt(0)+""))
						continue;
					else
						break;
				}
			}
			//STRING
			else if (line.charAt(0) == '\'') {
				type = "<STRING>";
				while (true) {
					sb.append(line.charAt(0));
					line = line.substring(1);
					if (line.length() == 0) {
						line = null;
						break;
					}
					if (line.charAt(0) == '\\') {	//If escape character, don't consider the next character
						sb.append(line.charAt(0));
						line = line.substring(1);
						sb.append(line.charAt(0));
						line = line.substring(1);
					}
					if (line.charAt(0) == '\'') {
						sb.append(line.charAt(0));
						line = line.substring(1);
						if (line.length() == 0)
							line = null;
						break;
					}
				}
			}
			//SPACES
			else if (spaces.contains(line.charAt(0)+"")) {
				type = "<DELETE>";
				line = line.substring(1);
				if (line.length() == 0)
					line = readFile();
				if (line == null)
					return "";
				cont = true;
			}
			//PUNCTUATIONS
			else if (punctions.contains(line.charAt(0)+"")) {
				type = "<PUNCTIONS>";
				sb.append(line.charAt(0));
				line = line.substring(1);
				if (line.length() == 0)
					line = null;
			}
		} while (cont == true);
		
		return sb.toString();
	}
	
	//Get a new token for parsing. Read a new line if the previous one is finished.
	
	public String getToken() {
		token = "";
		if (line == null) {
			line = readFile();
		}
		if (line != null) {
			token = parse();
			if (token.equals(""))
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		else {	//No more contents left in file to read
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			token = "";
		}
		return token;
	}
	
	//Get type of the last returned token
	
	public String getType() {
		if (token.equals(""))
			type = "<EOF>";
		return type;
	}
}

