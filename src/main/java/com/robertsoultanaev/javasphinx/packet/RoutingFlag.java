package com.robertsoultanaev.javasphinx.packet;

public enum RoutingFlag {
    DESTINATION(String.valueOf((char) 0xf0)),
    RELAY(String.valueOf((char) 0xf1)),
    SURB(String.valueOf((char) 0xf2));

    private final String value;

    RoutingFlag(final String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
