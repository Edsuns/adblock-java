package io.github.edsuns.adblock.util.bucket;

/**
 * Created by Edsuns@qq.com on 2021/8/20.
 */
interface SubstringGenerator {
    /**
     * Generate substrings.
     *
     * @return substring or null if all substrings generated
     */
    boolean next(char[] parent, char[] substring);
}
