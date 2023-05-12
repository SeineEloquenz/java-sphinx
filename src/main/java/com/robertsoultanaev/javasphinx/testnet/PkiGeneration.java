package com.robertsoultanaev.javasphinx.testnet;

import com.robertsoultanaev.javasphinx.SerializationUtils;
import com.robertsoultanaev.javasphinx.SphinxParams;
import com.robertsoultanaev.javasphinx.pki.PkiEntry;
import com.robertsoultanaev.javasphinx.pki.PkiGenerator;
import org.bouncycastle.math.ec.ECPoint;

import java.util.LinkedList;

public class PkiGeneration {

    public static void main(String[] args) {
        System.out.println("This will generate initial pki information for a testnet");
        final var generator = new PkiGenerator(new SphinxParams());
        final var mixes = new LinkedList<PkiEntry>();
        mixes.add(generator.generateKeyPair());
        mixes.add(generator.generateKeyPair());
        mixes.add(generator.generateKeyPair());
        for (final var entry: mixes) {
            System.out.println("pub:");
            System.out.println(pubKeyToString(entry.pub()));
            System.out.println("priv:");
            System.out.println(entry.priv());
        }
    }

    private static String pubKeyToString(ECPoint pubKey) {
        return SerializationUtils.base64encode(SerializationUtils.encodeECPoint(pubKey));
    }
}
