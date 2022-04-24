package io.github.edsuns.adblock.hash;

import io.github.edsuns.adblock.util.hash.RabinFingerprint;
import io.github.edsuns.adblock.util.hash.RabinKarpAlgorithm;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by Edsuns@qq.com on 2021/8/20.
 */
public class RabinFingerprintTest {
    @Test
    public void searchSubstring() {
        final String textStr = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz";
        final String targetStr = "GgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYy";// very long one
        final String targetStr2 = "MmNn";// very short one
        final int expected = textStr.indexOf(targetStr);
        final int expected2 = textStr.indexOf(targetStr2);
        final char[] text = textStr.toCharArray();
        final char[] target = targetStr.toCharArray();
        final char[] target2 = targetStr2.toCharArray();

        assertEquals(expected, RabinKarpAlgorithm.search(RabinFingerprint.p13, text, target));
        assertEquals(expected, RabinKarpAlgorithm.search(RabinFingerprint.p17, text, target));
        assertEquals(expected, RabinKarpAlgorithm.search(RabinFingerprint.p19, text, target));
        assertEquals(expected, RabinKarpAlgorithm.search(RabinFingerprint.p31, text, target));
        assertEquals(expected, RabinKarpAlgorithm.search(RabinFingerprint.p41, text, target));
        assertEquals(expected, RabinKarpAlgorithm.search(RabinFingerprint.p53, text, target));

        assertEquals(expected2, RabinKarpAlgorithm.search(RabinFingerprint.p13, text, target2));
        assertEquals(expected2, RabinKarpAlgorithm.search(RabinFingerprint.p17, text, target2));
        assertEquals(expected2, RabinKarpAlgorithm.search(RabinFingerprint.p19, text, target2));
        assertEquals(expected2, RabinKarpAlgorithm.search(RabinFingerprint.p31, text, target2));
        assertEquals(expected2, RabinKarpAlgorithm.search(RabinFingerprint.p41, text, target2));
        assertEquals(expected2, RabinKarpAlgorithm.search(RabinFingerprint.p53, text, target2));

        assertEquals(-1, RabinKarpAlgorithm.search(RabinFingerprint.p13, target2, text));
    }
}
