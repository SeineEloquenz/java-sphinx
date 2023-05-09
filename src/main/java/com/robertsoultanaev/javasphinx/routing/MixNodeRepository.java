package com.robertsoultanaev.javasphinx.routing;

import java.util.HashMap;
import java.util.Map;

public final class MixNodeRepository {
    private final Map<Integer, MixNode> nodes;

    public MixNodeRepository() {
        this.nodes = new HashMap<>();
    }

    public void put(int id, MixNode node) {
        nodes.put(id, node);
    }

    public MixNode byId(int nodeId) {
        return nodes.get(nodeId);
    }
}
