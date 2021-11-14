package io.github.edsuns.adblock.util.bucket;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Edsuns@qq.com on 2021/8/20.
 * <p>
 * Used as the params of {@link java.util.HashMap#put(Object, Object)}.
 * <p>
 * No need to implement multi-thread safe.
 */
public class FingerprintBucket<F extends Serializable> extends HashBucket {

    public static class NoFingerprintException extends Exception {
        public NoFingerprintException() {
            super("No fingerprint found!");
        }
    }

    @Nullable
    final F extraData;

    public FingerprintBucket(String data) throws NoFingerprintException {
        this.extraData = generateExtraData(data);
        generateHashes(data.toCharArray(), createGenerator());
        mainHashIndex = 0;// only need to put the leading substring hash in HashBucketFilter
    }

    private void generateHashes(char[] data, SubstringGenerator generator) throws NoFingerprintException {
        List<int[]> hashList = new ArrayList<>();
        char[] substring = new char[SUBSTRING_LENGTH];
        while (generator.next(data, substring)) {
            hashList.add(calcHashes(substring));
        }
        if (hashList.isEmpty()) {
            throw new NoFingerprintException();
        }
        super.hashes = hashList.toArray(new int[0][0]);
    }

    /**
     * Create {@link FingerprintGenerator}. Override this method to provide custom generator.
     *
     * @return newly created generator
     */
    protected FingerprintGenerator createGenerator() {
        return new FingerprintGenerator();
    }

    /**
     * Create extra data from source data.
     *
     * @param source data passed from the constructor
     * @return extra data
     */
    @Nullable
    protected F generateExtraData(String source) {
        return null;
    }

    @Nullable
    public F getExtraData() {
        return extraData;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof FingerprintBucket) {
            @SuppressWarnings("unchecked") final FingerprintBucket<F> fingerprintBucket = (FingerprintBucket<F>) obj;
            return Arrays.deepEquals(hashes, fingerprintBucket.hashes);
        }
        // The comparison between FingerprintBucket and SubstringBucket
        // is required to be performed at SubstringBucket.
        // And HashBucketFilter.matches() will call equals() of SubstringBucket indirectly.
        throw new RuntimeException("Calling `equals()` is not allowed!");
    }
}
