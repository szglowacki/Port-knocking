package client;


import java.util.ArrayList;
import java.util.List;

public class Client {
    public static void main(String[] args)
    {
         List<String> portlist = new ArrayList<>();
         String destinationIP;
        destinationIP = args[0];
        for(int i = 1; i< args.length; i++)  portlist.add(args[i]);

        new Thread(new ClientThread(destinationIP,portlist)).start();

    }
}
