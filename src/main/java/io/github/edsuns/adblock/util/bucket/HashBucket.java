package io.github.edsuns.adblock.util.bucket;

import io.github.edsuns.adblock.util.hash.RabinFingerprint;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by Edsuns@qq.com on 2021/8/20.
 * <p>
 * No need to implement multi-thread safe.
 */
public abstract class HashBucket {

    static final int SUBSTRING_LENGTH = 6;

    private static final RabinFingerprint[] hashFunctions = {
            RabinFingerprint.p31,
            RabinFingerprint.p13, RabinFingerprint.p17,
            RabinFingerprint.p41, RabinFingerprint.p53
    };
    static final int HASH_FUNCTION_COUNT = hashFunctions.length;
    static final int PRIMARY_INDEX = 0;

    protected int[][] hashes;
    private int mainHashIndex = -1;

    protected HashBucket() {
    }

    protected void onMainHashChanged() {
        // do nothing
    }

    void forEach(Consumer<HashBucket> consumer) {
        for (mainHashIndex = 0; mainHashIndex < hashes.length; mainHashIndex++) {
            onMainHashChanged();
            consumer.accept(this);
        }
    }

    <T> T anyNotNullOf(Function<HashBucket, T> consumer) {
        T bucket;
        for (mainHashIndex = 0; mainHashIndex < hashes.length; mainHashIndex++) {
            onMainHashChanged();
            if ((bucket = consumer.apply(this)) != null) {
                return bucket;
            }
        }
        return null;
    }

    protected int getMainHashIndex() {
        return mainHashIndex;
    }

    @Override
    public int hashCode() {
        if (mainHashIndex < 0 || hashes.length <= 0) {
            return 0;
        }
        return hashes[mainHashIndex][PRIMARY_INDEX];
    }

    protected static int[] calcHashes(char[] substring) {
        int[] hashes = new int[HASH_FUNCTION_COUNT];
        for (int i = 0; i < hashes.length; i++) {
            hashes[i] = hashFunctions[i].hash(substring, substring.length);
        }
        return hashes;
    }

    protected static int[] calcHashes(char[] str, int start, int[] lastHashes) {
        int[] hashes = new int[HASH_FUNCTION_COUNT];
        for (int i = 0; i < hashes.length; i++) {
            hashes[i] = hashFunctions[i].hash(str, start, SUBSTRING_LENGTH, lastHashes[i]);
        }
        return hashes;
    }
}
