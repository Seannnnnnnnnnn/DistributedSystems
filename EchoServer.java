/*
 Okay, we're going to need a bit of theory first. Remember that a TCP connection is always between
 a client and a server, with each end having an address and port:
 +------+                                                    +------+
 |Client|  198.51.100.106:39482 -----------> 233.252.0.70:80 |Server|
 +------+                                                    +------+
 Each of the client and server will use a Socket to encapsulate the details of the connection.
 */
package com.distributedsystems;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class EchoServer {

    public static void main(String[] args) {
        server();
        client();
    }

    static void client() {
        /*
        Let's go ahead and make a client socket for initiating ICP with a server. To construct the
        socket we must specify the remote address and port no. of the server we wish to connect to.
        Remember that these must be known a priori.
        */
        String remoteHostName = "google.com";
        int remotePort = 80;
        try {
            Socket socket = new Socket(remoteHostName, remotePort);   // this binds host name and port
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            /*
            From here, reading and writing data is just like reading and writing to a console. Instead
            we are just writing to a server.
            */
            writer.print("GET / HTTP/1.1\nHost: google.com\n\n");
            writer.flush();
            String response = reader.readLine();
            System.out.format("Response from server: %s", response);

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            // try to figure out how to put the socket.close in here:
            System.out.println("Connect to server terminated");
        }
    }

    static void server() {
        /*
         Servers also use Sockets to read and write data to clients, but they setup things a little differently.
         Instead of creating a Socket directly, we want to listen for any incoming connections from a client.
         To listen for connections we use a ServerSocket. Don't be confused by the name! This is quite a different
         object to a Socket, and has a different purpose.
         To make a ServerSocket, we need to tell it what port it should listen on. This is the port clients will
         connect to (e.g. 80 for a web server).
         */
        int listeningPort = 2613;
        try {
            ServerSocket serverSocket = new ServerSocket(listeningPort);
            System.out.format("listening on port: %d\n", listeningPort);

            /*
             So far we haven't connected to anything, we've just told the operating system that we're interested in
             TCP connections arriving on port 2612.
             Next we ask the operating system to accept an incoming connection if there is one.
             */
            while (true) {
                Socket socket = serverSocket.accept();        // accept just blocks until someone connects to serverSocket
                PrintWriter writer = new PrintWriter(socket.getOutputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                /*
                 All we'll do is read a line from the client and send it back reversed:
                 */
                String dataIn = reader.readLine();
                writer.println(reverse(dataIn));
                writer.flush();
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String reverse(String s) {
        String output = "";
        for (int i=s.length()-1; i>=0; i--){
            output = output + s.charAt(i);
        }
        return output;
    }
}
