package com.webl.keyvaluestore.observers;

import com.webl.keyvaluestore.memtable.MemTable;

public interface FlushObserver {
    void onFlush(MemTable memTable);
}
