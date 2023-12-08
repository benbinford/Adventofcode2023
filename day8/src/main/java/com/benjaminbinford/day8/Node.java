package com.benjaminbinford.day8;

import java.util.Objects;

public class Node {

    private String label;
    private Node left;
    private Node right;

    private boolean simultaneousSuccess;

    public Node(String label) {
        this.label = label;
        this.simultaneousSuccess = label.endsWith("Z");
    }

    public String getLabel() {
        return label;
    }

    public boolean isSimultaneousSuccess() {
        return simultaneousSuccess;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Node node = (Node) o;
        return Objects.equals(label, node.label);
    }

    @Override
    public int hashCode() {
        return label.hashCode();
    }
}
