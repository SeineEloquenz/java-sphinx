package com.robertsoultanaev.javasphinx;

/**
 * Type to combine Sphinx header and payload
 */
public record HeaderAndDelta(Header header, byte[] delta) {
}
