import com.robertsoultanaev.javasphinx.SphinxClient;
import com.robertsoultanaev.javasphinx.SphinxException;
import com.robertsoultanaev.javasphinx.SphinxNode;
import com.robertsoultanaev.javasphinx.SphinxParams;
import com.robertsoultanaev.javasphinx.crypto.ECCGroup;
import com.robertsoultanaev.javasphinx.packet.ProcessedPacket;
import com.robertsoultanaev.javasphinx.packet.RoutingFlag;
import com.robertsoultanaev.javasphinx.packet.SphinxPacket;
import com.robertsoultanaev.javasphinx.packet.header.PacketContent;
import com.robertsoultanaev.javasphinx.packet.message.DestinationAndMessage;
import com.robertsoultanaev.javasphinx.packet.reply.SingleUseReplyBlock;
import com.robertsoultanaev.javasphinx.pki.PkiEntry;
import com.robertsoultanaev.javasphinx.routing.RandomRoutingStrategy;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Random;

import static org.junit.Assert.*;

public class SphinxClientTest {

    private SphinxParams params;
    private SphinxClient client;
    private HashMap<Integer, PkiEntry> pkiPriv;
    private byte[][] nodesRouting;
    private ECPoint[] nodeKeys;
    private int[] useNodes;

    @Before
    public void setUp() throws SphinxException {
        params = new SphinxParams();
        client = new SphinxClient(params, new RandomRoutingStrategy());

        int r = 5;

        pkiPriv = new HashMap<>();
        HashMap<Integer, PkiEntry> pkiPub = new HashMap<>();

        for (int i = 0; i < 10; i++) {
            BigInteger x = params.getGroup().genSecret();
            ECPoint y = params.getGroup().expon(params.getGroup().getGenerator(), x);

            PkiEntry privEntry = new PkiEntry(x, y);
            PkiEntry pubEntry = new PkiEntry(null, y);

            pkiPriv.put(i, privEntry);
            pkiPub.put(i, pubEntry);
        }

        Integer[] pubKeys = pkiPub.keySet().toArray(new Integer[0]);
        int[] nodePool = new int[pubKeys.length];
        for (int i = 0; i < nodePool.length; i++) {
            nodePool[i] = pubKeys[i];
        }
        useNodes = client.route(nodePool, r);

        nodesRouting = new byte[useNodes.length][];
        for (int i = 0; i < useNodes.length; i++) {
            nodesRouting[i] = client.encodeNode(useNodes[i], (new Random()).nextLong());
        }

        nodeKeys = new ECPoint[useNodes.length];
        for (int i = 0; i < useNodes.length; i++) {
            nodeKeys[i] = pkiPub.get(useNodes[i]).pub();
        }
    }

    @Test
    public void encodeAndDecode() throws SphinxException {
        byte[] dest = "bob".getBytes();
        byte[] message = "this is a test".getBytes();
        PacketContent packetContent = client.createForwardMessage(nodesRouting, nodeKeys, dest, message);

        SphinxPacket sphinxPacket = new SphinxPacket(params, packetContent);

        byte[] binMessage = client.packMessage(sphinxPacket);
        SphinxPacket unpackedSphinxPacket = client.unpackMessage(binMessage);
        PacketContent unpackedPacketContent = unpackedSphinxPacket.packetContent();

        assertEquals(params.headerLength(), unpackedSphinxPacket.headerLength());
        assertEquals(params.bodyLength(), unpackedSphinxPacket.bodyLength());

        assertEquals(packetContent.header().alpha(), unpackedPacketContent.header().alpha());
        assertArrayEquals(packetContent.header().beta(), unpackedPacketContent.header().beta());
        assertArrayEquals(packetContent.header().gamma(), unpackedPacketContent.header().gamma());
        assertArrayEquals(packetContent.delta(), unpackedPacketContent.delta());
    }

