package theocagameserver;

import ClientCommunication.Server;

/**
 * @author Michele
 */
public class TheOcaGameServer {

    public static void main(String[] args) {
        // TODO code application logic here
        Server server = new Server(1337);
        
        //Mette il server in running
        server.startServer();
    }
}
