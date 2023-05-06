package com.robertsoultanaev.javasphinx.packet.message;

/**
 * Type to combine destination and message
 */
public record DestinationAndMessage(byte[] destination, byte[] message) {
}
