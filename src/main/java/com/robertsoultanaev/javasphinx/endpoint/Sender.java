package com.robertsoultanaev.javasphinx.endpoint;

import com.robertsoultanaev.javasphinx.SphinxClient;
import com.robertsoultanaev.javasphinx.routing.OutwardMessage;

import java.io.IOException;

public class Sender {

    private final Endpoint endpoint;
    private final SphinxClient client;

    public Sender(final Endpoint endpoint, final SphinxClient client) {
        this.endpoint = endpoint;
        this.client = client;
    }

    public void send(OutwardMessage message) throws IOException {
        final var packets = endpoint.splitIntoSphinxPackets(message);
        for (final var entry : packets.entrySet()) {
            entry.getValue().send(client, entry.getKey());
        }
    }
}
