package com.robertsoultanaev.javasphinx.packet.header;

/**
 * Type to combine Sphinx header and payload
 */
public record HeaderAndDelta(Header header, byte[] delta) {
}
