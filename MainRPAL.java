
// Containing the main function.
public class MainRPAL {
    public static void main(String[] args) {
        String inputFileName = args[0]; // Get the arg value which is userinput, the rpal tree input (txt file)

        STTransformer TreeConstructor = new STTransformer();

        TreeConstructor.constructAST(inputFileName);
        // Construct the Abstract Syntax Tree from the data in the txt file
        TreeConstructor.constructST();
    }
}
