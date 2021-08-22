package io.github.edsuns.util.bucket;

/**
 * Created by Edsuns@qq.com on 2021/8/21.
 */
class FingerprintGenerator implements SubstringGenerator {

    private char[] buffer;
    private int i = -1;

    private boolean isSeparatorChar(char c) {
        return c == '*' || c == '^' || c == '|';
    }

    @Override
    public boolean next(char[] parent, char[] substring) {
        if (i < 0) {
            buffer = parent;
            i = 0;
        }
        int j = i;
        do {
            if (substring.length + j > buffer.length) {
                i = -1;
                return false;
            }
            if (isSeparatorChar(buffer[j])) {
                i = ++j;
            } else {
                i++;
            }
        } while (i - j != substring.length);
        System.arraycopy(buffer, j, substring, 0, substring.length);
        return true;
    }
}
