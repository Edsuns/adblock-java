package io.github.edsuns.util.bloom;

import java.util.LinkedList;

/**
 * Created by Edsuns@qq.com on 2021/8/21.
 */
public final class BloomFilterPool {

    public static final int FILTER_SIZE = 1024 * 512;
    public static final int CREATED_COUNT_MAX = 8;

    private static final LinkedList<BloomFilter> POOL = new LinkedList<>();
    private static volatile boolean EMPTY = true;
    private static volatile int CREATED_COUNT = 0;

    private BloomFilterPool() {
        // private
    }

    public static BloomFilter getBloomFilter() {
        synchronized (POOL) {
            boolean interrupted = false;
            try {
                final BloomFilter filter;
                if (EMPTY && CREATED_COUNT < CREATED_COUNT_MAX) {
                    filter = new BloomFilter(FILTER_SIZE);
                    CREATED_COUNT++;
                } else {
                    while (EMPTY) {
                        try {
                            POOL.wait();
                        } catch (InterruptedException e) {
                            interrupted = true;
                        }
                    }
                    filter = POOL.pop();
                    EMPTY = POOL.isEmpty();
                }
                return filter;
            } finally {
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public static void recycle(BloomFilter bloomFilter) {
        bloomFilter.clear();
        synchronized (POOL) {
            POOL.push(bloomFilter);
            EMPTY = POOL.isEmpty();
            POOL.notify();
        }
    }
}
