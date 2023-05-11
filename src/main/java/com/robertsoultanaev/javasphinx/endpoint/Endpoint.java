package com.robertsoultanaev.javasphinx.endpoint;

import com.robertsoultanaev.javasphinx.SphinxClient;
import com.robertsoultanaev.javasphinx.Util;
import com.robertsoultanaev.javasphinx.packet.SphinxPacket;
import com.robertsoultanaev.javasphinx.packet.header.PacketContent;
import com.robertsoultanaev.javasphinx.routing.DestinationEncoding;
import com.robertsoultanaev.javasphinx.routing.MixNode;
import com.robertsoultanaev.javasphinx.routing.MixNodeRepository;
import com.robertsoultanaev.javasphinx.routing.OutwardMessage;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Base64;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class Endpoint {

    public static int PACKET_HEADER_SIZE = 24;

    private record RoutingInformation(byte[][] nodesRouting, ECPoint[] nodeKeys) {
    }

    private final SphinxClient client;
    private final MixNodeRepository mixNodeRepository;
    private final int numRouteNodes;

    public Endpoint(MixNodeRepository mixNodeRepository, int numRouteNodes, SphinxClient client) {
        this.mixNodeRepository = mixNodeRepository;
        this.client = client;
        this.numRouteNodes = numRouteNodes;
    }

    public SphinxPacket[] splitIntoSphinxPackets(OutwardMessage message) {
        UUID messageId = UUID.randomUUID();
        byte[] dest = DestinationEncoding.encode(message.address());

        int packetPayloadSize = client.getMaxPayloadSize() - dest.length - PACKET_HEADER_SIZE;
        int packetsInMessage = (int) Math.ceil((double) message.message().length / packetPayloadSize);
        SphinxPacket[] sphinxPackets = new SphinxPacket[packetsInMessage];

        for (int i = 0; i < packetsInMessage; i++) {

            ByteBuffer packetHeader = ByteBuffer.allocate(Endpoint.PACKET_HEADER_SIZE);
            packetHeader.putLong(messageId.getMostSignificantBits());
            packetHeader.putLong(messageId.getLeastSignificantBits());
            packetHeader.putInt(packetsInMessage);
            packetHeader.putInt(i);

            byte[] packetPayload = copyUpToNum(message.message(), packetPayloadSize * i, packetPayloadSize);
            byte[] sphinxPayload = Util.concatenate(packetHeader.array(), packetPayload);

            RoutingInformation routingInformation = generateRoutingInformation(this.numRouteNodes);

            sphinxPackets[i] = createSphinxPacket(dest, sphinxPayload, routingInformation);
        }

        return sphinxPackets;
    }

    private SphinxPacket createSphinxPacket(byte[] dest, byte[] message, RoutingInformation routingInformation) {
        PacketContent packetContent = client.createForwardMessage(routingInformation.nodesRouting, routingInformation.nodeKeys, dest, message);
        return client.createPacket(packetContent);
    }

    private RoutingInformation generateRoutingInformation(int numRouteNodes) {
        final byte[][] nodesRouting;
        final ECPoint[] nodeKeys;

        final var nodePool = mixNodeRepository.all().stream()
                .sorted(Comparator.comparingInt(MixNode::id))
                .mapToInt(MixNode::id)
                .toArray();
        int[] usedNodes = client.randSubset(nodePool, numRouteNodes);

        nodesRouting = new byte[usedNodes.length][];
        for (int i = 0; i < usedNodes.length; i++) {
            nodesRouting[i] = client.encodeNode(usedNodes[i]);
        }

        nodeKeys = new ECPoint[usedNodes.length];
        for (int i = 0; i < usedNodes.length; i++) {
            nodeKeys[i] = mixNodeRepository.byId(usedNodes[i]).publicKey();
        }

        return new RoutingInformation(nodesRouting, nodeKeys);
    }

    public AssembledMessage reassemble(List<Packet> packets) {
        String uuid = packets.get(0).uuid();
        byte[][] payloads = new byte[packets.size()][];
        for (int i = 0; i < packets.size(); i++) {
            payloads[i] = packets.get(i).payload();
        }
        byte[] message = Util.concatenate(payloads);

        return new AssembledMessage(uuid, message);
    }

    private byte[] copyUpToNum(byte[] source, int offset, int numBytes) {
        if (offset + numBytes > source.length) {
            numBytes = source.length - offset;
        }

        byte[] result = new byte[numBytes];
        System.arraycopy(source, offset, result, 0, numBytes);

        return result;
    }

    public Packet parseMessageToPacket(byte[] encodedMessage) {
        byte[] message = Base64.decode(encodedMessage);

        byte[] headerBytes = Arrays.copyOfRange(message, 0, Endpoint.PACKET_HEADER_SIZE);
        ByteBuffer byteBuffer = ByteBuffer.wrap(headerBytes);
        long uuidHigh = byteBuffer.getLong();
        long uuidLow = byteBuffer.getLong();

        int packetsInMessage = byteBuffer.getInt();
        int sequenceNumber = byteBuffer.getInt();
        String uuid = new UUID(uuidHigh, uuidLow).toString();
        byte[] payload = Arrays.copyOfRange(message, 24, message.length);

        return new Packet(uuid, sequenceNumber, packetsInMessage, payload);
    }
}
