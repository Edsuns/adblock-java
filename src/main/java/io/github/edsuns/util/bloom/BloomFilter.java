package io.github.edsuns.util.bloom;

/**
 * Created by Edsuns@qq.com on 2021/8/21.
 */
public class BloomFilter {

    private final BitArray bitArray;
    private final int mod;

    public BloomFilter(int size) {
        bitArray = new BitArray(size, true);
        mod = size * BitArray.LAYER_SIZE;
    }

    public void add(int[] hashes) {
        for (int hash : hashes) {
            bitArray.set(hash % mod);
        }
    }

    public void add(int[][] hashes) {
        for (int[] hs : hashes) {
            for (int hash : hs) {
                bitArray.set(hash % mod);
            }
        }
    }

    public void clear() {
        bitArray.clear();
    }

    public boolean contains(int[] hashes) {
        for (int hash : hashes) {
            if (!bitArray.get(hash % mod)) {
                return false;
            }
        }
        return true;
    }

    public boolean contains(int[][] hashes) {
        for (int[] hs : hashes) {
            for (int hash : hs) {
                if (!bitArray.get(hash % mod)) {
                    return false;
                }
            }
        }
        return true;
    }
}
