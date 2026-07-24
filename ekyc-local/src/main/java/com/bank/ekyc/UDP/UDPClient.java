package com.bank.ekyc.UDP;

import java.net.*;

public class UDPClient {

    public static void main(String[] args) throws Exception {

        DatagramSocket socket = new DatagramSocket();

        InetAddress address = InetAddress.getByName("localhost");

        for (int i = 1; i <= 100; i++) {

            String message = String.valueOf(i);

            byte[] data = message.getBytes();

            DatagramPacket packet =
                    new DatagramPacket(
                            data,
                            data.length,
                            address,
                            9999);

            socket.send(packet);
        }

        System.out.println("Đã gửi 100 packet.");

        socket.close();
    }
}