package io.github.edsuns.adblock.util.bucket;

import javax.annotation.Nullable;

/**
 * Created by Edsuns@qq.com on 2021/8/21.
 */
class FixedSizeSubstringGenerator implements SubstringGenerator {

    @Nullable
    private char[] buffer;
    private int i = 0;

    @Override
    public boolean next(char[] parent, char[] substring) {
        if (buffer == null) {
            buffer = parent;
        }
        if (i > buffer.length - substring.length) {
            i = 0;
            buffer = null;
            return false;
        }
        System.arraycopy(buffer, i, substring, 0, substring.length);
        i++;
        return true;
    }
}
