package com.robertsoultanaev.javasphinx;

import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Base64;

/**
 * Client used for conformance testing
 */
public class ConformanceClient {
    public static void main(String[] args) throws Exception {
        final var params = new SphinxParams();
        final var client = new SphinxClient(params);
        byte[] dest = Base64.decode(args[0]);
        byte[] message = Base64.decode(args[1]);

        int numNodes = args.length - 2;
        byte[][] nodesRouting = new byte[numNodes][];
        ECPoint[] nodeKeys = new ECPoint[numNodes];

        for (int i = 0; i < numNodes; i++) {
            String[] split = args[2 + i].split(":");
            int nodeId = Integer.parseInt(split[0]);
            nodesRouting[i] = client.encodeNode(nodeId);

            byte[] encodedKey = Base64.decode(split[1]);
            nodeKeys[i] = Util.decodeECPoint(encodedKey);
        }

        DestinationAndMessage destinationAndMessage = new DestinationAndMessage(dest, message);
        HeaderAndDelta headerAndDelta = client.createForwardMessage(nodesRouting, nodeKeys, destinationAndMessage);
        SphinxPacket sphinxPacket = new SphinxPacket(params, headerAndDelta);
        byte[] binMessage = client.packMessage(sphinxPacket);

        System.out.write(binMessage);
    }
}
