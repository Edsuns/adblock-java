package io.github.edsuns.adblock.bloom;

import io.github.edsuns.adblock.util.bloom.BitArray;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by Edsuns@qq.com on 2021/8/21.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BitArrayTest {

    /**
     * @see BitArrayTest#performanceSet()
     * @see BitArrayTest#performanceGet()
     */
    private static final int[] hashes = {
            -1206840145, 42202881, 157957853, -499880827, 1475544601,
            -863408392, 46649364, 174976372, 890120596, -2096634188,
            -867068221, 46531935, 174640367, 878928047, -2127910177,
            -980522866, 45005412, 168928336, 420033592, 509429756,
            -947602118, 44467868, 168374104, 605777920, 1312624052,
            1705397300, 22999346, 86539430, -1734050082, -1136325190,
            1392102911, 19036685, 71191417, 1290251745, -1433416795,
            1442517346, 20616994, 75788458, 1444114170, -1004566126,
            -1289602469, 41161007, 153938151, -837461001, 249682847,
            -766529339, 47585483, 179054199, 1299646239, -574223709,
            -1413832303, 39394245, 147413149, -1330937555, -1507664715,
            -1067639471, 43924811, 164679439, 67524487, -761232685,
            -985705839, 44900473, 168555553, 403217329, 460598057,
            -1108274292, 43103650, 162036782, -83688874, -1275456006,
            -1357881673, 39052125, 147767889, -1046426295, -333909595,
            1411782634, 20170018, 74159746, 1322003778, -1439852062,
            -1354874863, 40177125, 150387613, -1093882835, -656110635,
            -1014966182, 44448644, 166970200, 286559536, 42411308,
            -1127841250, 42056676, 159223344, -116554152, -1274992580,
            1443006298, 20614708, 75801928, 1445927712, -998872612,
            -1274444951, 41131295, 154167147, -763105773, 551439095,
            -1184149951, 42372429, 158809573, -401893643, 1844430909,
            -1047515992, 44026746, 165318110, 157490966, -415053630,
            -1249381677, 41398813, 155275385, -658261279, 938694133,
            -1294692140, 41023352, 153512048, -853373632, 204586792,
            -924309125, 45795983, 171810463, 647228383, 1330652687,
            -979983582, 45091600, 169095052, 420555796, 508717064,
            -930884319, 45588307, 171208271, 627188279, 1274851371,
            -1183814542, 42391866, 158857842, -401088414, 1846214666,
            -1037118317, 44279423, 166138679, 190505351, -320514513,
            -927053757, 44683609, 169225053, 695328501, 1654300033
    };

    private static final int SIZE = 1024 * 512;
    private static final int MOD = SIZE * BitArray.LAYER_SIZE;
    private static final BitArray bitArray = new BitArray(SIZE, true);

    @Test
    @Order(1)
    public void setBit() {
        BitArray bitArray = new BitArray(32);
        bitArray.set(32);
        assertFalse(bitArray.get(36));
        assertTrue(bitArray.get(32));
    }

    @Test
    @Order(2)
    public void setNegativeBit() {
        BitArray bitArray = new BitArray(32);
        bitArray.set(-32);
        assertFalse(bitArray.get(-36));
        assertTrue(bitArray.get(-32));
    }

    @Test
    @Order(3)
    public void isSizeSticky() {
        final int size = 1;
        final int mod = size * BitArray.LAYER_SIZE;
        BitArray bitArray = new BitArray(size);

        bitArray.set(-32);
        bitArray.set(63 % mod);
        bitArray.set(-63 % mod);
        bitArray.set(64 % mod);
        bitArray.set(-64 % mod);
        assertTrue(bitArray.get(-32));
        assertTrue(bitArray.get(63 % mod));
        assertTrue(bitArray.get(-63 % mod));
        assertTrue(bitArray.get(64 % mod));
        assertTrue(bitArray.get(-64 % mod));
        assertTrue(bitArray.isSizeSticky());

        assertFalse(bitArray.get(1024));
        assertFalse(bitArray.get(-1024));

        bitArray.set(64);
        bitArray.set(-64);
        assertFalse(bitArray.isSizeSticky());
    }

    @Test
    @Order(4)
    public void clearBit() {
        BitArray bitArray = new BitArray(32);
        bitArray.set(32);
        assertFalse(bitArray.get(36));

        assertTrue(bitArray.get(32));
        bitArray.clear(32);
        bitArray.set(36);
        assertFalse(bitArray.get(32));
        assertTrue(bitArray.get(36));
    }

    @Test
    @Order(5)
    public void clearAll() {
        BitArray bitArray = new BitArray(32);
        bitArray.set(24);
        bitArray.set(32);
        bitArray.set(36);
        assertFalse(bitArray.get(42));

        assertTrue(bitArray.get(24));
        assertTrue(bitArray.get(32));
        assertTrue(bitArray.get(36));
        bitArray.clear();
        bitArray.set(42);
        assertFalse(bitArray.get(24));
        assertFalse(bitArray.get(32));
        assertFalse(bitArray.get(36));

        assertTrue(bitArray.get(42));
    }

    @Test
    @Order(6)
    public void performanceSet() {
        for (int h : hashes) {
            bitArray.set(h % MOD);
        }
    }

    @Test
    @Order(7)
    public void performanceGet() {
        boolean get = true;
        for (int h : hashes) {
            if (!bitArray.get(h % MOD)) {
                get = false;
                break;
            }
        }
        assertTrue(get);
    }
}
