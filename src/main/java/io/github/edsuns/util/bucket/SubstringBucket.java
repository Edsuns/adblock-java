package io.github.edsuns.util.bucket;

import io.github.edsuns.util.bloom.BloomFilter;
import io.github.edsuns.util.bloom.BloomFilterPool;

import javax.annotation.Nullable;

/**
 * Created by Edsuns@qq.com on 2021/8/20.
 * <p>
 * No need to implement multi-thread safe.
 */
public class SubstringBucket extends HashBucket implements AutoCloseable {

    private final char[] data;
    private final SubstringGenerator generator;

    private boolean allHashesGenerated = false;

    private final char[][] substrings;
    @Nullable
    private BloomFilter bloomFilter;

    public SubstringBucket(String data) {
        final int substringCount;
        if (data.length() >= SUBSTRING_LENGTH) {
            substringCount = data.length() - SUBSTRING_LENGTH + 1;
        } else {
            substringCount = 0;
        }
        this.data = data.toCharArray();
        this.generator = new FixedSIzeSubstringGenerator();
        super.hashes = new int[substringCount][HASH_FUNCTION_COUNT];
        this.substrings = new char[substringCount][SUBSTRING_LENGTH];
    }

    private void ensureHashes() {
        if (allHashesGenerated || hashes.length <= 0) {
            return;
        }
        for (int i = getMainHashIndex() + 1; i < hashes.length; i++) {
            if (i == 0) {
                hashes[i] = calcHashes(substrings[i]);
            } else {
                hashes[i] = calcHashes(data, i, hashes[i - 1]);
            }
        }
        allHashesGenerated = true;
    }

    private void ensureMainHashes() {
        int mainIndex = getMainHashIndex();
        if (allHashesGenerated || hashes.length <= 0) {
            return;
        }
        char[] substring = new char[SUBSTRING_LENGTH];
        if (!generator.next(data, substring)) {
            throw new RuntimeException("Failed to generate substring!");
        }
        substrings[mainIndex] = substring;
        if (mainIndex > 0) {
            int[] lastHash = hashes[mainIndex - 1];
            hashes[mainIndex] = calcHashes(data, mainIndex, lastHash);
        } else {
            hashes[mainIndex] = calcHashes(substring);
        }
    }

    private BloomFilter getBloomFilter() {
        if (bloomFilter == null) {
            bloomFilter = BloomFilterPool.getBloomFilter();
            bloomFilter.add(hashes);
        }
        return bloomFilter;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }

    @Override
    protected void onMainHashChanged() {
        ensureMainHashes();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (hashes.length <= 0) {
            return false;
        }
        if (obj instanceof HashBucket) {
            return containsHashes((HashBucket) obj);
        }
        return false;
    }

    public boolean containsHashes(HashBucket bucket) {
        ensureHashes();
        final BloomFilter filter = getBloomFilter();
        return filter.contains(bucket.hashes);
    }

    @Override
    public void close() {
        if (bloomFilter != null) {
            BloomFilterPool.recycle(bloomFilter);
        }
    }
}