    @Test
    public void encodeAndDecodeMaxMessageLength() throws SphinxException {
        byte[] dest = "bob".getBytes();
        byte[] message = new byte[client.getMaxPayloadSize() - dest.length];
        Arrays.fill(message, (byte) 0xaa);

        PacketContent packetContent = client.createForwardMessage(nodesRouting, nodeKeys, dest, message);

        SphinxPacket sphinxPacket = new SphinxPacket(params, packetContent);

        byte[] binMessage = client.packMessage(sphinxPacket);
        SphinxPacket unpackedSphinxPacket = client.unpackMessage(binMessage);
        PacketContent unpackedPacketContent = unpackedSphinxPacket.packetContent();

        assertEquals(params.headerLength(), unpackedSphinxPacket.headerLength());
        assertEquals(params.bodyLength(), unpackedSphinxPacket.bodyLength());

        assertEquals(packetContent.header().alpha(), unpackedPacketContent.header().alpha());
        assertArrayEquals(packetContent.header().beta(), unpackedPacketContent.header().beta());
        assertArrayEquals(packetContent.header().gamma(), unpackedPacketContent.header().gamma());
        assertArrayEquals(packetContent.delta(), unpackedPacketContent.delta());
    }

    @Test
    public void routeSphinxMessage() throws Exception {
        byte[] dest = "bob".getBytes();
        byte[] message = "this is a test".getBytes();

        PacketContent packetContent = client.createForwardMessage(nodesRouting, nodeKeys, dest, message);

        BigInteger firstNodeKey = pkiPriv.get(useNodes[0]).priv();

        testRouting(params, packetContent, firstNodeKey, dest, message);
    }

    @Test
    public void routeSphinxMessageMaxMessageLength() throws Exception {
        byte[] dest = "bob".getBytes();
        byte[] message = new byte[client.getMaxPayloadSize() - dest.length];
        Arrays.fill(message, (byte) 0xaa);

        PacketContent packetContent = client.createForwardMessage(nodesRouting, nodeKeys, dest, message);

        BigInteger firstNodeKey = pkiPriv.get(useNodes[0]).priv();

        testRouting(params, packetContent, firstNodeKey, dest, message);
    }

    @Test
    public void routeSphinxMessageNonDefaultBodySize() throws Exception {
        SphinxParams params = new SphinxParams(16, 4096, 192, new ECCGroup());
        SphinxClient client = new SphinxClient(params, new RandomRoutingStrategy());

        byte[] dest = "bob".getBytes();
        byte[] message = new byte[client.getMaxPayloadSize() - dest.length];
        Arrays.fill(message, (byte) 0xaa);

        PacketContent packetContent = client.createForwardMessage(nodesRouting, nodeKeys, dest, message);

        BigInteger firstNodeKey = pkiPriv.get(useNodes[0]).priv();

        testRouting(params, packetContent, firstNodeKey, dest, message);
    }

    private void testRouting(SphinxParams params, PacketContent packetContent, BigInteger firstNodeKey, byte[] dest, byte[] message) throws Exception {
        BigInteger currentNodeKey = firstNodeKey;
        MessageUnpacker unpacker;

        while (true) {
            final var node = new SphinxNode(params, new RandomRoutingStrategy(), currentNodeKey);
            ProcessedPacket ret = node.sphinxProcess(packetContent);
            packetContent = ret.packetContent();

            byte[] encodedRouting = ret.routing();

            unpacker = MessagePack.newDefaultUnpacker(encodedRouting);
            int routingLen = unpacker.unpackArrayHeader();
            String flag = unpacker.unpackString();

            assertTrue(flag.equals(RoutingFlag.RELAY.value()) || flag.equals(RoutingFlag.DESTINATION.value()));

            if (flag.equals(RoutingFlag.RELAY.value())) {
                int addr = unpacker.unpackInt();
                long id = unpacker.unpackLong();
                currentNodeKey = pkiPriv.get(addr).priv();

                unpacker.close();
            } else if (flag.equals(RoutingFlag.DESTINATION.value())) {
                unpacker.close();

                assertEquals(1, routingLen);

                DestinationAndMessage destAndMsg = client.receiveForward(ret.macKey(), ret.packetContent().delta());

                assertArrayEquals(dest, destAndMsg.destination());
                assertArrayEquals(message, destAndMsg.message());

                break;
            }
        }
    }

