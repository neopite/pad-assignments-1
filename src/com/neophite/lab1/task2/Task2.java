package com.neophite.lab1.task2;

public class Task2 implements Runnable {
    private SkipList<Integer> skipList;

    public Task2(SkipList<Integer> list) {
        this.skipList = list;
    }

    public static void main(String[] args) {
        SkipList<Integer> list = new SkipList<>();

        var runnable1 = new Thread(new Task2(list));
        var runnable2 = new Thread(new Task2(list));
        var runnable3 = new Thread(new Task2(list));
        var runnable4 = new Thread(new Task2(list));

        runnable1.start();
        runnable2.start();
        runnable3.start();
        runnable4.start();

        try {
            runnable1.join();
            runnable2.join();
            runnable3.join();
            runnable4.join();
        } catch (InterruptedException err) {
            System.err.println(err);
        }
    }

    public void run() {
        skipList.add(13);
        skipList.add(234);
        skipList.add(124);
        skipList.add(65);
        skipList.add(24);
    }
}