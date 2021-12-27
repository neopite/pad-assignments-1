package com.neophite.lab1.task4;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class HarrisLinkedListLockFree<T> {
    private volatile AtomicReference<Node<T>> head = new AtomicReference<>(null);
    private int count;

    public void add(T elem) {
        var newNode = new Node<>(elem);
        while (true) {
            if (head.get() == null) {
                if (head.compareAndSet(null, newNode)) {
                    count++;
                    break;
                }
            } else {
                var curHead = head.get();
                var next = curHead.getNext();
                newNode.setNext(next);
                if (!curHead.isLogicallyDeleted.get() && curHead.getNext().compareAndSet(next.get(), newNode)) {
                    count++;
                    break;
                }
            }
        }
    }

    public boolean removeAt(int index) {
        if (index > count || index < 0) {
            return false;
        }
        Node<T> nodeAtIndex;
        while (true) {
            nodeAtIndex = indexAt(index);
            nodeAtIndex.setIsLogicallyDeleted(true);
            if (index != 0) {
                var prevNode = indexAt(index - 1);
                var nextNode = nodeAtIndex.getNext();
                if (!prevNode.isLogicallyDeleted.get() && prevNode.next.compareAndSet(nodeAtIndex, nextNode.get())) {
                    count--;
                    break;
                }
            } else {
                if (!head.get().isLogicallyDeleted.get() && head.compareAndSet(nodeAtIndex, nodeAtIndex.getNext().get())) {
                    count--;
                    break;
                }
            }
            nodeAtIndex.setIsLogicallyDeleted(false);
        }
        return true;
    }

    private Node<T> indexAt(int index) {
        Node<T> curHead = head.get();
        int atIndex = 0;
        while (atIndex < index && curHead != null) {
            curHead = curHead.getNext().get();
            atIndex++;
        }
        if (atIndex == index) {
            return curHead;
        } else return null;
    }


    private static class Node<T> {
        private T data;
        private AtomicReference<Node<T>> next;
        private AtomicReference<Boolean> isLogicallyDeleted;

        public Node(T data, AtomicReference<Node<T>> next) {
            this.data = data;
            this.next = next;
        }

        public Node(T data) {
            this.data = data;
        }

        public boolean canNodeBeDeleted() {
            return isLogicallyDeleted.get();
        }

        public AtomicReference<Node<T>> getNext() {
            return next;
        }

        public AtomicReference<Boolean> getIsLogicallyDeleted() {
            return isLogicallyDeleted;
        }

        public void setIsLogicallyDeleted(boolean isLogicallyDeleted) {
            this.isLogicallyDeleted.set(isLogicallyDeleted);
        }

        public void setNext(AtomicReference<Node<T>> next) {
            this.next = next;
        }
    }

    public String traverse() {
        Node<T> currNode = head.get();
        List<String> out = new LinkedList<>();

        while (currNode != null) {
            out.add(currNode.getNext().toString());
            currNode = currNode.getNext().get();
        }

        return out.stream().collect(Collectors.joining(","));
    }

}
