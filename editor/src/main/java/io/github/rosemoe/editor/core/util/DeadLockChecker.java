package io.github.rosemoe.editor.core.util;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import io.github.rosemoe.editor.core.codeanalysis.analyzer.CodeAnalyzer;

public class DeadLockChecker {
    public static long deadLockMs = 10000;
    public static void startChecker(ReentrantLock rl, Object ...errorArgs) {
        if ( Logger.DEBUG == false ) { return; }
        if ( rl == null ) {
            Logger.debug("ReentrantLock is null cannot start Deadlock");
        }
        new Thread() {
            @Override
            public void run() {
                while(true) {
                    try {
                        if (!rl.tryLock(deadLockMs, TimeUnit.MILLISECONDS)) {
                            Logger.debug(errorArgs);
                        } else {
                            rl.unlock();
                            Thread.sleep(deadLockMs);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}
