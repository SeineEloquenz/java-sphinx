package com.robertsoultanaev.javasphinx;

/**
 * Type used to represent the Sphinx packet as it is encoded into a binary format
 */
public record SphinxPacket(ParamLengths paramLengths, HeaderAndDelta headerAndDelta) {
}
