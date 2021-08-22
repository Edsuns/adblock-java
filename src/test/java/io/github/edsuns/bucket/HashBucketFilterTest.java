package io.github.edsuns.bucket;

import io.github.edsuns.util.bucket.FingerprintBucket;
import io.github.edsuns.util.bucket.HashBucketFilter;
import io.github.edsuns.util.bucket.SubstringBucket;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by Edsuns@qq.com on 2021/8/21.
 */
public class HashBucketFilterTest {

    private static final HashBucketFilter filter = new HashBucketFilter();

    @BeforeAll
    static void preparePattern() {
        final String pattern = "example.com/finger*/query";
        filter.add(new FingerprintBucket(pattern));
    }

    @Test
    public void contains() {
        final String url = "https://example.com/fingerprint/query";
        assertTrue(filter.contains(new SubstringBucket(url)));
    }

    @Test
    public void containsNegative() {
        final String url = "https://example.com/fingerprint/send";
        assertFalse(filter.contains(new SubstringBucket(url)));
    }

    @Test
    public void containsTotallyNegative() {
        final String url = "https://negative.io/negative/negative";
        assertFalse(filter.contains(new SubstringBucket(url)));
    }
}
