package com.webl.keyvaluestore.server;

import com.webl.keyvaluestore.models.RespToken;
import com.webl.keyvaluestore.models.RespTokenType;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public interface RespParser {
    default List<RespToken> tokenize(String received) {
        List<RespToken> tokens = new ArrayList<>();
        CharacterIterator parts = new StringCharacterIterator(received);
        for (char c = parts.next(); c != CharacterIterator.DONE; c = parts.next()) {
            switch (c) {
                case '$':
                    StringBuilder literalBuilder = new StringBuilder();

                    // skip '\r\n'
                    parts.next();
                    parts.next();
                    parts.next();

                    for (c = parts.next(); c != '\r'; c = parts.next()) {
                        literalBuilder.append(c);
                    }

                    String result = literalBuilder.toString();
                    RespToken token = getTokenType(result);
                    tokens.add(token);

                    break;
                default:
                    break;
            }
        }

        return tokens;
    }

    private RespToken getTokenType(String literal) {
        return switch (literal.toLowerCase()) {
            case "get" -> new RespToken(RespTokenType.Get);
            case "set" -> new RespToken(RespTokenType.Set);
            default -> new RespToken(RespTokenType.StringLiteral, literal);
        };
    }
}
