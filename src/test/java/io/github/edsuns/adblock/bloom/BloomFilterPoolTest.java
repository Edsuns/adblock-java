package io.github.edsuns.adblock.bloom;

import io.github.edsuns.adblock.util.bloom.BloomFilter;
import io.github.edsuns.adblock.util.bloom.BloomFilterPool;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by Edsuns@qq.com on 2021/8/23.
 */
public class BloomFilterPoolTest {
    static final int THREAD_COUNT = BloomFilterPool.CREATED_COUNT_MAX * 2;
    static final int SLEEP_TIME_MS = 20;
    static final ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

    static final int[] ITEM = {12345678, 98765432, -12345678, -98765432, 31, -32};

    static class Consumer implements Runnable {

        private final CountDownLatch countDownLatch;

        public Consumer(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                final BloomFilter bloomFilter = BloomFilterPool.getBloomFilter();
                assertFalse(bloomFilter.contains(ITEM));
                Thread.sleep(SLEEP_TIME_MS);
                bloomFilter.add(ITEM);
                Thread.sleep(SLEEP_TIME_MS);
                assertTrue(bloomFilter.contains(ITEM));
                bloomFilter.clear();
                Thread.sleep(SLEEP_TIME_MS);
                assertFalse(bloomFilter.contains(ITEM));
                BloomFilterPool.recycle(bloomFilter);
                countDownLatch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void concurrency() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.execute(new Consumer(countDownLatch));
        }
        assertTrue(countDownLatch.await(5, TimeUnit.SECONDS));
    }

    @AfterAll
    public static void afterAll() {
        executor.shutdown();
    }
}
