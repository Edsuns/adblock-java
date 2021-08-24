package io.github.edsuns.adblock.bloom;

import io.github.edsuns.adblock.util.bloom.BloomFilter;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by Edsuns@qq.com on 2021/8/21.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BloomFilterTest {

    private static final int[] item = {46, 1046, 2000, 123456, 654321, 987654, -56789, -321678};
    private static final int[] itemFalse = {1234, 56789, -890};
    private static final BloomFilter filter = new BloomFilter(1024 * 32);

    @BeforeAll
    public static void prepareFilter() {
        filter.add(item);
    }

    @Test
    @Order(1)
    public void contains() {
        assertTrue(filter.contains(item));
        assertFalse(filter.contains(itemFalse));
    }

    @Test
    @Order(2)
    public void performanceAdd() {
        for (int i = 0; i < 100; i++) {
            filter.add(item);
        }
        assertFalse(filter.contains(itemFalse));
    }

    @Test
    @Order(3)
    public void performanceContains() {
        boolean contains = true;
        for (int i = 0; i < 100; i++) {
            if (!filter.contains(item)) {
                contains = false;
                break;
            }
        }
        assertTrue(contains);
        assertFalse(filter.contains(itemFalse));
    }
}
