package com.neophite.lab1.task2;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class SkipList<T extends Comparable<T>> {

    private int curMaxHeight;
    private Node<T> upperHeadNode;
    private static int MAX_HEIGHT = 24;

    public SkipList() {
        curMaxHeight = MAX_HEIGHT;
        Node<T> initialNode = new Node(null, MAX_HEIGHT);
        upperHeadNode = initialNode;
        initHeadNode(upperHeadNode);
    }


    private int compareNodesDatas(T data1 , T data2){
        return data1.compareTo(data2) ;
    }


    private void initHeadNode(Node<T> initialNode){
        for (int i = 1; i < curMaxHeight; i++) {
            Node<T> nextNode = new Node(null, curMaxHeight);
            initialNode.setLowerNode(null, nextNode);
            initialNode = nextNode;
        }
    }

    private Node<T> getHeadBottomNode() {
        return returnHeadNodeForLevel(0);
    }

    private Node<T> returnHeadNodeForLevel(int index) {
        Node<T> head = upperHeadNode;
        for (int i = curMaxHeight - 1; i > index; i--) {
            head = head.getLowerNode();
        }
        return head;
    }

    private int getTowerHeight() {
        int nodeTowerLevel = 1;
        while (nodeTowerLevel < curMaxHeight && Math.abs( new Random().nextInt()) % 2 == 0) {
            nodeTowerLevel++;
        }
        return nodeTowerLevel;
    }

    public void add(T elem) {
        int randomHeight = getTowerHeight();
        Node<T> previousNode = null;
        Node<T> headCurrentLevelNode = returnHeadNodeForLevel(randomHeight - 1);

        while (headCurrentLevelNode != null) {
            Node<T> headRightTowerNode = headCurrentLevelNode.getRightNode();
            if (headRightTowerNode != null && compareNodesDatas(headRightTowerNode.getValue(),elem) == -1){
                headCurrentLevelNode = headRightTowerNode;
            }
            else {
                Node<T> newTowerNode = new Node(elem, randomHeight);
                newTowerNode.setRightNode(headRightTowerNode);
                if (headCurrentLevelNode.compareAndSetRightNode(headRightTowerNode, newTowerNode)) {
                    headCurrentLevelNode = headCurrentLevelNode.getLowerNode();
                    if (previousNode != null) {
                        previousNode.setLowerNode(newTowerNode);
                    }

                    previousNode = newTowerNode;
                }
            }
        }
    }

    public void remove(T elem) {
        Node<T> headBottomNode = getHeadBottomNode();

        while (headBottomNode != null) {
            Node<T> rightTowerNode = headBottomNode.getRightNode();
            if (rightTowerNode != null && rightTowerNode.getValue().compareTo(elem) == -1) {
                headBottomNode = rightTowerNode;
            } else if (rightTowerNode != null && rightTowerNode.getValue().compareTo(elem) == 0) {
                rightTowerNode.setToBeDeleted(true);
                if (headBottomNode.compareAndSetRightNode(rightTowerNode, rightTowerNode.getRightNode())) {
                    headBottomNode = headBottomNode.getLowerNode();
                } else {
                    rightTowerNode.setToBeDeleted(false);
                }
            } else {
                headBottomNode = headBottomNode.getLowerNode();
            }
        }
    }


    private static class Node<T> {
        private AtomicReference<Node<T>> rightNode;
        private AtomicReference<Node<T>> lowerNode;
        private T value;
        private boolean toBeDeleted;

        public Node(T value, int height) {
            this.value = value;
            this.rightNode = new AtomicReference<>(null);
            this.lowerNode = new AtomicReference<>(null);
            this.toBeDeleted = false;
        }

        public T getValue() {
            return value;
        }


        public Node<T> getRightNode() {
            return rightNode.get();
        }

        public void setRightNode(Node<T> rightNode) {
            this.rightNode.set(rightNode);
        }

        public Node<T> getLowerNode() {
            return lowerNode.get();
        }

        public void setLowerNode(Node<T> lowerNode) {
            this.lowerNode.set(lowerNode);
        }

        public void setToBeDeleted(boolean toBeDeleted) {
            this.toBeDeleted = toBeDeleted;
        }

        public boolean compareAndSetRightNode(Node<T> expected, Node<T> next) {
            return !toBeDeleted && this.rightNode.compareAndSet(expected, next);
        }

        public boolean setLowerNode(Node<T> expected, Node<T> next) {
            return this.lowerNode.compareAndSet(expected, next);
        }
    }

}