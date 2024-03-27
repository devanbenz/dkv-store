package com.webl.keyvaluestore;

import com.webl.keyvaluestore.memtable.MemTable;
import com.webl.keyvaluestore.sstable.SSTable;
import com.webl.keyvaluestore.wal.WriteAheadLog;
import org.tinylog.Logger;

import java.io.IOException;

public class KeyValueStoreMain {
    public static void main(String[] args) throws IOException {
        WriteAheadLog wal = new WriteAheadLog("wal.dat");
        MemTable memTable = new MemTable();
        SSTable ssTable = new SSTable("sstable.dat");

        memTable.insertKeyValue("a", "b");
        wal.writeLog("a", "b");
        memTable.insertKeyValue("1", "2");
        wal.writeLog("1", "2");

        MemTable memTable2 = new MemTable(wal.readLog());
        String valueFromLoadedWal = memTable2.getValue("a");

        Logger.info(String.format("from loaded file %s", valueFromLoadedWal));

        memTable2.registerSSTableObserver(ssTable);
        memTable2.flushToDisk();

        wal.clearLog();
        wal.closeWriter();
    }
}
