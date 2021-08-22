package io.github.edsuns.util.bloom;

/**
 * Created by Edsuns@qq.com on 2021/8/21.
 */
public class BloomFilter {

    private final BitArray bitArray;

    public BloomFilter(int size) {
        bitArray = new BitArray(size);
    }

    public void add(int[] hashes) {
        for (int hash : hashes) {
            bitArray.set(hash);
        }
    }

    public void add(int[][] hashes) {
        for (int[] hs : hashes) {
            for (int hash : hs) {
                bitArray.set(hash);
            }
        }
    }

    public void clear() {
        bitArray.clear();
    }

    public boolean contains(int[] hashes) {
        for (int hash : hashes) {
            if (!bitArray.get(hash)) {
                return false;
            }
        }
        return true;
    }

    public boolean contains(int[][] hashes) {
        for (int[] hs : hashes) {
            for (int hash : hs) {
                if (!bitArray.get(hash)) {
                    return false;
                }
            }
        }
        return true;
    }
}
