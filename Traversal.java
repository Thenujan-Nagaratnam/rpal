import java.util.ArrayList;
import java.util.Stack;

// Class used to traverse the node tree.
// It has some functions 
//  1. getRoot   => return the root node 
//  2. preOrderTraversal  => Traverse the tree in preorder method (root,left,right)
//  3. preOrder   =>  Set the tree in a array in preorder level.
class Traversal {
    Lexical lexer = new Lexical();

    Stack<Node> stack = new Stack<>();

    private Node root;

    Node getRoot() {
        return this.root;
    }

    private void setRoot(Node root) {
        this.root = root;
    }

    void preOrderTraversal() {
        int depthOfTree = 0;

        if (stack.empty()) {
            throw new RuntimeException("Error!! The Stack is empty");
        }
        Node root = stack.pop();

        this.setRoot(root);
        preOrder(root, depthOfTree);
    }

    void preOrder(Node t, int depthOfTree) {
        ArrayList<String> elements = new ArrayList<>();

        elements.add(t.getToken());
        depthOfTree++;
        if (t.getLeftChild() != null) {
            preOrder(t.getLeftChild(), depthOfTree);
        }
        if (t.getRightChild() != null) {
            preOrder(t.getRightChild(), depthOfTree - 1);
        }
    }

}