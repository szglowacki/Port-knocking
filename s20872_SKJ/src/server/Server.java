package server;

import java.util.ArrayList;
import java.util.List;


public class Server {
    static List<Integer> PORTS_TO_LISTEN = new ArrayList<>();

    public static void main(String[] args)
    {

        List<Integer> openedUDPports = new ArrayList<>();

        for (String arg : args)
        {

            int port = Integer.parseInt(arg);
            PORTS_TO_LISTEN.add(port);
            if(!openedUDPports.contains(port)) {

                Thread thread =  new Thread(new PortListener(port));
                thread.start();
                openedUDPports.add(port);
            }
        }


    }

}
