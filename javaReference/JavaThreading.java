/*
What is a thread?
A thread is a single sequential flow of control within a program. When using
multi-threading, it is about the use of multiple threads at the same time and
performing different tasks in a single program.

The primary difference between a thread in a process is shared memory. Threads
can access the same Java objects and if we don't coordinate access, things can
go very wrong.
 */

package com.distributedsystems;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Threading {
    public static void main(String[] args) {
        myFirstThread();
        System.out.println("\n=========-0-=========\n");
        mySecondThread();
        System.out.println("\n=========-0-=========\n");
        myThirdThread();
    }


    static void myFirstThread() {
        class Multiplier extends Thread {
            private int val;

            public Multiplier(int val) {
                this.val = val;
            }

            @Override
            public void run() {
                this.val *= 3;
            }

            public void getResult() {
                System.out.format("value: %d\n", this.val);
            }
        }

        // lets go ahead and spawn some threads. We'll create a list of threads
        List<Multiplier> multipliers = new ArrayList<>();

        for (int i=0; i<10; i++){
            Multiplier multiplier = new Multiplier(i);
            multipliers.add(multiplier);
        }

        // calling .start() is necessary to run any class that extends the Thread class confusing, but start()
        // calls the overridden run() method.
        for (Multiplier multiplier: multipliers) {
            multiplier.start();
        }

        for (Multiplier multiplier: multipliers) {
            try {
                multiplier.join();     // wait for thread to reach the TERMINATED STATE
            } catch (InterruptedException e) {
                return;
            }
        }

        for (Multiplier multiplier: multipliers) {
            multiplier.getResult();
        }
    }


    // Now onto the second part of the multi - threading tutorial
    static void mySecondThread() {
        class Threads2 {
            void showInterface() {
                /*
                 Let's start two threads that both execute the Counter class below. We'll start them
                 around the same time, then join() them to wait for them to finish. Counter will increment
                 and decrement c many times.
                 Just to complicate things though, instead of using a class that extends Thread we're using
                 a class that implements Runnable.
                 */
                Counter counter = new Counter(this);
                Thread c1, c2;

                c1 = new Thread(counter);
                c2 = new Thread(counter);
                c1.start();
                c2.start();
                try {
                    c1.join();
                    c2.join();
                } catch (InterruptedException e){
                    return;
                }
                // now, what is the final output?
                System.out.format("Final value is %d wtf?!\n", this.c);

                /*
                What is happening is there is uncoordinated memory access, resulting in each thread stepping
                on each others toes.

                When we apply the 'synchronized' keyword to a method in Java, we enforce that only one thread can
                execute a synchronized method at any given time. In this case, ThreadA can call increment, though
                threadB cannot call decrement until ThreadA has concluded execution.

                Rule of thumb is to use synchronized on any method that manipulates a shared object when multi threading

                */
            }


            private volatile int c = 0;
            public void increment() {c++;}
            public void decrement() {c--;}

            class Counter implements Runnable {
                private final Threads2 parent;

                public Counter(Threads2 parent) {
                    this.parent = parent;
                }

                @Override
                public void run() {
                    for (int i=0; i<=10000; i++) {
                        this.parent.increment();
                        this.parent.decrement();
                    }
                }
            }
        }
        Threads2 threads2 = new Threads2();
        threads2.showInterface();
    }

    static void myThirdThread(){
        class Thread3 {
            public void threadPool() {
                /*
                It turns our creating a new thread every time you want to do something - such as perform computation or
                accept a connection from a client to a server, can be quite inefficient.

                Reason being, there is a quite a bit of overhead when creating and destroying a thread. A threadPool
                takes care of creating all our threads, and then wait until we assign them work. This does result in us
                limiting the number of threads we can use however - eg// what if 100,000 people arrive at once to our
                server
                */


                // let's go ahead and create a thread pool!
                // the below code creates n threads that sit idle until assigned work
                int n = 5;
                ExecutorService pool = Executors.newFixedThreadPool(n);

                Runnable add3, add5;
                add3 = new Adder(this, 3);
                add5 = new Adder(this, 5);

                for (int i=0; i<100; i++) {
                    pool.execute(add3);
                    pool.execute(add5);
                }

                // ask pool to stop accepting new jobs and finish up current jobs in its queue.
                pool.shutdown();

                try {
                    /*
                     awaitTermination() will return true only once all the jobs have finished. Note
                     the timeout value, and consider what an appropriate value is.
                     */
                    while (!pool.awaitTermination(1, TimeUnit.SECONDS)) {
                        System.out.println("Waiting for jobs to finish...");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.format("Final value is %d\n", this.c);
            }

            // volatile keyword alerts java that variable is being accessed by multiple threads, so always read from
            // memory instead of Cache. Otherwise there is slight chance that cached value will be different from memory
            private volatile int c = 0;
            public synchronized void increment() {c++;}

            static class Adder implements Runnable {
                private final Thread3 parent;
                private final int constant;

                public Adder(Thread3 parent, int constant) {
                    this.parent = parent;
                    this.constant = constant;
                }

                @Override
                public void run() {
                    for (int i=0; i<=constant; i++) {
                        parent.increment();
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                }
            }
        }
        new Thread3().threadPool();
    }
}
