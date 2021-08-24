package io.github.edsuns.adblock.util.bloom;

import java.util.Arrays;

/**
 * Created by Edsuns@qq.com on 2021/8/21.
 */
public class BitArray {

    public static final int WORD_SIZE = Long.SIZE;
    public static final int LAYER_SIZE = WORD_SIZE / 2;

    /**
     * Every word is divided into positive and negative layers,
     * one word can preserve [0, 31] and [-1, -32].
     */
    private long[] words;

    private int tailUsedIndex = -1;

    /**
     * Whether to disable automatic capacity expansion.
     */
    private final boolean fixedSize;

    /**
     * Whether the size of "words" is user-specified.
     */
    private transient boolean isSizeSticky;

    /**
     * Whether words is cleared, used to avoid clearing multiple times.
     *
     * @see BitArray#clear()
     */
    private transient boolean isCleared;

    public BitArray(int wordsSize) {
        this(wordsSize, false);
    }

    public BitArray(int wordsSize, boolean fixedSize) {
        this.words = new long[wordsSize];
        this.fixedSize = fixedSize;
        this.isSizeSticky = true;
    }

    /**
     * Ensures that the BitArray can hold enough words.
     *
     * @param wordsRequired the minimum acceptable number of words.
     */
    private void ensureCapacity(int wordsRequired) {
        if (words.length < wordsRequired) {
            if (fixedSize) {
                throw new IndexOutOfBoundsException("Insufficient capacity of BitArray!");
            }
            // Allocate larger of doubled size or required size
            int request = Math.max(2 * words.length, wordsRequired);
            words = Arrays.copyOf(words, request);
            isSizeSticky = false;
        }
    }

    private int[] getIndexesOf(int bitIndex) {
        final int[] indexes = new int[2];
        final int splitSize = WORD_SIZE / 2;
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

    /**
     * Sets the bit at the specified index to {@code true}.
     *
     * @param bitIndex a bit index, allow negative numbers
     */
    public void set(int bitIndex) {
        final int[] indexes = getIndexesOf(bitIndex);
        ensureCapacity(indexes[0] + 1);
        if (indexes[0] > tailUsedIndex) {
            tailUsedIndex = indexes[0];
        }
        words[indexes[0]] |= 1L << indexes[1];
        isCleared = false;
    }

    /**
     * Sets the bit specified by the index to {@code false}.
     *
     * @param bitIndex the index of the bit to be cleared, allow negative numbers
     */
    public void clear(int bitIndex) {
        final int[] indexes = getIndexesOf(bitIndex);
        words[indexes[0]] &= ~(1L << indexes[1]);
    }

    /**
     * Sets all the bits in this BitArray to {@code false}.
     */
    public void clear() {
        if (isCleared) {
            return;
        }
        while (tailUsedIndex >= 0)
            words[tailUsedIndex--] = 0;
        isCleared = true;
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
