package com.danielpclin;

import java.io.*;
import java.net.*;

public class Client {

    private String serverName = null;
    private int serverPort = 0;

    public Client(String name, int port) {
        serverName = name;
        serverPort = port;
    }

    public void start(){
        //set server address
        SocketAddress severSocketAddress = new InetSocketAddress(serverName, serverPort);

        try(Socket clientSocket = new Socket()) {
            //connect to server in the specific timeout 3000 ms
            System.out.println("Connecting to server " + serverName + ":" + serverPort);
            clientSocket.connect(severSocketAddress, 3000);

            //get client address and port at local host
            InetSocketAddress socketAddress = (InetSocketAddress)clientSocket.getLocalSocketAddress();
            String clientAddress = socketAddress.getAddress().getHostAddress();
            int clientPort = socketAddress.getPort();
            System.out.println("Client " + clientAddress + ":" + clientPort);
            System.out.println("Connected to server " + serverName + ":" + serverPort);

            try {
                InputStream inputStream = clientSocket.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream();
                //create a thread to read server's output(which is client's input)
                Thread task = new Thread(new ListeningTask(inputStream));
                task.start();

                //read keyboard message into a buf and write the message to the server
                byte[] buf = new byte[1024];
                int length = System.in.read(buf);
                //length = -1 if there is no more data
                while(length > 0) {
                    outputStream.write(buf, 0, length);
                    outputStream.flush();
                    length = System.in.read(buf);
                }

            } catch(IOException e) {
                e.printStackTrace();
            }

        } catch (ConnectException e) {
            System.out.println("Connection failed");
        } catch (IOException e) {
             e.printStackTrace();
        }
    }

    private class ListeningTask implements Runnable {
        private InputStream inputStream;

        public ListeningTask(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            //read server's output(which is client's input) into buf and write it to System
            byte[] buf = new byte[1024];
            try {
                int length = inputStream.read(buf);
                while(length > 0) {
                    System.out.write(buf, 0, length);
                    length = inputStream.read(buf);
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
        //Default server address is 127.0.0.1:12000		
        String serverName = "127.0.0.1";
        int serverPort = 12000;

        if(args.length >= 2) {
            serverName = args[0];
            try {
                serverPort = Integer.parseInt(args[1]);
            } catch(NumberFormatException e) {}
        }

        Client client = new Client(serverName, serverPort);
        client.start();
    }

}