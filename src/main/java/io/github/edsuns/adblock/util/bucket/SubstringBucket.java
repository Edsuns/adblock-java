package io.github.edsuns.adblock.util.bucket;

import io.github.edsuns.adblock.util.bloom.BloomFilter;
import io.github.edsuns.adblock.util.bloom.BloomFilterPool;

import javax.annotation.Nullable;
import java.util.function.Function;

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
        this(data.toCharArray());
    }

    public SubstringBucket(char[] data) {
        final int substringCount;
        if (data.length >= SUBSTRING_LENGTH) {
            substringCount = data.length - SUBSTRING_LENGTH + 1;
        } else {
            substringCount = 0;
        }
        this.data = data;
        this.generator = new FixedSizeSubstringGenerator();
        super.hashes = new int[substringCount][HASH_FUNCTION_COUNT];
        this.substrings = new char[substringCount][SUBSTRING_LENGTH];
    }

    private void ensureHashes() {
        if (allHashesGenerated || hashes.length <= 0) {
            return;
        }
        for (int i = mainHashIndex + 1; i < hashes.length; i++) {
            if (i == 0) {
                hashes[i] = calcHashes(substrings[i]);
            } else {
                hashes[i] = calcHashes(data, i, hashes[i - 1]);
            }
        }
        allHashesGenerated = true;
    }

    private void ensureMainHashes() {
        int mainIndex = mainHashIndex;
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

    <T> T anyNotNullOf(Function<HashBucket, T> consumer) {
        T bucket;
        for (mainHashIndex = 0; mainHashIndex < hashes.length; mainHashIndex++) {
            ensureMainHashes();
            if ((bucket = consumer.apply(this)) != null) {
                return bucket;
            }
        }
        return null;
    }

    private BloomFilter getBloomFilter() {
        if (bloomFilter == null) {
            bloomFilter = BloomFilterPool.getBloomFilter();
            bloomFilter.add(hashes);
        }
        return bloomFilter;
    }

    private boolean containsHashes(HashBucket bucket) {
        ensureHashes();
        final BloomFilter filter = getBloomFilter();
        return filter.contains(bucket.hashes);
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

    @Override
    public void close() {
        if (bloomFilter != null) {
            BloomFilterPool.recycle(bloomFilter);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }
}
