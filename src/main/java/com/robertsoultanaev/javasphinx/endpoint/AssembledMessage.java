package com.robertsoultanaev.javasphinx.endpoint;

import java.util.UUID;

public record AssembledMessage(UUID uuid, byte[] messageBody) {
}