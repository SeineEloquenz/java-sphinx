package com.robertsoultanaev.javasphinx.routing;

import java.net.InetSocketAddress;

public record OutwardMessage(InetSocketAddress address, byte[] message) {
}
