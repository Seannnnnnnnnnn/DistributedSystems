package com.distributedsystems;

public class FibonnaciNumber implements Runnable {
    private int N;

    public FibonnaciNumber(int N) { this.N = N;}

    private int getFibbonacci(int n) {
        /* the highly inefficient O(2^n) fib algorithm that we can speed up with multi threading */
        assert n >= 0;
        if (n == 1 || n==0) {
            return 1;
        } else {
            return getFibbonacci(n-1) + getFibbonacci(n-2);
        }
    }

    @Override
    public void run() {
        System.out.printf("%d fibonacci number : %d\n", N, getFibbonacci(N));
    }
}
