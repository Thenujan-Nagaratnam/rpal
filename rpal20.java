/*
 * **************************************************
 * MAIN CLASS to run
 * **************************************************
 */

 public class rpal20 {

	public static void main(String args[]){
		String fileName = "";
		String AST_Text = "";
		if (args.length == 0) {	//No switches => no output
			System.exit(0);
		}
		else if (args.length == 1) {
			fileName = args[0];
			Parser p = new Parser(fileName);
			Node AST  = p.getAST();
			STTransformer TreeConstructor = new STTransformer();
			TreeConstructor.constructAST(AST);
			TreeConstructor.constructST();
		}
		else if (args.length == 2 && args[0].equalsIgnoreCase("-ast")) {	//Generate AST by reading FILE
			fileName = args[1];
			Parser p = new Parser(fileName);
			AST_Text = p.startParsing();
			System.out.print(AST_Text);
		}
		else {
			System.err.println("Error: Illegal parameters");
			System.exit(0);
		}
	}
}