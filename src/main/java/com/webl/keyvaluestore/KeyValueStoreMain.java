package com.webl.keyvaluestore;

import com.webl.keyvaluestore.memtable.MemTable;
import com.webl.keyvaluestore.server.KvServer;
import com.webl.keyvaluestore.sstable.SSTable;
import com.webl.keyvaluestore.wal.WriteAheadLog;
import org.tinylog.Logger;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "dkv", description = "DKV key value store")
public class KeyValueStoreMain implements Callable<Integer> {
    @CommandLine.Option(names = {"-d", "--store-dir"}, description = "Directory for storage directory")
    private String storageDir = String.format("%s/store", Paths.get("").toAbsolutePath());

    @CommandLine.Option(names = {"-l", "--log-dir"}, description = "Directory for storage directory")
    private String logDir = String.format("%s/log", Paths.get("").toAbsolutePath());

    @CommandLine.Option(names = {"-p", "--port"}, description = "Port for server to listen on")
    private int port = 6379;

    @Override
    public Integer call() throws Exception {
        SSTable ssTable = new SSTable("sstable.dat");
        WriteAheadLog wal = new WriteAheadLog("wal.dat");
        MemTable memTable = new MemTable();

        try {
            memTable.registerSSTableObserver(ssTable);
            memTable.registerWalObserver(wal);

            new KvServer(this.port, memTable);
        } catch (IOException e) {
            Logger.error(e.getMessage());
            throw new IOException(e);
        }

        return 0;
    }

    public static void main(String ...args) {
        int exitCode = new CommandLine(new KeyValueStoreMain()).execute(args);
        System.exit(exitCode);
    }
}
