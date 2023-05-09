package com.robertsoultanaev.javasphinx.mix;

import org.bouncycastle.math.ec.ECPoint;

public record MixNode(int id, String host, int port, ECPoint publicKey) {
}
