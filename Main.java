import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

/*
 * **************************************************
 * MAIN CLASS to run
 * **************************************************
 */

 public class P1 {

	public static void main(String args[]){
		String fileName;
		if (args.length == 0) {	//No switches => no output
			System.exit(0);
		}
		else if (args.length == 1) {
			if (args[0].equalsIgnoreCase("-l")) {	//Listing
				// TO DO : listing
			}
			else if (args[0].equalsIgnoreCase("-ast")) {
				System.err.println("Error: FILE_NAME expected");
				System.exit(0);
			}
			else {
				System.err.println("Error: Unidentified Switch");
				System.exit(0);
			}
		}
		else if (args.length == 2 && args[0].equalsIgnoreCase("-ast")) {	//Generate AST by reading FILE
			fileName = args[1];
			Parser p = new Parser(fileName);
			p.startParsing();
		}
		else {
			System.err.println("Error: Illegal parameters");
			System.exit(0);
		}
	}
}