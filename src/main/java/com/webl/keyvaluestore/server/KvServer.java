package com.webl.keyvaluestore.server;

import com.webl.keyvaluestore.memtable.MemTable;
import com.webl.keyvaluestore.models.RespToken;
import com.webl.keyvaluestore.models.RespTokenType;
import org.tinylog.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;

public class KvServer implements RespParser {
    private MemTable memTable = null;

    public KvServer(int port, MemTable memTable) throws IOException {
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
            String res = execute(received);

            out.write(res.getBytes());
        }

        socket.close();
        in.close();
    }

    private String execute(String received) throws IOException {
        Iterator<RespToken> tokens = tokenize(received).iterator();
        while (tokens.hasNext()) {
            RespTokenType type = tokens.next().getTokenType();
            if (type == RespTokenType.Set) {
                this.memTable.insertKeyValue(tokens.next().getLiteral(), tokens.next().getLiteral());
                return "+OK\r\n";
            }

            if (type == RespTokenType.Get) {
                String val = this.memTable.getValue(tokens.next().getLiteral());
                return String.format("+%s\r\n", val);
            }

            if (type == RespTokenType.StringLiteral) {
                return "+OK\r\n";
            }
        }
        return "+OK\r\n";
    }
}
