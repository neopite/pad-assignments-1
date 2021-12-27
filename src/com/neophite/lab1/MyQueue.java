package com.neophite.lab1;

import com.neophite.lab1.task3.MichaelAndScottQueue;

public interface MyQueue<T> {
    void add(T elem);
    MichaelAndScottQueue<T>.Node<T> remove();
}
