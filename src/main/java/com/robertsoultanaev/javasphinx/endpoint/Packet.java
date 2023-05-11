package com.robertsoultanaev.javasphinx.endpoint;

public record Packet(String uuid, int sequenceNumber, int packetsInMessage, byte[] payload) {
}