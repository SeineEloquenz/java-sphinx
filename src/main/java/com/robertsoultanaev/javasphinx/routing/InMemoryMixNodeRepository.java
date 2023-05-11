package com.robertsoultanaev.javasphinx.routing;

import java.util.HashMap;
import java.util.Map;

public final class InMemoryMixNodeRepository implements MixNodeRepository {
    private final Map<Integer, MixNode> nodes;

    public InMemoryMixNodeRepository() {
        this.nodes = new HashMap<>();
    }

    public void put(int id, MixNode node) {
        nodes.put(id, node);
    }

    @Override
    public MixNode byId(int nodeId) {
        return nodes.get(nodeId);
    }
}
