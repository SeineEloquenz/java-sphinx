package com.robertsoultanaev.javasphinx.routing;

import org.bouncycastle.math.ec.ECPoint;

public record MixNode(int id, String host, int port, ECPoint publicKey) {
}
