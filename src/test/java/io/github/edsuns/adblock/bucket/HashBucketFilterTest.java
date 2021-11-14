package io.github.edsuns.adblock.bucket;

import io.github.edsuns.adblock.util.bucket.FingerprintBucket;
import io.github.edsuns.adblock.util.bucket.HashBucketFilter;
import io.github.edsuns.adblock.util.bucket.SubstringBucket;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.annotation.Nullable;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Edsuns@qq.com on 2021/8/21.
 */
public class HashBucketFilterTest {

    static class Substrings extends SubstringBucket<String, Integer> {
        public Substrings(String data) {
            super(data);
        }

        @Nullable
        @Override
        protected String generateExtraData(String source) {
            return source;
        }

        @Override
        protected boolean fullyMatches(@Nullable String sExtraData, @Nullable Integer fExtraData) {
            return sExtraData != null && fExtraData != null;
        }
    }

    static class Fingerprints extends FingerprintBucket<Integer> {
        public Fingerprints(String data) throws NoFingerprintException {
            super(data);
        }

        @Nullable
        @Override
        protected Integer generateExtraData(String source) {
            return source.length();
        }
    }

    private static final HashBucketFilter<String, Integer> filter = new HashBucketFilter<>();

    static final String pattern = "example.com/finger*/query";

    @BeforeAll
    static void preparePattern() throws FingerprintBucket.NoFingerprintException {
        filter.add(new Fingerprints(pattern));
    }

    @Test
    public void noFingerprint() {
        final String noFingerprintPattern = "NoFi*print";
        boolean hasException;
        try {
            filter.add(new Fingerprints(noFingerprintPattern));
            hasException = false;
        } catch (FingerprintBucket.NoFingerprintException e) {
            hasException = true;
        }
        assertTrue(hasException);
    }

    @Test
    public void matchedOriginal() {
        final String url = "https://example.com/fingerprint/query";
        try (SubstringBucket<String, Integer> bucket = new SubstringBucket<>(url)) {
            FingerprintBucket<Integer> actual = filter.matches(bucket);
            assertNotNull(actual);
            assertEquals(pattern.length(), actual.getExtraData());
        }
    }

    @Test
    public void matched() {
        final String url = "https://example.com/fingerprint/query";
        try (Substrings bucket = new Substrings(url)) {
            FingerprintBucket<Integer> actual = filter.matches(bucket);
            assertNotNull(actual);
            assertEquals(pattern.length(), actual.getExtraData());
        }
    }

    @Test
    public void matchNegative() {
        final String url = "https://example.com/fingerprint/send";
        try (Substrings bucket = new Substrings(url)) {
            assertNull(filter.matches(bucket));
        }
    }

    @Test
    public void matchNegativeOnNoSubstring() {
        final String url = "short";
        try (Substrings bucket = new Substrings(url)) {
            assertNull(filter.matches(bucket));
        }
    }

    @Test
    public void matchTotallyNegative() {
        final String url = "https://negative.io/negative/negative";
        try (Substrings bucket = new Substrings(url)) {
            assertNull(filter.matches(bucket));
        }
    }
}
