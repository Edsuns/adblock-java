package io.github.edsuns.adblock.util.bucket;

import javax.annotation.Nullable;
import java.util.HashMap;

/**
 * Created by Edsuns@qq.com on 2021/8/20.
 */
public class HashBucketFilter {

    @Nullable
    private static ExtraDataMatcher EXTRA_DATA_MATCHER;

    /**
     * Used by {@link HashBucketFilter#matchExtraData(FingerprintBucket.ExtraData, SubstringBucket.ExtraData)}.
     *
     * @param extraDataMatcher null to disable extra data match
     */
    public static void setExtraDataMatcher(@Nullable ExtraDataMatcher extraDataMatcher) {
        HashBucketFilter.EXTRA_DATA_MATCHER = extraDataMatcher;
    }

    /**
     * Used by {@link SubstringBucket#equals(Object)}.
     *
     * @return true if {@link HashBucketFilter#EXTRA_DATA_MATCHER} is null, or match the params and return
     * @see ExtraDataMatcher#matches(FingerprintBucket.ExtraData, SubstringBucket.ExtraData)
     */
    static boolean matchExtraData(FingerprintBucket.ExtraData fExtraData, SubstringBucket.ExtraData sExtraData) {
        return EXTRA_DATA_MATCHER == null || EXTRA_DATA_MATCHER.matches(fExtraData, sExtraData);
    }

    private final HashMap<HashBucket, FingerprintBucket> container;

    public HashBucketFilter() {
        this(20000);
    }

    public HashBucketFilter(int initialCapacity) {
        container = new HashMap<>(initialCapacity);
    }

    /**
     * Add {@link FingerprintBucket}.
     *
     * @param fingerprints fingerprints
     */
    public void add(FingerprintBucket fingerprints) {
        container.put(fingerprints, fingerprints);
    }

    /**
     * Find matched {@link FingerprintBucket} for {@link SubstringBucket}.
     *
     * @param substringBucket target
     * @return matched {@link FingerprintBucket}, or null if no matched
     */
    @Nullable
    public FingerprintBucket matches(SubstringBucket substringBucket) {
        return substringBucket.anyNotNullOf(container::get);
    }
}
