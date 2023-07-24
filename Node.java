
/*
 * ABSTRACT SYNTAX TREE
 * 
 * Tree is in FIRST leftChild NEXT rightChild form,
 * leftChild = first leftChild, rightChild = next rightChild
 */

 class Node {

	private String token;
	private Node leftChild;
	private Node rightChild;

    Node(String token) {
        this.token = token;
    }

    Node(Node node) {
        this.token = node.token;
    }

	public void setToken(String token) {
		this.token = token;
	}
	public void setLeftChild(Node leftChild) {
		this.leftChild = leftChild;
	}
	public void setRightChild(Node rightChild) {
		this.rightChild = rightChild;
	}
	public String getToken() {
		return this.token;
	}
	public Node getLeftChild() {
		return this.leftChild;
	}
	public Node getRightChild() {
		return this.rightChild;
	}
}