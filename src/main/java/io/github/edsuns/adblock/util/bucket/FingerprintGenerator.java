package io.github.edsuns.adblock.util.bucket;

/**
 * Created by Edsuns@qq.com on 2021/8/21.
 */
public class FingerprintGenerator implements SubstringGenerator {

    private char[] buffer;
    private int end = -1;

    protected boolean isSeparatorChar(char c) {
        return c == '*' || c == '^' || c == '|';
    }

    @Override
    public boolean next(char[] parent, char[] substring) {
        if (end < 0) {
            buffer = parent;
            end = 0;
        }
        int start = end;
        do {
            if (substring.length + start > buffer.length) {
                end = -1;
                return false;
            }
            if (isSeparatorChar(buffer[end])) {
                start = ++end;
            } else {
                end++;
            }
        } while (end - start != substring.length);
        System.arraycopy(buffer, start, substring, 0, substring.length);
        return true;
    }
}
