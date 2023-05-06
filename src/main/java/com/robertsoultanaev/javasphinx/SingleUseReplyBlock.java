package com.robertsoultanaev.javasphinx;

/**
 * Type to combine a reply block and the related information
 */
public record SingleUseReplyBlock(byte[] xid, byte[][] keyTuple, NymTuple nymTuple) {
}
