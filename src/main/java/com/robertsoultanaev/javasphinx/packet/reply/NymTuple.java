package com.robertsoultanaev.javasphinx.packet.reply;

import com.robertsoultanaev.javasphinx.packet.header.Header;

/**
 * Class to represent the reply block used for replying to anonymous recipients
 */
public record NymTuple(byte[] node, Header header, byte[] kTilde) {
}
