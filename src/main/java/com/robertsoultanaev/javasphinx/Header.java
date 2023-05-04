package com.robertsoultanaev.javasphinx;

import org.bouncycastle.math.ec.ECPoint;

/**
 * Class to represent the header of a Sphinx packet
 */
public record Header(ECPoint alpha, byte[] beta, byte[] gamma) {
}
