package io.github.edsuns.adblock.util.hash;

/**
 * Created by Edsuns@qq.com on 2021/8/21.
 */
public final class RabinKarpAlgorithm {
    interface RollingHash {
        /**
         * Hash specified substring by RabinFingerprint.
         * <p>
         * Usage: {@link RabinKarpAlgorithm#search(RollingHash, char[], char[])}
         *
         * @param str      origin string
         * @param start    start index of substring
         * @param count    length of substring
         * @param lastHash hash of the last substring
         * @return hash code
         */
        int hash(char[] str, int start, int count, int lastHash);

        /**
         * @see RollingHash#hash(char[], int, int, int)
         */
        int hash(char[] str, int start, int count);

        /**
         * @see RollingHash#hash(char[], int, int)
         */
        int hash(char[] str, int count);
    }

    private RabinKarpAlgorithm() {
        // private
    }

    /**
     * Use Rabin–Karp algorithm to search specified substring.
     *
     * @param text   string
     * @param target the substring to search for.
     * @return the index of the first occurrence of the specified substring, or {@code -1} if there is no such occurrence.
     * @see <a href="https://en.wikipedia.org/wiki/Rabin%E2%80%93Karp_algorithm">https://en.wikipedia.org/wiki/Rabin%E2%80%93Karp_algorithm</a>
     */
    public static int search(RollingHash fn, char[] text, char[] target) {
        int n = text.length, m = target.length;
        if (n < m) return -1;
        int targetHash = fn.hash(target, m);
        int hash = fn.hash(text, m);
        for (int i = 0, end = n - m; ; ) {
            if (hash == targetHash) {
                boolean arrEquals = true;
                for (int j = 0; j < m; j++) {
                    if (text[i + j] != target[j]) {
                        arrEquals = false;
                        break;
                    }
                }
                if (arrEquals) return i;
            }
            if (++i > end) break;
            hash = fn.hash(text, i, m, hash);
        }
        return -1;
    }
}
