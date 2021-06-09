package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PortListener implements Runnable {

    private int port;
    private static HashMap<String, List<Integer>> portsMap = new HashMap<>();
    private byte[] data = new byte[1024];


    PortListener(int port)
    {
        this.port = port;
        System.out.println("Listening on: "+port);


    }
    @Override

    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(port);
            while(true)
            {
                DatagramPacket packet = new DatagramPacket(data,data.length);
                socket.receive(packet);
                String clientAddress = String.valueOf(packet.getAddress());
               addLogToMap(clientAddress,port);

               if(checkClient(clientAddress))
               {

                   portsMap.remove(clientAddress);
                   ServerSocket serverSocket = new ServerSocket(0);
                   int freeTCPPort = serverSocket.getLocalPort();
                   byte[] tcpPortTab = Integer.toString(freeTCPPort).getBytes();
                   DatagramPacket answer = new DatagramPacket(tcpPortTab, tcpPortTab.length, packet.getAddress(), packet.getPort());
                   socket.send(answer);

                   Socket tcpSocket;
                   tcpSocket = serverSocket.accept();
                   BufferedReader in = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
                   PrintWriter out = new PrintWriter(tcpSocket.getOutputStream(),true);
                   String receivedMessage = in.readLine();
                   System.out.println("Received request from IP: "+packet.getAddress()+" Content: "+receivedMessage);
                   out.println("Respond from server on: " + receivedMessage+" on port: "+freeTCPPort);



                   tcpSocket.close();
                   serverSocket.close();



               }

            }
        } catch (SocketException e) {
            System.err.println("Couldn't open port ");
        } catch (IOException e) {
            e.getCause();
        }

    }

    private static void addLogToMap(String client, int port)
    {
        List<Integer> tmp = new ArrayList<>();
        if(!portsMap.containsKey(client))
        {

            tmp.add(port);
            portsMap.put(client,tmp);
        }
        else {
            tmp = portsMap.get(client);
            tmp.add(port);
            portsMap.put(client, tmp);
        }
    }

    private static boolean checkClient(String client)
    {
        boolean tmp = false;
        List<Integer> portsList = portsMap.get(client);
        for(int i = 0; i <= portsList.size()- Server.PORTS_TO_LISTEN.size(); i++)
        {
            if(portsList.subList(i, Server.PORTS_TO_LISTEN.size()+i).equals(Server.PORTS_TO_LISTEN)) tmp = true;

        }

        return tmp;
    }


}
