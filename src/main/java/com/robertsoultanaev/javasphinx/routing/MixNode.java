package com.robertsoultanaev.javasphinx.routing;

import com.robertsoultanaev.javasphinx.SphinxClient;
import com.robertsoultanaev.javasphinx.packet.SphinxPacket;
import org.bouncycastle.math.ec.ECPoint;

import java.io.IOException;
import java.net.Socket;

public record MixNode(int id, String host, int port, ECPoint publicKey) {

    public Socket openSocket() throws IOException {
        return new Socket(host, port);
    }

    /**
     * Sends a {@link SphinxPacket} via the given {@link SphinxClient}
     * @param client client to use for sending
     * @param packet packet to send
     * @throws IOException thrown if an error occurs opening the connection to the host
     */
    public void send(SphinxClient client, SphinxPacket packet) throws IOException {
        try (final var socket = new Socket(host, port)) {
            final var os = socket.getOutputStream();
            os.write(client.packMessage(packet));
        }
    }
}
