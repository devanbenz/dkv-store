package com.webl.keyvaluestore.sstable;

import com.webl.keyvaluestore.memtable.MemTable;
import com.webl.keyvaluestore.observers.FlushObserver;

public class SSTable implements FlushObserver {
    @Override
    public void onFlush(MemTable memTable) {
        System.out.printf("%s\n", memTable.toString());
    }
}
