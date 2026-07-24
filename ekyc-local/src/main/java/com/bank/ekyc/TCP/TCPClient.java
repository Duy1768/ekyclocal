package com.bank.ekyc.TCP;

import java.io.*;
import java.net.*;

public class TCPClient {
    public static void main(String[] args) throws Exception {

        Socket socket = new Socket("localhost", 9999);

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        out.println("Hello TCP");

        socket.close();
    }
}
