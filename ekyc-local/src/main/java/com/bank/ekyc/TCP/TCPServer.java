package com.bank.ekyc.TCP;

import java.io.*;
import java.net.*;

public class TCPServer {
    public static void main(String[] args) throws Exception {

        ServerSocket server = new ServerSocket(9999);

        System.out.println("TCP Server đang chờ...");

        Socket socket = server.accept();

        System.out.println("Đã kết nối: " + socket.getInetAddress());

        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

        String message = in.readLine();

        System.out.println("Client gửi: " + message);

        socket.close();
        server.close();
    }
}
