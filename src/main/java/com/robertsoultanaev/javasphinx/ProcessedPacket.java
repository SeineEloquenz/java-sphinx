package com.robertsoultanaev.javasphinx;

/**
 * Type to represent the return value of the mix node processing method
 */
public record ProcessedPacket(byte[] tag, byte[] routing, HeaderAndDelta headerAndDelta, byte[] macKey) {
}
