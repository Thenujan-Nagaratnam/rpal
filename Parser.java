import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

/*
 * **************************************************
 * PARSER
 * Request a token from Lexical and parse it accordingly
 * **************************************************
 */

 class Parser {
	Lexical l;
	String token;
	Stack<Node> stack = new Stack<Node>();
	
	//Constructor to initialize Lexical with fileName
	
	Parser(String fileName) {
		l = new Lexical(fileName);
	}
	
	//Read and compare the token with the expected value
	
	private void read(String s) {
		if (!s.equals(token)) {	//SYNTAX ERROR !! exit.
			System.err.println("Expected '"+s+"' but found '"+token+"'");
			System.exit(0);
		}
		if ((!l.reserved.contains(token)) && (l.getType().equals("<IDENTIFIER>") || l.getType().equals("<INTEGER>") || l.getType().equals("<STRING>")))
			buildTree(token, 0);	//Build a node in case of identifiers, integers and strings
		token = l.getToken();
	}
	
	//Build Tree by popping the specified number of trees from stack and join with root node specified
	
	private void buildTree(String root, int n) {
		int i = 0;	//To pop the required number of trees
		Node temp = null;
		String s;
		if (n == 0 && l.getType().equals("<IDENTIFIER>") && !l.reserved.contains(root))
			s = "<ID:"+root+">";
		else if (n == 0 && l.getType().equals("<INTEGER>"))
			s = "<INT:"+root+">";
		else if (n == 0 && l.getType().equals("<STRING>") && !l.reserved.contains(root))
			s = "<STR:"+root+">";
		else s = root;
		while (i < n) {
			Node sib = stack.pop();
			sib.setRightChild(temp);	//First popped node has rightChild set as null, second popped has rightChild as 'first popped'
			temp = sib;
			i++;
		}
		Node r = new Node();
		r.setToken(s);
		r.setLeftChild(temp);
		r.setRightChild(null);
		stack.push(r);	//Push the newly constructed tree which has root as token='root' from argument
	}
	
	/*
	 * E -> 'let' D 'in' E		=> 'let'
	 *   -> 'fn' Vb+ '.' 'E'	=> 'lambda'
	 *   -> Ew;
	 */
	private void E() {
		if (token.equals("let")) {
			read("let");
			D();
			read("in");
			E();
			buildTree("let", 2);
		}
		else if (token.equals("fn")) {
			read("fn");
			int n = 0;
			do {
				Vb();
				n++;
			} while (l.getType().equals("<IDENTIFIER>") | token.equals("("));
			read(".");
			E();
			buildTree("lambda", n+1);
		}
		else {
			Ew();
		}
	}
	
	/*
	 * Ew -> T 'where' Dr	=> 'where'
	 *    -> T;
	 */
	private void Ew() {
		T();
		if (token.equals("where")) {
			read("where");
			Dr();
			buildTree("where", 2);
		}
	}
	
	/*
	 * T -> Ta ( ',' Ta )+	=> 'tau'
	 *   -> Ta;
	 */
	private void T() {
		Ta();
		int n = 0;
		while (token.equals(",")) {
			read(",");
			Ta();
			n++;
		}
		if (n > 0) {
			buildTree("tau", n+1);
		}
	}
	
	/*
	 * Ta -> Ta 'aug' Tc	=> 'aug'
	 *    -> Tc;
	 */
	private void Ta() {
		Tc();
		while (token.equals("aug")) {
			read("aug");
			Tc();
			buildTree("aug", 2);
		}
	}
	
	/*
	 * Tc -> B '->' Tc '|' Tc	=> '->'
	 *    -> B;
	 */
	private void Tc() {
		B();
		if (token.equals("->")) {
			read("->");
			Tc();
			read("|");
			Tc();
			buildTree("->", 3);
		}
	}
	
	/*
	 * B -> B 'or' Bt	=> 'or'
	 *   -> Bt;
	 */
	private void B() {
		Bt();
		while (token.equals("or")) {
			read("or");
			Bt();
			buildTree("or", 2);
		}
	}
	
	/*
	 * Bt -> Bt '&' Bs	=> '&'
	 *    -> Bs;
	 */
	private void Bt() {
		Bs();
		while (token.equals("&")) {
			read("&");
			Bs();
			buildTree("&", 2);
		}
	}
	
	/*
	 * Bs -> 'not' Bp	=> 'not'
	 *    -> Bp;
	 */
	private void Bs() {
		if (token.equals("not")) {
			read("not");
			Bp();
			buildTree("not", 1);
		}
		else {
			Bp();
		}
	}
	
	/*
	 * Bp -> A ( 'gr' | '>' ) A		=> 'gr'
	 *    -> A ( 'ge' | '>=' ) A	=> 'ge'
	 *    -> A ( 'ls' | '<' ) A		=> 'ls'
	 *    -> A ( 'le' | '<=' ) A	=> 'le'
	 *    -> A 'eq' A				=> 'eq'
	 *    -> A 'ne' A				=> 'ne'
	 *    -> A;
	 */
	private void Bp() {
		A();
		if (token.equals("gr") || token.equals(">")) {
			read(token);
			A();
			buildTree("gr", 2);
		}
		else if (token.equals("ge") || token.equals(">=")) {
			read(token);
			A();
			buildTree("ge", 2);
		}
		else if (token.equals("ls") || token.equals("<")) {
			read(token);
			A();
			buildTree("ls", 2);
		}
		else if (token.equals("le") || token.equals("<=")) {
			read(token);
			A();
			buildTree("le", 2);
		}
		else if (token.equals("eq")) {
			read("eq");
			A();
			buildTree("eq", 2);
		}
		else if (token.equals("ne")) {
			read("ne");
			A();
			buildTree("ne", 2);
		}
	}
	
	/*
	 * A -> A '+' At	=> '+'
	 *   -> A '-' At	=> '-'
	 *   -> '+' At
	 *   -> '-' At		=> 'neg'
	 *   -> At;
	 */
	private void A() {
		if (token.equals("+")) {
			read("+");
			At();
		}
		else if (token.equals("-")) {
			read("-");
			At();
			buildTree("neg", 1);
		}
		else {
			At();
		}
		String temp;
		while (token.equals("+") || token.equals("-")) {
			temp = token;
			read(temp);
			At();
			buildTree(temp, 2);
		}
	}
	
	/*
	 * At -> At '*' Af	=> '*'
	 *    -> At '/' Af	=> '/'
	 *    -> Af;
	 */
	private void At() {
		Af();
		String temp;
		while (token.equals("*") || token.equals("/")) {
			temp = token;
			read(temp);
			Af();
			buildTree(temp, 2);
		}
	}
	
	/*
	 * Af -> Ap '**' Af		=> '**'
	 *    -> Ap;
	 */
	private void Af() {
		Ap();
		if (token.equals("**")) {
			read("**");
			Af();
			buildTree("**", 2);
		}
	}
	
	/*
	 * Ap -> Ap '@' '<identifier>' R	=> '@'
	 *    -> R;
	 */
	private void Ap() {
		R();
		while (token.equals("@")) {
			read("@");
			read(token);
			R();
			buildTree("@", 3);
		}
	}
	
	/*
	 * R -> R Rn	=> 'gamma'
	 *   -> Rn;
	 */
	private void R() {
		Rn();
		while ((!l.reserved.contains(token)) && (l.getType().equals("<IDENTIFIER>") || l.getType().equals("<INTEGER>") || l.getType().equals("<STRING>") || 
				token.equals("true") || token.equals("false") || token.equals("nil") || token.equals("(") || token.equals("dummy"))) {
			Rn();
			buildTree("gamma", 2);
		}
	}
	
	/*
	 * Rn -> '<identifier>'
	 *    -> '<integer>'
	 *    -> '<string>'
	 *    -> 'true'			=> 'true'
	 *    -> 'false'		=> 'false'
	 *    -> 'nil'			=> 'nil'
	 *    -> '(' E ')'
	 *    -> 'dummy'		=> 'dummy'
	 */
	private void Rn() {
		if (l.getType().equals("<IDENTIFIER>") || l.getType().equals("<INTEGER>") || l.getType().equals("<STRING>")) {
			read(token);
		}
		else if (token.equals("true")) {
			read("true");
			buildTree("true", 0);
		}
		else if (token.equals("false")) {
			read("false");
			buildTree("false", 0);
		}
		else if (token.equals("nil")) {
			read("nil");
			buildTree("nil", 0);
		}
		else if (token.equals("(")) {
			read("(");
			E();
			read(")");
		}
		else if (token.equals("dummy")) {
			read("dummy");
			buildTree("dummy", 0);
		}
	}
	
	/*
	 * D -> Da 'within' D	=> 'within'
	 *   -> Da;
	 */
	private void D() {
		Da();
		if (token.equals("within")) {
			read("within");
			D();
			buildTree("within", 2);
		}
	}
	
	/*
	 * Da -> Dr ( 'and' Dr )+	=> 'and'
	 *    -> Dr;
	 */
	private void Da() {
		Dr();
		int n = 0;
		while (token.equals("and")) {
			read("and");
			Dr();
			n++;
		}
		if (n > 0)
			buildTree("and", n+1);
	}
	
	/*
	 * Dr -> 'rec' Db	=> 'rec'
	 *    -> Db;
	 */
	private void Dr() {
		if (token.equals("rec")) {
			read("rec");
			Db();
			buildTree("rec", 1);
		}
		else
			Db();
	}
	
	/*
	 * Db -> Vl '=' E					=> '='
	 *    -> '<identifier>' Vb+ '=' E	=> 'fcn_form'
	 *    -> '(' D ')';
	 */
	private void Db() {
		if (l.getType().equals("<IDENTIFIER>")) {
			Vl();
			if (token.equals("=")) {
				read("=");
				E();
				buildTree("=", 2);
			}
			else {
				int n = 0;
				while (l.getType().equals("<IDENTIFIER>") || token.equals("(")) {
					Vb();
					n++;
				}
				read("=");
				E();
				buildTree("function_form", n+2);
			}
		}
		else if (token.equals("(")) {
			read("(");
			D();
			read(")");
		}
	}
	
	/*
	 * Vb -> '<identifier>'
	 *    -> '(' Vl ')'
	 *    -> '(' ')'		=> '()';
	 */
	private void Vb() {
		if (l.getType().equals("<IDENTIFIER>"))
			read(token);
		else if (token.equals("(")) {
			read("(");
			if (token.equals(")")) {
				read(")");
				buildTree("()", 2);
			}
			else {
				Vl();
				read(")");
			}
		}
	}
	
	/*
	 * Vl -> '<identifier>' list ','	=> ','?;
	 */
	private void Vl() {
		if (l.getType().equals("<IDENTIFIER>")) {
			read(token);
		}
		int n = 0;
		while (token.equals(",")) {
			read(",");
			read(token);
			n++;
		}
		if (n > 0)
			buildTree(",", n+1);
	}
	
	//Traverse the AST in pre-order and print the tokens
	
	private void preOrder(Node n, int level, String AST) {
		String dot = "";
		for (int i = 0 ; i < level ; i++)
			dot = dot + ".";
		System.out.println(dot+""+n.getToken());
		AST = AST + dot + "" + n.getToken() + "\n";
		if (n.getLeftChild() != null) {
			preOrder(n.getLeftChild(), level+1, AST);
		}
		if (n.getRightChild() != null) {
			preOrder(n.getRightChild(), level, AST);
		}
	}
	//Print the generated Abstract Syntax Tree
	
	private String printAST() {
		Node ast = stack.pop();
		String AST = "";
		preOrder(ast, 0, AST);
		return AST;
	}
	
	//Start Parsing and call PRINT upon completion
	
	public String startParsing() {
		token = l.getToken();
		E();
		String AST = "";
		if (l.getType().equals("<EOF>")) {	//EOF reached
			AST = printAST();
		}
		return AST;
	}
}

