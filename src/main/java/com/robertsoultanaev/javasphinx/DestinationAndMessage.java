package com.robertsoultanaev.javasphinx;

/**
 * Type to combine destination and message
 */
public record DestinationAndMessage(byte[] destination, byte[] message) {
}
