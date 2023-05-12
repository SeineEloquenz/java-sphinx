package com.robertsoultanaev.javasphinx.routing;

import com.robertsoultanaev.javasphinx.SerializationUtils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class DestinationEncoding {

    public static byte[] encode(InetSocketAddress address) {
        return SerializationUtils.concatenate(address.getAddress().getAddress(), SerializationUtils.encodeInt(address.getPort()));
    }

    public static InetSocketAddress decode(byte[] destination) {
        final var addressBytes = SerializationUtils.slice(destination, 8);
        final var port = SerializationUtils.slice(destination, 8, 12);
        try {
            return new InetSocketAddress(InetAddress.getByAddress(addressBytes), SerializationUtils.decodeInt(port));
        } catch (UnknownHostException e) {
            System.out.println("Illegal IP address decoded!"); //TODO actually handle this correctly
            throw new RuntimeException(e);
        }
    }
}
