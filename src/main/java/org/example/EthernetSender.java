package org.example;

import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EthernetSender {
    private static final Logger logger = Logger.getLogger(EthernetSender.class.getName());
    private Socket socket;
    private OutputStream outputStream;

    public void connect(String ipAddress, int port) throws Exception {
        socket = new Socket(ipAddress, port);
        outputStream = socket.getOutputStream();
        logger.log(Level.INFO, "Connected to printer at " + ipAddress + ":" + port);
    }

    public void send(byte[] data) throws Exception {
        if (outputStream == null) {
            throw new IllegalStateException("Connection not established.");
        }
        outputStream.write(data);
        outputStream.flush();
        logger.log(Level.INFO, "Data sent over Ethernet.");
    }

    public void disconnect() {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            logger.log(Level.INFO, "Disconnected from Ethernet connection.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error closing connection", e);
        }
    }

    public boolean isConnected() {
        return socket != null && !socket.isClosed() && outputStream != null;
    }
}