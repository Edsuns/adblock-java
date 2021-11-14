package io.github.edsuns.adblock.util.bucket;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Edsuns@qq.com on 2021/8/20.
 */
public class HashBucketFilter<S, F extends Serializable> {

    private final HashMap<HashBucket, FingerprintBucket<F>> container;

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
    public void add(FingerprintBucket<F> fingerprints) {
        container.put(fingerprints, fingerprints);
    }

    /**
     * Find matched {@link FingerprintBucket} for {@link SubstringBucket}.
     *
     * @param substringBucket target
     * @return matched {@link FingerprintBucket}, or null if no matched
     */
    @Nullable
    public FingerprintBucket<F> matches(SubstringBucket<S, F> substringBucket) {
        return substringBucket.anyNotNullOf(container::get);
    }
}
