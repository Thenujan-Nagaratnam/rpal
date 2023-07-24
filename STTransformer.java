import java.util.ArrayList;

public class STTransformer extends Traversal {

    static final String GAMMA = "gamma";
    static final String LAMBDA = "lambda";

    private Traversal p = null;
    private ArrayList<String> controlStructure = new ArrayList<>();
    private static int counter = 1;
    private ArrayList<Node> stack = new ArrayList<>();
    private ArrayList<ArrayList<String>> controlStructureArray = new ArrayList<>();

    private void convertLet(Node node) {
        Node X = null;
        Node E = null;
        Node P = null;

        Node lambdaNode = new Node(LAMBDA);

        if (!node.getToken().equals("let")) {
            return;
        }
        try {
            node.setToken(GAMMA);

            if (node.getLeftChild().getToken().equals("=")) {

                P = node.getLeftChild().getRightChild();
                X = node.getLeftChild().getLeftChild();
                E = node.getLeftChild().getLeftChild().getRightChild();
            }
            node.setLeftChild(lambdaNode);
            lambdaNode.setRightChild(E); // This would be the transformed lambda node.
            lambdaNode.setLeftChild(X);
            assert X != null;
            X.setRightChild(P);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void convertWhere(Node node) {
        Node X;
        Node E;
        Node P;

        Node lambdaNode = new Node(LAMBDA);
        if (!node.getToken().equals("where")) {
            return;
        }
        node.setToken(GAMMA);

        if (node.getLeftChild().getRightChild().getToken().equals("=")) {
            P = node.getLeftChild();
            X = node.getLeftChild().getRightChild().getLeftChild();
            E = node.getLeftChild().getRightChild().getLeftChild().getRightChild();

            node.setLeftChild(lambdaNode);
            node.setRightChild(null); // null the right child of the incoming where node
            P.setRightChild(null);
            X.setRightChild(P);
            lambdaNode.setLeftChild(X);
            lambdaNode.setRightChild(E);

            preOrder(node, 0);
        }

    }

    private void convertWithin(Node node) {
        Node X1;
        Node E1;
        Node X2;
        Node E2;

        Node gammaNode = new Node(GAMMA);
        Node lambdaNode = new Node(LAMBDA);

        if (!node.getToken().equals("within")) {
            return;
        }

        node.setToken("=");

        if (node.getLeftChild().getToken().equals("=")
                && node.getLeftChild().getRightChild().getToken().equals("=")) {
            X1 = node.getLeftChild().getLeftChild();
            E1 = node.getLeftChild().getLeftChild().getRightChild();
            X2 = node.getLeftChild().getRightChild().getLeftChild();
            E2 = node.getLeftChild().getRightChild().getLeftChild().getRightChild();

            node.setLeftChild(X2);
            X2.setRightChild(gammaNode);
            X2.setLeftChild(null);
            gammaNode.setLeftChild(lambdaNode);
            gammaNode.setRightChild(null);
            lambdaNode.setRightChild(E1);
            lambdaNode.setLeftChild(X1);
            X1.setRightChild(E2);
            X1.setLeftChild(null);
        }
    }

    private void convertRec(Node node) {
        Node X;
        Node E;

        Node ystar = new Node("<Y*>");

        Node gammaNode = new Node(GAMMA);
        Node lambdaNode = new Node(LAMBDA);

        if (!node.getToken().equals("rec")) {
            return;
        }
        node.setToken("=");
        if (node.getLeftChild().getToken().equals("=")) {

            X = node.getLeftChild().getLeftChild();
            Node newX = new Node(X);

            E = node.getLeftChild().getLeftChild().getRightChild();

            node.setLeftChild(X);
            X.setRightChild(gammaNode);
            X.setLeftChild(null);
            gammaNode.setLeftChild(ystar);

            ystar.setRightChild(lambdaNode);
            lambdaNode.setLeftChild(newX);
            newX.setRightChild(E);

        }
    }

    private void convertFcnForm(Node node) {

        Node P;
        ArrayList<Node> V = new ArrayList<>();
        Node E;

        Node lambdaNode = new Node(LAMBDA);
        try {
            if (!node.getToken().equals("function_form")) {
                return;
            }
            node.setToken("="); // setting function_form to be '='

            P = node.getLeftChild();
            Node PCopy = P;

            while (!(PCopy.getRightChild().getToken().equals("->") ||
                    PCopy.getRightChild().getToken().equals(GAMMA) ||
                    PCopy.getRightChild().getToken().startsWith("<INT:") ||
                    lexer.isOperatorSymbol(PCopy.getRightChild().getToken()) ||
                    PCopy.getRightChild().getToken().equalsIgnoreCase("aug"))) {
                V.add(PCopy.getRightChild());
                PCopy = PCopy.getRightChild();
            }

            E = PCopy.getRightChild(); // The last node should be E
            P.setRightChild(lambdaNode);

            for (int i = 0; i < V.size(); i++) {
                lambdaNode.setLeftChild(V.get(i));
                if (V.size() > 1) {
                    lambdaNode = new Node(LAMBDA);
                    V.get(i).setRightChild(lambdaNode);
                }
            }

            V.get(V.size() - 1).setRightChild(E);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void convertAnd(Node node) {

        ArrayList<Node> X = new ArrayList<>();
        ArrayList<Node> E = new ArrayList<>();

        Node commaNode = new Node(",");
        Node tauNode = new Node("tau");

        if (!node.getToken().equals("and")) {
            return;
        }

        node.setToken("="); // Converting and to '='

        if (node.getLeftChild().getToken().equals("=")) {

            Node equalNode = node.getLeftChild();
            Node equalNodeCopy = equalNode;
            while (equalNodeCopy != null) {

                X.add(equalNodeCopy.getLeftChild());
                E.add(equalNodeCopy.getLeftChild().getRightChild());
                equalNodeCopy = equalNodeCopy.getRightChild();
            }

            // Disconnect the last X node's right child.
            X.get(X.size() - 1).setRightChild(null);

            node.setLeftChild(commaNode);
            commaNode.setRightChild(tauNode);

            commaNode.setLeftChild(X.get(0));
            tauNode.setLeftChild(E.get(0));

            for (int i = 0; i < X.size() - 1; i++) {
                X.get(i).setRightChild(X.get(i + 1));
            }

            for (int i = 0; i < E.size() - 1; i++) {
                E.get(i).setRightChild(E.get(i + 1));
            }

        }
    }

    private void convertAtTheRateOf(Node node) {
        Node E1;
        Node N;
        Node E2;

        Node gammaNode2 = new Node(GAMMA);
        if (!node.getToken().equals("@")) {
            return;
        }

        node.setToken(GAMMA);
        E1 = node.getLeftChild();
        N = E1.getRightChild();
        E2 = N.getRightChild();

        node.setLeftChild(gammaNode2);
        gammaNode2.setRightChild(E2);
        gammaNode2.setLeftChild(N);

        N.setRightChild(E1);
        E1.setRightChild(null);
    }

    private void postOrder(Node t) {
        if (t.getLeftChild() != null) {
            postOrder(t.getLeftChild());
        }
        if (t.getRightChild() != null) {
            postOrder(t.getRightChild());
        }
        reduceConstruct(t);
    }

    private void reduceConstruct(Node t) {
        String nodeValue = t.getToken();

        switch (nodeValue) {
            case "let":
                convertLet(t);
                break;
            case "where":
                convertWhere(t);
                break;
            case "within":
                convertWithin(t);
                break;
            case "rec":
                convertRec(t);
                break;
            case "function_form":
                convertFcnForm(t);
                break;
            case "and":
                convertAnd(t);
                break;
            case "@":
                convertAtTheRateOf(t);
                break;
            default:
        }
    }

    void constructAST(String rpalFileName) {
        p = new ASTParser(rpalFileName);
        p.preOrderTraversal();
    }

    void constructST() {

        /*
         * IMP. We will need to invoke getRoot using the instance of the parent
         * class.
         * The below call is going to traverse the tree recursively in a post order
         * fashion and reduce it to a
         * partial standard tree.
         */

        this.postOrder(p.getRoot());
        p.preOrder(p.getRoot(), 0);
        this.generateControlStructures();
    }

    private void generateControlStructures() {

        /*
         * Logic would be to travers through the control elements in a pre-order way
         * when we encounter a lambda switch to a new context.
         */

        stack.add(p.getRoot());

        while (stack.size() != 0) {
            Node elem = stack.get(0);
            stack.remove(0);

            controlStructure = preOrderTraverse(elem);
            this.controlStructureArray.add(controlStructure); // We will have the final control structure (flattened
                                                              // here)
            controlStructure = new ArrayList<>(); // We need to do this to clear out the arrayList

        }

        // Lets pass the generated control structures to the CSE machine class.
        CSEMachine cseMachine = new CSEMachine(controlStructureArray);
        cseMachine.runCSEMachine();
    }

    private ArrayList<String> preOrderTraverse(Node t) {

        switch (t.getToken()) {
            case LAMBDA:
                if (t.getLeftChild().getToken().equals(",")) {
                    Node node = t.getLeftChild().getLeftChild();
                    StringBuffer sb = new StringBuffer();
                    while (node != null) {
                        sb.append(getValueOfToken(node.getToken()) + ",");
                        node = node.getRightChild();
                    }
                    sb.deleteCharAt(sb.length() - 1);
                    controlStructure.add(t.getToken() + "~" + (counter) + "~" + "{" + sb.toString() + "}");
                    counter++;
                } else {
                    controlStructure.add(t.getToken() + "~" + (counter) + "~"
                            + getValueOfToken(t.getLeftChild().getToken()));
                    counter++;

                }
                stack.add(t.getLeftChild().getRightChild());
                if (t.getRightChild() != null) {
                    preOrderTraverse(t.getRightChild());
                }

                return controlStructure;

            case "->":
                StringBuffer deltaThen = new StringBuffer();
                StringBuffer deltaElse = new StringBuffer();
                StringBuffer B = new StringBuffer();

                litePreOrderTraverse(t.getLeftChild().getRightChild().getRightChild(), deltaElse, "");
                controlStructure.add("deltaelse:" + deltaElse);

                // disconnect the else part
                t.getLeftChild().getRightChild().setRightChild(null);

                litePreOrderTraverse(t.getLeftChild().getRightChild(), deltaThen, "then");
                controlStructure.add("deltathen:" + deltaThen);
                // disconnect the then part
                t.getLeftChild().setRightChild(null);

                litePreOrderTraverse(t.getLeftChild(), B, "B");
                controlStructure.add("Beta:" + B);

                if (t.getRightChild() != null) {
                    preOrderTraverse(t.getRightChild());
                }

                return controlStructure;

            case "tau":
                StringBuffer sb = new StringBuffer();
                litePreOrderTraverse(t, sb, "");
                controlStructure.add(sb.toString());
                return controlStructure;
            default:
                controlStructure.add(t.getToken());
                break;
        }
        if (t.getLeftChild() != null) {
            preOrderTraverse(t.getLeftChild());
        }
        if (t.getRightChild() != null) {
            preOrderTraverse(t.getRightChild());
        }

        return controlStructure;
    }

    private StringBuffer litePreOrderTraverse(Node t, StringBuffer sb, String delta) {

        if (t.getToken().equals(LAMBDA)) {
            if (t.getLeftChild().getToken().equals(",")) {
                Node node = t.getLeftChild().getLeftChild();
                StringBuffer sb1 = new StringBuffer();
                while (node != null) {
                    sb1.append(getValueOfToken(node.getToken()) + ",");
                    node = node.getRightChild();
                }
                sb1.deleteCharAt(sb1.length() - 1);
                sb.append(t.getToken() + "~" + (counter) + "~" + "{" + sb1.toString() + "}");
                counter++;

            }

            else {
                sb.append(t.getToken() + "~" + (counter) + "~" + getValueOfToken(t.getLeftChild().getToken())
                        + " ");
                counter++;

            }
            stack.add(t.getLeftChild().getRightChild());
            t.setLeftChild(null); // Prevent the below getLeft child from running.
        } else if (t.getToken().equals("->")) {
            StringBuffer deltaThen = new StringBuffer();
            StringBuffer deltaElse = new StringBuffer();
            StringBuffer B = new StringBuffer();
            StringBuilder result = new StringBuilder();

            litePreOrderTraverse(t.getLeftChild().getRightChild().getRightChild(), deltaElse, "");
            // controlStructure.add ("deltaelse:" + deltaElse);
            result.append("deltaelse:").append(deltaElse);

            // disconnect the else part
            t.getLeftChild().getRightChild().setRightChild(null);

            litePreOrderTraverse(t.getLeftChild().getRightChild(), deltaThen, "then");
            // controlStructure.add ("deltathen:" + deltaThen);
            result.append("deltathen:").append(deltaThen);
            // disconnect the then part
            t.getLeftChild().setRightChild(null);

            litePreOrderTraverse(t.getLeftChild(), B, "B");
            // controlStructure.add("Beta:" + B);
            result.append("Beta:").append(B);

            // add this to the end ?
            sb.append(result);
        }

        else {
            if (t.getToken().equals("tau")) {
                int tempCount = 0;
                Node node = t.getLeftChild();
                if (node != null) {
                    if (node.getToken().equals("tau")) {
                        processTauNode(node);
                    }
                    while (node != null) {
                        tempCount++;
                        node.setToken(node.getToken() + ";"); // a differentiator for the tuple elements.
                        node = node.getRightChild();

                        // check if the internal node is again a tau.
                        if (node != null && node.getToken().equals("tau")) {
                            processTauNode(node);
                        }
                    }
                }
                t.setToken("tau_" + tempCount + ";");
            }
            sb.append(t.getToken()).append(" ");
        }

        if (t.getLeftChild() != null) {
            litePreOrderTraverse(t.getLeftChild(), sb, delta);
        }
        if (t.getRightChild() != null) {
            litePreOrderTraverse(t.getRightChild(), sb, delta);
        }
        return sb;
    }

    private void processTauNode(Node t) {
        if (t.getToken().equals("tau")) {
            int tempCount = 0;
            Node node = t.getLeftChild();
            if (node != null) {
                while (node != null) {
                    tempCount++;
                    node.setToken(node.getToken() + ";"); // a differentiator for the tuple elements.
                    node = node.getRightChild();
                }
            }
            t.setToken("tau_" + tempCount);
        }
    }

    private String getValueOfToken(String token) {
        int beginIndex = token.indexOf(':') + 1;
        if (beginIndex <= 0)
            return token;
        return token.substring(beginIndex, token.length() - 1);
    }
}