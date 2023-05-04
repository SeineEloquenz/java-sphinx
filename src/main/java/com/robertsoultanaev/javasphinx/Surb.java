package com.robertsoultanaev.javasphinx;

/**
 * Type to combine a reply block and the related information
 */
public record Surb(byte[] xid, byte[][] keytuple, NymTuple nymTuple) {
}
