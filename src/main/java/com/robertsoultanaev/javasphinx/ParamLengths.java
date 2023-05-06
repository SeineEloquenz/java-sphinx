package com.robertsoultanaev.javasphinx;

/**
 * Type to combine the header and body length parameters
 */
public record ParamLengths(int headerLength, int bodyLength) {

    public ParamLengths(SphinxParams params) {
        this(params.getHeaderLength(), params.getBodyLength());
    }
}
