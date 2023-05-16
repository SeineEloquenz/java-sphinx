package com.robertsoultanaev.javasphinx.routing;

import com.robertsoultanaev.javasphinx.coordinator.CoordinatorClient;
import com.robertsoultanaev.javasphinx.coordinator.CoordinatorConfig;

import java.io.IOException;
import java.util.Set;

public class CoordinatorMixNodeRepository implements MixNodeRepository {

    private final InMemoryMixNodeRepository mixNodeRepository;
    private final CoordinatorClient coordinatorClient;

    public CoordinatorMixNodeRepository(CoordinatorConfig config) {
        this.mixNodeRepository = new InMemoryMixNodeRepository();
        this.coordinatorClient = new CoordinatorClient(config);
    }

    public void sync() throws IOException {
        final var mixes = coordinatorClient.getAllMixes();
        mixes.forEach(mix -> mixNodeRepository.put(mix.id(), mix));
    }

    @Override
    public MixNode byId(final int nodeId) {
        return mixNodeRepository.byId(nodeId);
    }

    @Override
    public Set<MixNode> all() {
        return mixNodeRepository.all();
    }
}