package io.github.edsuns.util.bloom;

import java.util.Arrays;

/**
 * Created by Edsuns@qq.com on 2021/8/21.
 */
public class BitArray {

    /**
     * one word can preserve [0, 31] and [-1, -32]
     */
    private long[] words;

    private int tailUsedIndex = -1;

    /**
     * Whether the size of "words" is user-specified.
     */
    private transient boolean isSizeSticky;

    private transient boolean isClear;

    public BitArray() {
        this(16);
        isSizeSticky = false;
    }

    public BitArray(int wordsSize) {
        words = new long[wordsSize];
        isSizeSticky = true;
    }

    /**
     * Ensures that the BitArray can hold enough words.
     *
     * @param wordsRequired the minimum acceptable number of words.
     */
    private void ensureCapacity(int wordsRequired) {
        if (words.length < wordsRequired) {
            // Allocate larger of doubled size or required size
            int request = Math.max(2 * words.length, wordsRequired);
            words = Arrays.copyOf(words, request);
            isSizeSticky = false;
        }
    }

    private int[] getIndexesOf(int bitIndex) {
        final int[] indexes = new int[2];
        final int splitSize = Long.SIZE / 2;
        if (bitIndex > 0) {
            indexes[0] = bitIndex / splitSize;
            indexes[1] = bitIndex % splitSize;
        } else {
            bitIndex = -bitIndex - 1;
            indexes[0] = bitIndex / splitSize;
            indexes[1] = (bitIndex % splitSize) + splitSize;
        }
        return indexes;
    }

    public void set(int bitIndex) {
        final int[] indexes = getIndexesOf(bitIndex);
        ensureCapacity(indexes[0] + 1);
        if (indexes[0] > tailUsedIndex) {
            tailUsedIndex = indexes[0];
        }
        words[indexes[0]] |= 1L << indexes[1];
        isClear = false;
    }

    public void clear(int bitIndex) {
        final int[] indexes = getIndexesOf(bitIndex);
        words[indexes[0]] &= ~(1L << indexes[1]);
    }

    /**
     * Sets all the bits in this BitArray to {@code false}.
     */
    public void clear() {
        if (isClear) {
            return;
        }
        while (tailUsedIndex >= 0)
            words[tailUsedIndex--] = 0;
        isClear = true;
    }

    public boolean get(int bitIndex) {
        final int[] indexes = getIndexesOf(bitIndex);
        if (indexes[0] >= words.length) {
            return false;
        }
        return (words[indexes[0]] & 1L << indexes[1]) != 0;
    }

    /**
     * @return Whether the size of "words" is user-specified.
     */
    public boolean isSizeSticky() {
        return isSizeSticky;
    }
}
