package io.github.edsuns.util.hash;

/**
 * Created by Edsuns@qq.com on 2021/8/20.
 *
 * @see <a href="https://github.com/Edsuns/AdblockAndroid/blob/master/adblock-client/src/main/cpp/third-party/hashset-cpp/hashFn.h">C++ Implement</a>
 * @see <a href="https://en.wikipedia.org/wiki/Rabin_fingerprint">https://en.wikipedia.org/wiki/Rabin_fingerprint</a>
 */
public class RabinFingerprint implements RabinKarpAlgorithm.RollingHash {

    public static final RabinFingerprint p13 = new RabinFingerprint(13);
    public static final RabinFingerprint p17 = new RabinFingerprint(17);
    public static final RabinFingerprint p19 = new RabinFingerprint(19);
    public static final RabinFingerprint p31 = new RabinFingerprint(31);
    public static final RabinFingerprint p41 = new RabinFingerprint(41);
    public static final RabinFingerprint p53 = new RabinFingerprint(53);

    private final int primeNum;
    private final int[] precomputedPowers = new int[30];

    private RabinFingerprint(int primeNum) {
        this.primeNum = primeNum;

        int ans = 1;
        precomputedPowers[0] = ans;
        for (int i = 1; i < precomputedPowers.length; i++) {
            ans *= primeNum;
            precomputedPowers[i] = ans;
        }
    }

    private int customPow(int exp) {
        if (exp < precomputedPowers.length) {
            return precomputedPowers[exp];
        }
        int base = primeNum;

        // TODO: Optimization possible here when passed in toSize which is bigger
        // than precomputedArraySize, we can start from the value of the last
        // precomputed value.
        int result = 1;
        while (exp > 0) {
            if ((exp & 1) > 0)
                result *= base;
            exp >>= 1;
            base *= base;
        }
        return result;
    }

    /**
     * Hash specified substring by RabinFingerprint.
     * <p>
     * Usage: {@link RabinKarpAlgorithm#search(RabinKarpAlgorithm.RollingHash, char[], char[])}
     *
     * @param str      origin string
     * @param start    start index of substring
     * @param count    length of substring
     * @param lastHash hash of the last substring
     * @return hash
     */
    @Override
    public int hash(char[] str, int start, int count, int lastHash) {
        final int end = start + count - 1;
        final char lastChar = str[start - 1];
        return (lastHash - lastChar * customPow(count - 1)) * primeNum + str[end];
    }

    /**
     * @see RabinFingerprint#hash(char[], int, int, int)
     */
    @Override
    public int hash(char[] str, int start, int count) {
        int total = 0;
        for (int i = start; i < count; i++) {
            total += str[i] * customPow(count - i - 1);
        }
        return total;
    }

    /**
     * @see RabinFingerprint#hash(char[], int, int, int)
     */
    @Override
    public int hash(char[] str, int count) {
        return hash(str, 0, count);
    }
}