    @Test
    public void routeSurb() throws Exception {
        byte[] surbDest = "myself".getBytes();
        byte[] message = "This is a reply".getBytes();

        SingleUseReplyBlock surb = client.createSurb(nodesRouting, nodeKeys, surbDest);
        PacketContent packetContent = client.packageSurb(surb.nymTuple(), message);

        BigInteger x = pkiPriv.get(useNodes[0]).priv();
        MessageUnpacker unpacker;

        while (true) {
            final var node = new SphinxNode(params, new RandomRoutingStrategy(), x);
            ProcessedPacket ret = node.sphinxProcess(packetContent);
            packetContent = ret.packetContent();

            byte[] encodedRouting = ret.routing();

            unpacker = MessagePack.newDefaultUnpacker(encodedRouting);
            unpacker.unpackArrayHeader();
            String flag = unpacker.unpackString();

            assertTrue(flag.equals(RoutingFlag.RELAY.value()) || flag.equals(RoutingFlag.SURB.value()));

            if (flag.equals(RoutingFlag.RELAY.value())) {
                int addr = unpacker.unpackInt();
                long id = unpacker.unpackLong();
                x = pkiPriv.get(addr).priv();

                unpacker.close();
            } else if (flag.equals(RoutingFlag.SURB.value())) {
                int destLength = unpacker.unpackBinaryHeader();
                byte[] finalDest = unpacker.readPayload(destLength);
                int surbIdLength = unpacker.unpackBinaryHeader();
                byte[] finalSurbId = unpacker.readPayload(surbIdLength);
                unpacker.close();

                assertArrayEquals(surbDest, finalDest);
                assertArrayEquals(surb.xid(), finalSurbId);

                byte[] received = client.receiveSurb(surb.keyTuple(), packetContent.delta());
                assertArrayEquals(message, received);

                break;
            }
        }
    }

    @Test(expected = SphinxException.class)
    public void randSubsetBadNu() throws SphinxException {
        int[] nodePool = {0,0,0,0,0};
        client.route(nodePool, nodePool.length + 1);
    }

    @Test(expected = SphinxException.class)
    public void receiveForwardCorruptedPayload() throws Exception {
        byte[] dest = "bob".getBytes();
        byte[] message = "this is a test".getBytes();

        PacketContent packetContent = client.createForwardMessage(nodesRouting, nodeKeys, dest, message);

        BigInteger x = pkiPriv.get(useNodes[0]).priv();

        MessageUnpacker unpacker;

        while (true) {
            final var node = new SphinxNode(params, new RandomRoutingStrategy(), x);
            ProcessedPacket ret = node.sphinxProcess(packetContent);
            packetContent = ret.packetContent();

            byte[] encodedRouting = ret.routing();

            unpacker = MessagePack.newDefaultUnpacker(encodedRouting);
            int routingLen = unpacker.unpackArrayHeader();
            String flag = unpacker.unpackString();

            assertTrue(flag.equals(RoutingFlag.RELAY.value()) || flag.equals(RoutingFlag.DESTINATION.value()));

            if (flag.equals(RoutingFlag.RELAY.value())) {
                final var addr = unpacker.unpackInt();
                final var id = unpacker.unpackLong();
                x = pkiPriv.get(addr).priv();

                unpacker.close();
            } else if (flag.equals(RoutingFlag.DESTINATION.value())) {
                unpacker.close();

                assertEquals(1, routingLen);

                // Corrupt payload
                ret.packetContent().delta()[20]++;

                DestinationAndMessage destAndMsg = client.receiveForward(ret.macKey(), ret.packetContent().delta());

                assertArrayEquals(dest, destAndMsg.destination());
                assertArrayEquals(message, destAndMsg.message());

                break;
            }
        }
    }

    @Test(expected = SphinxException.class)
    public void receiveSurbBadDelta() throws SphinxException {
        byte[] surbDest = "myself".getBytes();
        byte[] message = "This is a reply".getBytes();

        SingleUseReplyBlock surb = client.createSurb(nodesRouting, nodeKeys, surbDest);
        PacketContent packetContent = client.packageSurb(surb.nymTuple(), message);
        packetContent.delta()[0] = 1;
        client.receiveSurb(surb.keyTuple(), packetContent.delta());
    }

    @Test(expected = SphinxException.class)
    public void createForwardDestTooLong() throws SphinxException {
        byte[] dest = new byte[SphinxClient.MAX_DEST_SIZE + 1];
        byte[] message = "this is a test".getBytes();

        client.createForwardMessage(nodesRouting, nodeKeys, dest, message);
    }

    @Test(expected = SphinxException.class)
    public void createForwardDestAndMessageTooLong() throws SphinxException {
        byte[] dest = "bob".getBytes();
        byte[] message = new byte[(client.getMaxPayloadSize() - dest.length) + 1];

        client.createForwardMessage(nodesRouting, nodeKeys, dest, message);
    }
}
