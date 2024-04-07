package com.webl.keyvaluestore.server;

import com.webl.keyvaluestore.indexes.Indexes;
import com.webl.keyvaluestore.memtable.MemTable;
import com.webl.keyvaluestore.models.IndexEntry;
import com.webl.keyvaluestore.models.RespToken;
import com.webl.keyvaluestore.models.RespTokenType;
import com.webl.keyvaluestore.sstable.SSTable;
import org.tinylog.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;

public class KvServer implements RespParser {
    private final MemTable memTable;

    public KvServer(int port, MemTable memTable, Indexes indexes, SSTable ssTable) throws IOException {
        this.memTable = memTable;

        ServerSocket server = new ServerSocket(port);
        Logger.info(String.format("starting server on port %s...", port));

        Socket socket = server.accept();
        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();

        byte[] buffer = new byte[2046];
        int bytesRead;

        while ((bytesRead = in.read(buffer)) != -1) {
            String received = new String(buffer, 0, bytesRead);

            try {
                String res = execute(received, indexes, ssTable);
                out.write(res.getBytes());

            } catch (IOException e) {
                Logger.error(e.getMessage());
                out.write(String.format("-%s\r\n", e.getMessage()).getBytes());
            }

            if (memTable.getSize() > 3) {
                memTable.flushToDisk(indexes);
            }
        }

        socket.close();
        in.close();
    }

    private String execute(String received, Indexes indexes, SSTable ssTable) throws IOException {
        Iterator<RespToken> tokens = tokenize(received).iterator();
        while (tokens.hasNext()) {
            RespTokenType type = tokens.next().getTokenType();
            if (type == RespTokenType.Set) {
                String key;
                String value;
                try {
                    key = tokens.next().getLiteral();
                } catch (Exception e) {
                    throw new IOException("Could not set key");
                }

                try {
                    value = tokens.next().getLiteral();
                } catch (Exception e) {
                    throw new IOException("Could not set value");
                }

                this.memTable.insertKeyValue(key, value);
                return "+OK\r\n";
            }

            if (type == RespTokenType.Get) {
                String key;
                try {
                    key = tokens.next().getLiteral();
                } catch (Exception e) {
                    throw new IOException("Could not get key");
                }

                String val = this.memTable.getValue(key);
                if (val == null) {
                    IndexEntry indexEntry = indexes.idx.get(key);
                    if (indexEntry != null) {
                        try {
                            val = ssTable.readFromSSTable(indexEntry);
                        } catch (IOException e) {
                            Logger.error(e.getMessage());
                            return "-%s\r\n";
                        }
                    }
                }

                return String.format("+%s\r\n", val);
            }

            if (type == RespTokenType.StringLiteral) {
                return "+OK\r\n";
            }
        }
        return "+OK\r\n";
    }
}
