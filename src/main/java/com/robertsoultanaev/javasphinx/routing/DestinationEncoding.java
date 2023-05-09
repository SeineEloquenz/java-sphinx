package com.robertsoultanaev.javasphinx.routing;

import com.robertsoultanaev.javasphinx.Util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class DestinationEncoding {

    public static byte[] encode(InetSocketAddress address) {
        return Util.concatenate(address.getAddress().getAddress(), Util.encodeInt(address.getPort()));
    }

    public static InetSocketAddress decode(byte[] destination) {
        final var addressBytes = Util.slice(destination, 8);
        final var port = Util.slice(destination, 8, 12);
        try {
            return new InetSocketAddress(InetAddress.getByAddress(addressBytes), Util.decodeInt(port));
        } catch (UnknownHostException e) {
            System.out.println("Illegal IP address decoded!"); //TODO actually handle this correctly
            throw new RuntimeException(e);
        }
    }
}
