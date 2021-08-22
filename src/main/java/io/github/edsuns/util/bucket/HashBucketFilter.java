package io.github.edsuns.util.bucket;

import java.util.HashSet;

/**
 * Created by Edsuns@qq.com on 2021/8/20.
 */
public class HashBucketFilter extends HashSet<HashBucket> {

    @Override
    public boolean add(HashBucket hashBucket) {
        if (!(hashBucket instanceof FingerprintBucket)) {
            throw new IllegalArgumentException("Element to add must be FingerprintBucket!");
        }
        if (!hashBucket.all(super::add)) {
            throw new RuntimeException("Failed to add a HashBucket!");
        }
        return true;
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof SubstringBucket) {
            return contains((SubstringBucket) o);
        }
        throw new IllegalArgumentException("Element to query must be SubstringBucket!");
    }

    public boolean contains(SubstringBucket substringBucket) {
        return substringBucket.any(super::contains);
    }
}
