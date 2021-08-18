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

public class Threading {
    public static void main(String[] args) {
        myFirstThread();
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
}
