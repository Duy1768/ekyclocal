package com.bank.ekyc.UDP;

import java.net.*;

public class UDPServer {

    public static void main(String[] args) throws Exception {

        DatagramSocket socket = new DatagramSocket(9999);

        int count = 0;

        System.out.println("UDP Server đang chờ...");

        while (true) {

            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            socket.receive(packet);

            count++;

            String message = new String(packet.getData(), 0, packet.getLength());

            System.out.println("Đã nhận " + count + ": " + message);

            Thread.sleep(10000);
        }
    }
}