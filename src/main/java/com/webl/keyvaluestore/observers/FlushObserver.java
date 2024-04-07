package com.webl.keyvaluestore.observers;

import com.webl.keyvaluestore.indexes.Indexes;
import com.webl.keyvaluestore.models.IndexEntry;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public interface FlushObserver {
    void onFlush(TreeMap<String, String> treeMap, Indexes indexes) throws IOException;
}
