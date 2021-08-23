package com.distributedsystems;

public class Main {
    public static void main(String[] args) {
        int n = 40;
        ThreadPool threadPool = new ThreadPool(6);
        for (int i = 0; i < n; i++) {
            threadPool.execute(new FibonnaciNumber(i));
        }
    }
}
