package com.robertsoultanaev.javasphinx.packet;

import com.robertsoultanaev.javasphinx.packet.header.HeaderAndDelta;

/**
 * Type to represent the return value of the mix node processing method
 */
public record ProcessedPacket(byte[] tag, byte[] routing, HeaderAndDelta headerAndDelta, byte[] macKey) {
}
