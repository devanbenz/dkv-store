package com.webl.keyvaluestore.models;



public class RespToken {
    RespTokenType tokenType;
    String literal;

    public RespToken(RespTokenType tokenType) {
        this.tokenType = tokenType;
    }

    public RespToken(RespTokenType tokenType, String literal) {
        this.tokenType = tokenType;
        this.literal = literal;
    }

    public RespTokenType getTokenType() {
        return this.tokenType;
    }

    public String getLiteral() {
        return this.literal;
    }
}
