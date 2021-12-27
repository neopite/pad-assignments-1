package com.neophite.lab1.task1;

import com.neophite.lab1.Util;

import java.util.ConcurrentModificationException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class Mutex {
    AtomicReference<Thread> lockedThread ;
    private Set<Thread> threads;

    public Mutex(Set<Thread> threadsQueue) {
        this.threads = threadsQueue;
        this.lockedThread = new AtomicReference<>();

    }

    public void lock(){
        var currentThreadOnMutex = Thread.currentThread();
        while (!lockedThread.compareAndSet(null, currentThreadOnMutex)) {
            Thread.yield();
        }
    }

    public void unlock(){
        lockedThread.set(null);
    }

    public void _wait(){
        var currThread = Thread.currentThread();
        if(lockedThread.get()!=currThread){
            throw new ConcurrentModificationException();
        }
        threads.add(currThread);
        unlock();
        while (threads.contains(currThread)){
            Thread.yield();
        }
        lock();

    }

    public void _notify(){
        var randomWaitingThread = Util.getRandomSetElement(threads);
        threads.remove(randomWaitingThread);
    }

    public void _notifyAll(){
        threads.clear();
    }



}
