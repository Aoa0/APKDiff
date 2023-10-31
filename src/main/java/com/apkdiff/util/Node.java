package com.apkdiff.util;

public class Node<T> {
    private T element;
    private Node<T> father, firstChild, nextSibling;

    public Node() {
        this(null, null, null, null);
    }

    public Node(T element, Node<T> father, Node<T> firstChild, Node<T> nextSibling) {
        this.element = element;
        this.father = father;
        this.firstChild = firstChild;
        this.nextSibling = nextSibling;
    }

    public void setFather(Node<T> father) {
        this.father = father;
    }

    public void setFirstChild(Node<T> firstChild) {
        this.firstChild = firstChild;
    }

    public void setNextSibling(Node<T> nextSibling) {
        this.nextSibling = nextSibling;
    }

    public T getElement() {
        return element;
    }

    public Node<T> getFirstChild() {
        return firstChild;
    }

    public Node<T> getFather() {
        return father;
    }

    public Node<T> getNextSibling() {
        return nextSibling;
    }
}
