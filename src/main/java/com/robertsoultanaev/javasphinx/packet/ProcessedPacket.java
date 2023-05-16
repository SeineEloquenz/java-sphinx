package com.robertsoultanaev.javasphinx.packet;

import com.robertsoultanaev.javasphinx.packet.header.PacketContent;
import org.msgpack.core.MessagePack;

import java.io.IOException;

/**
 * Type to represent the return value of the mix node processing method
 */
public record ProcessedPacket(byte[] tag, byte[] routing, PacketContent packetContent, byte[] macKey) {

    public String routingFlag() throws IOException {
        final var unpacker = MessagePack.newDefaultUnpacker(routing);
        int routingLen = unpacker.unpackArrayHeader();
        String flag = unpacker.unpackString();
        unpacker.close();
        return flag;
    }
}
