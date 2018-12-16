package com.danielpclin;

import com.danielpclin.helpers.Broadcastable;

import java.io.*;
import java.net.*;
import java.util.function.Consumer;

public class Client implements Runnable, Broadcastable {

    private String serverName;
    private int serverPort;
    private Socket clientSocket;
    private Consumer<String> messageFunction;

    public Client(String name, int port, Consumer<String> function) {
        serverName = name;
        serverPort = port;
        messageFunction = function;
    }

    public Client(String name, int port) {
        this(name, port, (msg)->{});
    }

    public Client(Consumer<String> function) {
        this("127.0.0.1", 12000, function);
    }

    public Client(int port, Consumer<String> function) {
        this("127.0.0.1", port, function);
    }

    @Override
    public void run(){
        //set server address
        SocketAddress severSocketAddress = new InetSocketAddress(serverName, serverPort);
        try {
            Socket clientSocket = new Socket();
            //connect to server in the specific timeout 3000 ms
            System.out.println("Connecting to server " + serverName + ":" + serverPort);
            clientSocket.connect(severSocketAddress, 3000);
            System.out.println(clientSocket);
            this.clientSocket = clientSocket;

            //get client address and port at local host
            InetSocketAddress socketAddress = (InetSocketAddress)clientSocket.getLocalSocketAddress();
            String clientAddress = socketAddress.getAddress().getHostAddress();
            int clientPort = socketAddress.getPort();
            System.out.println("Client " + clientAddress + ":" + clientPort);
            System.out.println("Connected to server " + serverName + ":" + serverPort);

            try {
                InputStream inputStream = clientSocket.getInputStream();
                //create a thread to read server's output(which is client's input)
                Thread task = new Thread(new ListeningTask(inputStream));
                task.start();
            } catch(IOException e) {
                System.out.println("Server closed connection (unexpectedly) - WRITE");
            }
        } catch (ConnectException e) {
            System.out.println("Connection failed");
        } catch (IOException e) {
             e.printStackTrace();
        }
    }

    @Override
    public void broadcast(String message) {
        if (clientSocket!=null) {
            try {
                OutputStream outputStream = clientSocket.getOutputStream();
                outputStream.write(message.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ListeningTask implements Runnable {
        private InputStream inputStream;

        private ListeningTask(InputStream inputStream) {
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
                    messageFunction.accept(new String(buf));
                    length = inputStream.read(buf);
                }
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch(IOException e) {
                System.out.println("Server closed connection (unexpectedly) - READ");
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
            } catch (NumberFormatException e) {
                System.out.println("錯誤的 port number");
            }
        }

        Client client = new Client(serverName, serverPort, System.out::println);
        (new Thread(client)).start();
    }

}