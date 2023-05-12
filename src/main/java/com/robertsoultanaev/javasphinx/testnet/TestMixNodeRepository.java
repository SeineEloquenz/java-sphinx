package com.robertsoultanaev.javasphinx.testnet;

import com.robertsoultanaev.javasphinx.SerializationUtils;
import com.robertsoultanaev.javasphinx.routing.InMemoryMixNodeRepository;
import com.robertsoultanaev.javasphinx.routing.MixNode;
import com.robertsoultanaev.javasphinx.routing.MixNodeRepository;

import java.util.Set;

public class TestMixNodeRepository implements MixNodeRepository {

    private final InMemoryMixNodeRepository backend;

    public TestMixNodeRepository() {
        this.backend = new InMemoryMixNodeRepository();
        backend.put(0, buildNode(0, "mix0", "ApBDNYe3z9m+lLJ7EOYDsvfvdHSjsjPMV+ZLvwA="));
        backend.put(0, buildNode(1, "mix1", "AijlyfUMqyCEN//rOha2f1snYYRTkrjGZzChuGA="));
        backend.put(0, buildNode(2, "mix2", "A5CQCatbjAYIhdfcw2NXpD0rCloJxe4oX/KT3a8="));
    }

    @Override
    public MixNode byId(final int i) {
        return null;
    }

    @Override
    public Set<MixNode> all() {
        return backend.all();
    }

    private MixNode buildNode(final int id, final String address, String pubKey) {
        return new MixNode(id, address, 8888, SerializationUtils.decodeECPoint(SerializationUtils.base64decode(pubKey)));
    }
}
