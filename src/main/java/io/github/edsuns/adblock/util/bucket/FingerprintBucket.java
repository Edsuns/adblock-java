package io.github.edsuns.adblock.util.bucket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Edsuns@qq.com on 2021/8/20.
 * <p>
 * No need to implement multi-thread safe.
 */
public class FingerprintBucket extends HashBucket {

    public static class NoFingerprintException extends Exception {
        public NoFingerprintException() {
            super("No fingerprint found!");
        }
    }

    public FingerprintBucket(String data) throws NoFingerprintException {
        generateHashes(data.toCharArray(), new FingerprintGenerator());
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
        // And HashBucketFilter will call equals() of SubstringBucket.
        throw new RuntimeException("Calling `equals()` is not allowed!");
    }
}
