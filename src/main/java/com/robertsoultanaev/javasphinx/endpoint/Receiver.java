package com.robertsoultanaev.javasphinx.endpoint;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Receiver {

    private final Endpoint endpoint;
    private final Map<UUID, Set<Packet>> messageStore;
    private final ReassemblyHandler reassemblyHandler;

    public Receiver(final Endpoint endpoint, final ReassemblyHandler reassemblyHandler) {
        this.endpoint = endpoint;
        this.reassemblyHandler = reassemblyHandler;
        this.messageStore = new ConcurrentHashMap<>();
    }

    /**
     * Reeceives a packet and if this packet completes a message, the {@link ReassemblyHandler} is called
     * @param packet packet to receive
     */
    public void receive(Packet packet) {
        messageStore.putIfAbsent(packet.uuid(), Collections.synchronizedSet(new HashSet<>()));
        final var partialMessage = messageStore.get(packet.uuid());
        partialMessage.add(packet);
        if (packet.packetsInMessage() == partialMessage.size()) {
            final var assembledMessage = endpoint.reassemble(partialMessage);
            reassemblyHandler.onReassembly(assembledMessage);
        }
    }
}
