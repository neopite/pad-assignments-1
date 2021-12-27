package com.neophite.lab1.task3;

import com.neophite.lab1.MyQueue;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

public class MichaelAndScottQueue<T> implements MyQueue<T> {

    private AtomicReference<Node<T>> tail = new AtomicReference<>(new Node<>(null, new AtomicReference<>(null)));
    private AtomicReference<Node<T>> head = new AtomicReference<>(new Node<>(null, new AtomicReference<>(null)));

    @Override
    public void add(T data) {
        Node<T> newTail = new Node<>(data, new AtomicReference<>(null));
        while(true) {
            Node<T> currentTail = tail.get();
            if (currentTail.next.compareAndSet(null, newTail)) {
                tail.compareAndSet(currentTail, newTail);
                return;
            } else {
                tail.compareAndSet(currentTail, currentTail.next.get());
            }
        }
    }

    @Override
    public Node<T> remove() {
        while (true) {
            Node<T> currentHead = head.get();
            Node<T> currentTail = tail.get();
            Node<T> nextForHead = currentHead.next.get();
            if (currentHead == currentTail) {
                if (nextForHead == null) {
                    throw new NoSuchElementException();
                } else {
                    tail.compareAndSet(currentTail, currentTail.next.get());
                }
            } else {
                if (head.compareAndSet(currentHead, nextForHead)) {
                    return nextForHead;
                }
            }
        }
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        Node<T> current = getHead();
        while (current != null) {
            sb.append(current.data).append("\n");
            current = current.next.get();
        }
        return sb.toString();
    }

    private Node<T> getHead(){
        return head.get();
    }

    public class Node<T> {

        public T data;
        public AtomicReference<Node<T>> next;

        public Node(T data, AtomicReference<Node<T>> next) {
            this.data = data;
            this.next = next;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public AtomicReference<Node<T>> getNext() {
            return next;
        }

        public void setNext(AtomicReference<Node<T>> next) {
            this.next = next;
        }
    }
}