package io.github.edsuns.adblock.util.bucket;

import java.util.HashMap;

/**
 * Created by Edsuns@qq.com on 2021/8/20.
 */
public class HashBucketFilter<T> {

    private final HashMap<HashBucket, T> container;

    public HashBucketFilter() {
        this(20000);
    }

    public HashBucketFilter(int initialCapacity) {
        container = new HashMap<>(initialCapacity);
    }

    /**
     * Add {@link FingerprintBucket} with relative data.
     *
     * @param fingerprints fingerprints
     * @param relative     data related to fingerprints
     */
    public void put(FingerprintBucket fingerprints, T relative) {
        container.put(fingerprints, relative);
    }

    /**
     * Add {@link FingerprintBucket} and use itself as the relative data.
     *
     * @see HashBucketFilter#put(FingerprintBucket, Object)
     */
    public void add(T fingerprints) {
        put((FingerprintBucket) fingerprints, fingerprints);
    }

    /**
     * Find matched relative data {@link T} for {@link SubstringBucket}.
     *
     * @param substringBucket target
     * @return matched {@link T}, or null if no matched
     */
    public T matches(SubstringBucket substringBucket) {
        return substringBucket.anyNotNullOf(container::get);
    }
}
