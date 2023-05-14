package com.robertsoultanaev.javasphinx.endpoint;

import java.util.UUID;

public record Packet(UUID uuid, int sequenceNumber, int packetsInMessage, byte[] payload) {
}