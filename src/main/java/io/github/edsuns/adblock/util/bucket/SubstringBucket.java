package io.github.edsuns.adblock.util.bucket;

import io.github.edsuns.adblock.util.bloom.BloomFilter;
import io.github.edsuns.adblock.util.bloom.BloomFilterPool;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.function.Function;

/**
 * Created by Edsuns@qq.com on 2021/8/20.
 * <p>
 * Used as the param of {@link java.util.HashMap#get(Object)}.
 * <p>
 * No need to implement multi-thread safe.
 */
public class SubstringBucket<S, F extends Serializable> extends HashBucket implements AutoCloseable {

    private final char[] data;
    private final SubstringGenerator generator;

    @Nullable
    final S extraData;

    private boolean allHashesGenerated = false;

    private final char[][] substrings;
    @Nullable
    private BloomFilter bloomFilter;

    public SubstringBucket(String data) {
        final char[] dataArr = data.toCharArray();
        this.extraData = generateExtraData(data);
        final int substringCount;
        if (dataArr.length >= SUBSTRING_LENGTH) {
            substringCount = dataArr.length - SUBSTRING_LENGTH + 1;
        } else {
            substringCount = 0;
        }
        this.data = dataArr;
        this.generator = createGenerator();
        super.hashes = new int[substringCount][HASH_FUNCTION_COUNT];
        this.substrings = new char[substringCount][SUBSTRING_LENGTH];
    }

    /**
     * Create {@link SubstringGenerator}. Override this method to provide custom generator.
     *
     * @return newly created generator
     */
    protected SubstringGenerator createGenerator() {
        return new FixedSizeSubstringGenerator();
    }

    /**
     * Create extra data from source data.
     *
     * @param source data passed from the constructor
     * @return extra data
     */
    @Nullable
    protected S generateExtraData(String source) {
        return null;
    }

    @Nullable
    public S getExtraData() {
        return extraData;
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

    @Nullable
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

    protected boolean fullyMatches(@Nullable S sExtraData, @Nullable F fExtraData) {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (hashes.length <= 0) {
            return false;
        }
        if (obj instanceof FingerprintBucket) {
            // match with FingerprintBucket including extra data
            @SuppressWarnings("unchecked") final FingerprintBucket<F> fingerprintBucket = (FingerprintBucket<F>) obj;
            return containsHashes(fingerprintBucket) && fullyMatches(this.extraData, fingerprintBucket.extraData);
        }
        return false;
    }

    @Override
    public void close() {
        if (bloomFilter != null) {
            BloomFilterPool.recycle(bloomFilter);
            bloomFilter = null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }
}
