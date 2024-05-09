package First;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Base64;

public class RSA {
    private BigInteger p;
    private BigInteger q;
    private BigInteger n;
    private BigInteger phi;
    private BigInteger e;
    private BigInteger d;
    private int bitlength = 1024;
    private SecureRandom r;

    public RSA() {
        r = new SecureRandom();
        p = BigInteger.probablePrime(bitlength, r);
        q = BigInteger.probablePrime(bitlength, r);
        n = p.multiply(q);
        phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        e = BigInteger.probablePrime(bitlength / 2, r);
        while (phi.gcd(e).compareTo(BigInteger.ONE) > 0 && e.compareTo(phi) < 0) {
            e = e.add(BigInteger.ONE);
        }
        d = e.modInverse(phi);
    }

    public String getPublicKey() {
        return e.toString() + "," + n.toString();
    }


    public static String bytesToString(byte[] encrypted) {
        return Base64.getEncoder().encodeToString(encrypted);
    }


    public byte[] sign(byte[] message) {
        return (new BigInteger(message)).modPow(d, n).toByteArray();
    }
}
