package io.github.edsuns.adblock.bucket;

import io.github.edsuns.adblock.util.bucket.FingerprintBucket;
import io.github.edsuns.adblock.util.bucket.HashBucketFilter;
import io.github.edsuns.adblock.util.bucket.SubstringBucket;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Edsuns@qq.com on 2021/8/21.
 */
public class HashBucketFilterTest {

    private static final HashBucketFilter<FingerprintBucket> filter = new HashBucketFilter<>();

    @BeforeAll
    static void preparePattern() throws FingerprintBucket.NoFingerprintException {
        final String pattern = "example.com/finger*/query";
        filter.add(new FingerprintBucket(pattern));
    }

    @Test
    public void noFingerprint() {
        final String noFingerprintPattern = "NoFi*print";
        boolean hasException;
        try {
            filter.add(new FingerprintBucket(noFingerprintPattern));
            hasException = false;
        } catch (FingerprintBucket.NoFingerprintException e) {
            hasException = true;
        }
        assertTrue(hasException);
    }

    @Test
    public void matched() {
        final String url = "https://example.com/fingerprint/query";
        try (SubstringBucket bucket = new SubstringBucket(url)) {
            assertNotNull(filter.matches(bucket));
        }
    }

    @Test
    public void matchNegative() {
        final String url = "https://example.com/fingerprint/send";
        try (SubstringBucket bucket = new SubstringBucket(url)) {
            assertNull(filter.matches(bucket));
        }
    }

    @Test
    public void matchNegativeOnNoSubstring() {
        final String url = "short";
        try (SubstringBucket bucket = new SubstringBucket(url)) {
            assertNull(filter.matches(bucket));
        }
    }

    @Test
    public void matchTotallyNegative() {
        final String url = "https://negative.io/negative/negative";
        try (SubstringBucket bucket = new SubstringBucket(url)) {
            assertNull(filter.matches(bucket));
        }
    }
}
