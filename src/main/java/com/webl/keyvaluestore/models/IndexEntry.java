package com.webl.keyvaluestore.models;

import java.io.Serializable;

public record IndexEntry(String ssTableName, long offset) implements Serializable {
}
