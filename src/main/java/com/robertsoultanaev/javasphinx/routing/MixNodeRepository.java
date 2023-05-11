package com.robertsoultanaev.javasphinx.routing;

import java.util.Set;

public interface MixNodeRepository {
    MixNode byId(int nodeId);

    Set<MixNode> all();
}
