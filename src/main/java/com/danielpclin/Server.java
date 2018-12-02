package com.danielpclin;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Server {

    private String serverName = null;
    private int serverPort = 0;

    public Server(String name, int port) {
        serverName = name;
        serverPort = port;
    }

    public void start() {

        //set server address
        InetSocketAddress serverSocketAddress = new InetSocketAddress(serverName, serverPort);
        String localAddress = serverSocketAddress.getAddress().getHostAddress();

        //try-with-resources statement, 
        //following statement will close serverSocketChannel and selector automatically
        try(ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            Selector selector = Selector.open()) {

            //Retrieves a server socket associated with this channel.
            ServerSocket serverSocket = serverSocketChannel.socket();
            //Binds the ServerSocket to a specific address
            System.out.println("Bind server socekt to " + localAddress + ":" + serverPort);
            serverSocket.bind(serverSocketAddress);
            System.out.println("Non-blicking I/O TCP server binding success");

            //set non-blocking mode
            serverSocketChannel.configureBlocking(false);
            //register the channel to the selector 
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            byte[] buf = new byte[1024];
            InetSocketAddress clientSocketAddress = null;
            SocketChannel clientChannel = null;
            Iterator<SelectionKey> itr = null;
            String clientAddress = null;
            SelectionKey key = null;
            int clientPort = 0;
            int length = 0;
            String str = null;

            //select those channels that ready for IO operation
            while(true) {
                selector.select();
                itr = selector.selectedKeys().iterator();

                //for each selected channels, which represent as SelectionKey, do the IO task
                while(itr.hasNext()) {
                    key = itr.next();
                    itr.remove();

                    //case: serverSocketChannel ready for accepting new client
                    if(key.isAcceptable()) {
                        clientChannel = serverSocketChannel.accept();
                        //set the clientChannel to non-blocking mode
                        clientChannel.configureBlocking(false);
                        //register clientChannel to selector and 
                        clientChannel.register(selector, SelectionKey.OP_READ);

                        clientSocketAddress = (InetSocketAddress)(clientChannel.socket().getRemoteSocketAddress());
                        clientAddress = clientSocketAddress.getAddress().getHostAddress();
                        clientPort = clientSocketAddress.getPort();
                        System.out.println("Connecting to " + clientAddress + ":" + clientPort);
                        continue;
                    }

                    //case: a clientChannel ready for read message
                    if(key.isReadable()) {
                        //Get the channel for which this key was created
                        clientChannel = (SocketChannel)key.channel();
                        try {
                            length = clientChannel.read(buffer);
                            //length = 0 or -1 if clientChannel has reached end-of-stream
                            if(length <= 0) {
                                throw new IOException();
                            }
                            //get message from buffer
                            buffer.flip();
                            buffer.get(buf, 0, length);

                            str = new String(buf, 0, length);
                            System.out.println(str);
                            str = str.toUpperCase();
                            buffer.clear();
                            buffer.put(str.getBytes());

                            //echo the message
                            buffer.flip();
                            clientChannel.write(buffer);
                            buffer.clear();
                        } catch(IOException e) {
                            //client shutdown connection or other IO exception
                            //Requests that the registration of this key's channel with its selector be cancelled
                            key.cancel();

                            clientSocketAddress = (InetSocketAddress)(clientChannel.socket().getRemoteSocketAddress());
                            clientAddress = clientSocketAddress.getAddress().getHostAddress();
                            clientPort = clientSocketAddress.getPort();
                            System.out.println("Disconnecting to " + clientAddress + ":" + clientPort);

                            //close the disconnected client channel
                            try {
                                clientChannel.close();
                            } catch(IOException e1) {}
                        }
                    }
                }
            }

        } catch(IOException e2) {
            e2.printStackTrace();
        } finally {
            System.out.println("Server shutdown.");
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

        Server server = new Server(serverName, serverPort);
        server.start();
    }

}