import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.modes.SICBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;

import java.util.ArrayList;
import java.util.Arrays;

public class SphinxParams {

    private final int keyLength;
    private final int bodyLength;
    private final int headerLength;
    private final Group_ECC group;

    public SphinxParams() {
        this.keyLength = 16;
        this.bodyLength = 1024;
        this.headerLength = 192;
        this.group = new Group_ECC();
    }

    public byte[] aesCtr(byte[] key, byte[] message, byte[] iv) {
        CipherParameters params = new ParametersWithIV(new KeyParameter(key), iv);
        SICBlockCipher engine = new SICBlockCipher(new AESEngine());

        engine.init(true, params);

        byte[] ciphertext = new byte[message.length];

        engine.processBytes(message, 0, message.length, ciphertext, 0);

        return ciphertext;
    }

    public byte[] aesCtr(byte[] key, byte[] message) {
        byte[] iv = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        return aesCtr(key, message, iv);
    }

    public byte[] lionessEnc(byte[] key, byte[] message) {
        return null;
    }

    public byte[] lionessDec(byte[] key, byte[] message) {
        return null;
    }

    public byte[] xorRho(byte[] key, byte[] plain) {
        assert (key.length == keyLength);

        return aesCtr(key, plain);
    }

    public byte[] mu(byte[] key, byte[] data) {
        Mac mac = new HMac(new SHA256Digest());
        CipherParameters cipherParameters = new KeyParameter(key);
        mac.init(cipherParameters);
        byte[] output = new byte[mac.getMacSize()];

        mac.update(data, 0, data.length);
        mac.doFinal(output, 0);

        return Arrays.copyOf(output, keyLength);
    }

    public byte[] pi(byte[] key, byte[] data) {
        return null;
    }

    public byte[] pii(byte[] key, byte[] data) {
        return null;
    }

    public byte[] hash(byte[] data) {
        SHA256Digest digest = new SHA256Digest();
        byte[] output = new byte[digest.getDigestSize()];

        digest.update(data, 0, data.length);
        digest.doFinal(output, 0);

        return output;
    }

    public byte[] getAesKey(ECPoint s) {
        String prefix = Hex.toHexString("aes_key:".getBytes(StandardCharsets.US_ASCII));
        String printable = Hex.toHexString(group.printable(s));

        byte[] data = Hex.decode(prefix + printable);
        byte[] hash = hash(data);

        return Arrays.copyOf(hash, keyLength);
    }

    public byte[] deriveKey(byte[] k, byte[] flavor) {
        byte[] m = new byte[keyLength];

        return aesCtr(k, m, flavor);
    }

    public BigInteger hb(ECPoint alpha, byte[] k) {
        byte[] flavor = "hbhbhbhbhbhbhbhb".getBytes(StandardCharsets.US_ASCII);
        byte[] K = deriveKey(k, flavor);

        return group.makeexp(K);
    }

    public byte[] hrho(byte[] k) {
        byte[] flavor = "hrhohrhohrhohrho".getBytes(StandardCharsets.US_ASCII);

        return deriveKey(k, flavor);
    }

    public byte[] hmu(byte[] k) {
        byte[] flavor = "hmu:hmu:hmu:hmu:".getBytes(StandardCharsets.US_ASCII);

        return deriveKey(k, flavor);
    }

    public byte[] hpi(byte[] k) {
        byte[] flavor = "hpi:hpi:hpi:hpi:".getBytes(StandardCharsets.US_ASCII);

        return deriveKey(k, flavor);
    }

    public byte[] htau(byte[] k) {
        byte[] flavor = "htauhtauhtauhtau".getBytes(StandardCharsets.US_ASCII);

        return deriveKey(k, flavor);
    }
}
