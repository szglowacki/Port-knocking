package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ClientThread implements Runnable {
    private List<String> portlist;
    private DatagramSocket socket;
    private InetAddress dstAddress;


    public ClientThread(String destination, List<String>portlist)
    {
        this.portlist = portlist;
        try {
            socket = new DatagramSocket();
            dstAddress =  InetAddress.getByName(destination);
        } catch (SocketException e) {
            System.err.println("Couldn't open socket");
        } catch (UnknownHostException e) {
            System.err.println("Couldn't find destination's IP address");
        }
    }
    @Override
    public void run() {
        byte[] buff = new byte[0];
        try {
            buff = InetAddress.getLocalHost().getHostName().getBytes();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        for (String s : portlist) {
            DatagramPacket sentPacket = new DatagramPacket(buff, buff.length);
            sentPacket.setAddress(dstAddress);
            sentPacket.setPort(Integer.parseInt(s));
            try {
                socket.send(sentPacket);
            } catch (IOException e) {
                System.err.println("Couldn't send packet to server");
            }
        }
        DatagramPacket receivedPacket = new DatagramPacket(new byte[1024],1024 );
        try {
            socket.setSoTimeout(10000);
            socket.receive(receivedPacket);
            int tcpPort= Integer.parseInt(new String(receivedPacket.getData(),0,receivedPacket.getLength()));
            Socket tcpSocket = new Socket(dstAddress,tcpPort);
            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
            PrintWriter out = new PrintWriter(tcpSocket.getOutputStream(),true);
            out.println(new String(buff,0,buff.length, StandardCharsets.UTF_8)+" request");
            System.out.println(in.readLine());
            tcpSocket.close();
            socket.close();
        }catch (IOException e) {
            System.err.println("No respond message from server");
        }
    }
}
