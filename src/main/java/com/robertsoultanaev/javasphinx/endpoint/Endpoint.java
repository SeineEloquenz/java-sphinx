package com.robertsoultanaev.javasphinx.endpoint;

import com.robertsoultanaev.javasphinx.SerializationUtils;
import com.robertsoultanaev.javasphinx.SphinxClient;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Endpoint {

    public static int PACKET_HEADER_SIZE = 24;

    private record RoutingInformation(byte[][] nodesRouting, ECPoint[] nodeKeys, int firstNodeId) {
    }

    private final SphinxClient client;
    private final MixNodeRepository mixNodeRepository;
    private final int numRouteNodes;

    public Endpoint(MixNodeRepository mixNodeRepository, int numRouteNodes, SphinxClient client) {
        this.mixNodeRepository = mixNodeRepository;
        this.client = client;
        this.numRouteNodes = numRouteNodes;
    }

    public Map<SphinxPacket, MixNode> splitIntoSphinxPackets(OutwardMessage message) {
        UUID messageId = UUID.randomUUID();
        byte[] dest = DestinationEncoding.encode(message.address());

        final var packetPayloadSize = client.getMaxPayloadSize() - dest.length - PACKET_HEADER_SIZE;
        final var packetsInMessage = (int) Math.ceil((double) message.message().length / packetPayloadSize);
        final var sphinxPackets = new HashMap<SphinxPacket, MixNode>();

        for (int i = 0; i < packetsInMessage; i++) {
            final var packetHeader = ByteBuffer.allocate(Endpoint.PACKET_HEADER_SIZE);
            packetHeader.putLong(messageId.getMostSignificantBits());
            packetHeader.putLong(messageId.getLeastSignificantBits());
            packetHeader.putInt(packetsInMessage);
            packetHeader.putInt(i);

            byte[] packetPayload = copyUpToNum(message.message(), packetPayloadSize * i, packetPayloadSize);
            byte[] sphinxPayload = SerializationUtils.concatenate(packetHeader.array(), packetPayload);

            RoutingInformation routingInformation = generateRoutingInformation(this.numRouteNodes);

            final var targetMix = mixNodeRepository.byId(routingInformation.firstNodeId());
            sphinxPackets.put(createSphinxPacket(dest, sphinxPayload, routingInformation), targetMix);
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

        return new RoutingInformation(nodesRouting, nodeKeys, usedNodes[0]);
    }

    /**
     * Reassemble an {@link AssembledMessage} from received {@link Packet}s
     * @param packets received packets, may not be empty
     * @return the assembled message
     */
    public AssembledMessage reassemble(Set<Packet> packets) {
        assert packets.size() != 0;
        final var uuid = packets.stream().findAny().get().uuid();
        byte[][] payloads = new byte[packets.size()][];
        packets.stream()
                .sorted(Comparator.comparingInt(Packet::sequenceNumber))
                .forEach(packet -> payloads[packet.sequenceNumber()] = packet.payload());
        byte[] message = SerializationUtils.concatenate(payloads);

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
        final var uuid = new UUID(uuidHigh, uuidLow);
        byte[] payload = Arrays.copyOfRange(message, 24, message.length);

        return new Packet(uuid, sequenceNumber, packetsInMessage, payload);
    }
}
