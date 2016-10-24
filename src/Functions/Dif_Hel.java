package Functions;

import java.math.BigInteger;


public class Dif_Hel {
    public static BigInteger calculateMod(BigInteger x, BigInteger y, BigInteger z) {
        return x.modPow(y, z);
    }
}
