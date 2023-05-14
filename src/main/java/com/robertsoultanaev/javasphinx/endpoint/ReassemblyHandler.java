package com.robertsoultanaev.javasphinx.endpoint;

@FunctionalInterface
public interface ReassemblyHandler {
    void onReassembly(AssembledMessage message);
}
