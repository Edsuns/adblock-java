package io.github.edsuns.adblock.util.bucket;

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
public class FingerprintBucket extends HashBucket {

    public interface ExtraData {
    }

    public static class NoFingerprintException extends Exception {
        public NoFingerprintException() {
            super("No fingerprint found!");
        }
    }

    final ExtraData extraData;

    public FingerprintBucket(String data) throws NoFingerprintException {
        this(data.toCharArray(), new ExtraData() {
        });
    }

    public FingerprintBucket(String data, ExtraData extraData) throws NoFingerprintException {
        this(data.toCharArray(), extraData);
    }

    public FingerprintBucket(char[] data, ExtraData extraData) throws NoFingerprintException {
        this.extraData = extraData;
        generateHashes(data, new FingerprintGenerator());
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

    public ExtraData getExtraData() {
        return extraData;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof FingerprintBucket) {
            return Arrays.deepEquals(hashes, ((FingerprintBucket) obj).hashes);
        }
        // The comparison between FingerprintBucket and SubstringBucket
        // is required to be performed at SubstringBucket.
        // And HashBucketFilter.matches() will call equals() of SubstringBucket indirectly.
        throw new RuntimeException("Calling `equals()` is not allowed!");
    }
}
