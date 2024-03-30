package com.webl.keyvaluestore.server;

import com.webl.keyvaluestore.memtable.MemTable;
import org.tinylog.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class KvServer implements ServerInterface {
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
            System.out.println(received);

            if (received.contains("set\r\n")) {
                this.memTable.insertKeyValue("test", "123");
            }

            if (received.contains("get\r\n")) {
                String val = this.memTable.getValue("test");
                out.write(String.format("+%s\r\n", val).getBytes());
            }

            String ok = "+OK\r\n";
            out.write(ok.getBytes());
        }

        socket.close();
        in.close();
    }
}
