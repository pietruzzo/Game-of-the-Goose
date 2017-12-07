package ClientCommunication;

import Domain.CampoDaGioco;
import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Michele
 */
public class Server {
    private int port;
    private ServerSocket serverSocket;
    public CampoDaGioco campoDaGioco;
    
    public Server(int port) {
        this.port = port;
        
         //Instanzia la classe che gestisce il gioco dell'oca
        this.campoDaGioco = new CampoDaGioco();
        System.out.println(String.format("Creato il campo da gioco. Presenti %d caselle", campoDaGioco.NumeroCaselle()));
    }
    
    //Gestione MultiThread
    public void startServer() {
        //Crea un nuovo thread
        ExecutorService executor = Executors.newCachedThreadPool();
        
        try 
        {
            serverSocket = new ServerSocket(port);
        } 
        catch (IOException e) 
        {
            System.err.println(e.getMessage()); // porta non disponibile
            return;
        }
        System.out.println("Server ready");
        while (true) 
        {
            try 
            {
                Socket socket = serverSocket.accept();
                executor.submit(new ClientHandler(socket, this));
            } 
            catch(IOException e) 
            {
                break; // entrerei qui se serverSocket venisse chiuso
            }
        }
        
        executor.shutdown();
        
        try 
        {
            serverSocket.close();
        } 
        catch (IOException e) 
        {
            System.err.println(e.getMessage());
            return;
        }
    }
}
