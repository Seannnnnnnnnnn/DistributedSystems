package com.distributedsystems;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MultiThreadServer {
    /*
    chat server that can accept multiple connections on port 6739.
    upon receiving a message, we broadcast to all other connected clients
    handles leaving/joining without provoking an exception.
     */
    static final int standardPort = 6379;
    static final boolean alive = true;
    static private final List<ChatConnection> connectionList = new ArrayList<>();

    public static void main(String[] args) {
        multithreadServer();
    }

    static synchronized void leave(ChatConnection connection) {
        broadcast(String.format("%d has left the chat\n", connection.socket.getPort()), connection);
        connectionList.remove(connection);
    }

    static synchronized void join(ChatConnection connection) {
        System.out.format("%d has joined the chat\n", connection.socket.getPort());
        broadcast(String.format("%d has joined the chat\n", connection.socket.getPort()), connection);
        connectionList.add(connection);
    }

    static void broadcast(String message, ChatConnection sender) {
        // broadcasts message to all but sender
        System.out.format("%d : "+message+"\n", sender.socket.getPort());
        for (ChatConnection connection: connectionList) {
            if (!sender.equals(connection)) {
                connection.sendMessage(message+"\n");
            }
        }
    }

    static void multithreadServer() {
        ServerSocket serverSocket;

        try {
            serverSocket = new ServerSocket(standardPort);
            System.out.format("Listing on port: %d\n", standardPort);

            while (alive) {
                Socket clientSocket = serverSocket.accept();

                // as we are now handling multiple connections, we hand this socket off
                ChatConnection connection = new ChatConnection(clientSocket);
                Thread connectionThread = new Thread(connection);
                connectionThread.start();
                join(connection);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ChatConnection extends Thread{
        private Socket socket;
        private BufferedReader reader;
        private PrintWriter writer;
        private boolean connectionAlive = true;

        public ChatConnection(Socket socket) throws IOException{
            this.socket = socket;
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new PrintWriter(socket.getOutputStream());
        }

        public void sendMessage(String message) {
            writer.print(message);
            writer.flush();
        }

        public void close() {
            try {
                leave(this);
                reader.close();
                writer.close();
                socket.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        @Override
        public void run() {
            // here we manage connection with client
            while (connectionAlive) {
                try {
                    String input = reader.readLine();
                    // we now broadcast this to everyone else
                    if (input != null) {
                        broadcast(input, this);
                    } else {
                        connectionAlive = false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            connectionAlive = false;
            close();
        }
    }
}
