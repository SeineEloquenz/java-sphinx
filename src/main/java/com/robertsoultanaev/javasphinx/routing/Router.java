package com.robertsoultanaev.javasphinx.routing;

import com.robertsoultanaev.javasphinx.SphinxClient;
import com.robertsoultanaev.javasphinx.SphinxException;
import com.robertsoultanaev.javasphinx.packet.ProcessedPacket;
import org.msgpack.core.MessagePack;

import java.io.IOException;

public class Router {

    private final MixNodeRepository repository;
    private final SphinxClient client;

    public Router(final MixNodeRepository repository, final SphinxClient client) {
        this.repository = repository;
        this.client = client;
    }

    public MixNode findRelay(ProcessedPacket packet) throws IOException {
        final var unpacker = MessagePack.newDefaultUnpacker(packet.routing());
        final var routingLength = unpacker.unpackArrayHeader();
        final var flag = unpacker.unpackString();
        if (!SphinxClient.RELAY_FLAG.equals(flag)) {
            throw new SphinxException("Packet should not be relayed!");
        }
        final var nextNodeId = unpacker.unpackInt();
        unpacker.close();
        return repository.byId(nextNodeId);
    }

    public OutwardMessage findForwardDestination(ProcessedPacket packet) throws IOException {
        final var unpacker = MessagePack.newDefaultUnpacker(packet.routing());
        final var routingLength = unpacker.unpackArrayHeader();
        final var flag = unpacker.unpackString();
        if (!SphinxClient.DEST_FLAG.equals(flag)) {
            throw new SphinxException("Packet should not be forwarded!");
        }
        unpacker.close();
        final var destinationAndMessage = client.receiveForward(packet.macKey(), packet.packetContent().delta());
        return new OutwardMessage(DestinationEncoding.decode(destinationAndMessage.destination()), destinationAndMessage.message());
    }
}
