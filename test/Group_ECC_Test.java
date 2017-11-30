import org.bouncycastle.math.ec.ECPoint;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class Group_ECC_Test {

    private Group_ECC group_ecc;

    private String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789abcdef".toCharArray();

        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    @Before
    public void setUp() {
        group_ecc = new Group_ECC();
    }

    @Test
    public void expon() throws Exception {
        BigInteger secret = new BigInteger("10242318609670578569309311701916918226942711495988531232197429015905");

        ECPoint base = group_ecc.getGenerator();

        String expectedOutput1 = "02a66335a59f1277c193315eb2db69808e6eaf15c944286765c0adcae2";
        String output1 = bytesToHex(group_ecc.expon(base, secret).getEncoded(true));

        assertEquals(expectedOutput1, output1);
    }

    @Test
    public void multiexpon() throws Exception {
        BigInteger secret1 = new BigInteger("10242318609670578569309311701916918226942711495988531232197429015905");
        BigInteger secret2 = new BigInteger("9166896489953568699130350165214278503117209070949180823539577781184");
        
        ECPoint base = group_ecc.getGenerator();
        List<BigInteger> exponents = Arrays.asList(secret1, secret2);

        String expectedOutput1 = "03085f86c52bbb391e7fba0dd1e39541fe89ac5b6afd576c338948abe0";
        String output1 = bytesToHex(group_ecc.multiexpon(base, exponents).getEncoded(true));

        assertEquals(expectedOutput1, output1);
    }

}