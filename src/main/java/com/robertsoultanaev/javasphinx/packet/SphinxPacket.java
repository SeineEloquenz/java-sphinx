package com.robertsoultanaev.javasphinx.packet;

import com.robertsoultanaev.javasphinx.packet.header.HeaderAndDelta;
import com.robertsoultanaev.javasphinx.SphinxParams;

import java.util.Objects;

/**
 * Type used to represent the Sphinx packet as it is encoded into a binary format
 */
public final class SphinxPacket {
    private final int headerLength;
    private final int bodyLength;
    private final HeaderAndDelta headerAndDelta;

    /**
     *
     */
    public SphinxPacket(SphinxParams params, HeaderAndDelta headerAndDelta) {
        this.headerLength = params.headerLength();
        this.bodyLength = params.bodyLength();
        this.headerAndDelta = headerAndDelta;
    }

    public int headerLength() {
        return headerLength;
    }

    public int bodyLength() {
        return bodyLength;
    }

    public HeaderAndDelta headerAndDelta() {
        return headerAndDelta;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SphinxPacket) obj;
        return this.headerLength == that.headerLength && this.bodyLength == that.bodyLength &&
                Objects.equals(this.headerAndDelta, that.headerAndDelta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headerLength, bodyLength, headerAndDelta);
    }

    @Override
    public String toString() {
        return "SphinxPacket[" +
                "headerLength=" + headerLength + ", " +
                "bodyLength=" + bodyLength + ", " +
                "headerAndDelta=" + headerAndDelta + ']';
    }

}
