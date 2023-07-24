//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.ArrayDeque;
//import java.util.List;
//
////class TempTreeNode extends Node {
////
////    private int depthOfTree;
////    private TempTreeNode parent;
////
////    TempTreeNode(String token, int depthOfTree) {
////        super(token);
////        this.depthOfTree = depthOfTree;
////        this.parent = null;
////    }
////
////    int getDepth() {
////        return depthOfTree;
////    }
////
////    TempTreeNode getParent() {
////        return parent;
////    }
////
////    void setParent(TempTreeNode parent) {
////        this.parent = parent;
////    }
////}
//
//// Parsing the Abstract Tree into Standardize Tree
//public class ASTParser extends Traversal {
//
//    private String FileName;
//    private List<String> tokenStrings;
//
//    ASTParser(String fileName) {
//        super();
//        FileName = fileName;
//        getTokens();
//        createTree();
//    }
//
//    private void getTokens() {
//        try {
//            this.tokenStrings = Files.readAllLines(Paths.get(this.FileName));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void createTree(Node AST) {
//        ArrayDeque<Node> stack = AST;
//    }
////
////    private void createTree() {
////        ArrayDeque<Node> stack = new ArrayDeque<>();
////        for (String each : this.tokenStrings) {
////            String tokenValue = each.replaceAll("\\.", "");
////            int depthOfTree = each.length() - tokenValue.length();
////            TempTreeNode node = new TempTreeNode(tokenValue, depthOfTree);
////            stack.addLast(node);
////        }
////        var root = stack.pop();
////        var previous = root;
////        while (stack.size() != 0) {
////            var next = stack.pop();
////            insertNode(previous, next);
////            previous = next;
////        }
////        this.stack.push(root);
////    }
////
////    // Insert the node. It takes two input parameters
////    private void insertNode(TempTreeNode previous, TempTreeNode next) {
////        if (previous.getDepth() == next.getDepth()) {
////            // siblings insert right
////            if (previous.getRightChild() == null) {
////                next.setParent(previous);
////                previous.setRightChild(next);
////            } else {
////                insertNode((TempTreeNode) previous.getRightChild(), next);
////            }
////        } else if (previous.getDepth() < next.getDepth()) {
////            // children insert left
////            if (previous.getLeftChild() == null) {
////                next.setParent(previous);
////                previous.setLeftChild(next);
////            } else {
////                insertNode((TempTreeNode) previous.getLeftChild(), next);
////            }
////        } else {
////            // move up
////            insertNode(previous.getParent(), next);
////        }
////    }
//}
