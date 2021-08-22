package io.github.edsuns.util.bloom;

import java.util.LinkedList;

/**
 * Created by Edsuns@qq.com on 2021/8/21.
 */
public final class BloomFilterPool {

    // TODO: Implement BloomFilterPool
    private static final LinkedList<BloomFilter> pool = new LinkedList<>();

    static {
        for (int i = 0; i < 6; i++) {
            pool.push(new BloomFilter(1024 * 2048));
        }
    }

    private BloomFilterPool() {
    }

    public static synchronized BloomFilter getBloomFilter() {
        return pool.pop();
    }

    public static synchronized void onReturnToPool(BloomFilter bloomFilter) {
        bloomFilter.clear();
        pool.push(bloomFilter);
    }
}
