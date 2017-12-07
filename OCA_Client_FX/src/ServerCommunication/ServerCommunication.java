package ServerCommunication;

import Messages.*;
import javafx.scene.paint.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import oca_client_fx.Client_Lib;
import oca_client_fx.Giocatore;

/**
 * @author Michele
 */
public class ServerCommunication implements Runnable{
    
    private String ip;
    private int port;
    private Socket socket;
    Color colore; 
    String nomePedina;
    
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    
    static Semaphore mutex = new Semaphore(1);
    
    public ServerCommunication(String ip, int port, Color colore, String nome) {
        this.ip = ip;
        this.port = port;
        this.colore = colore;
        this.nomePedina = nome;
    }
    
    
    @Override
    public void run() {

        try {
            this.startClient();
        } catch (IOException ex) {
            Logger.getLogger(ServerCommunication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerCommunication.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void startClient() throws IOException, InterruptedException {
        socket = new Socket(ip, port);
        System.out.println("Connection established");
        
        oos = new ObjectOutputStream(socket.getOutputStream());
        oos.flush();
        ois = new ObjectInputStream(socket.getInputStream());       
                
        MessageFromServer response = this.SendMessageToServer(TypeMessage.NewPedina, /*chat message*/null);
        
        //Creare la pedina con i dati scelti dall'utente
        Giocatore player = new Giocatore(response.IdPedina, this.nomePedina, this.colore);
        Client_Lib.aggiungi_giocatore(player);
        
        try {
            while (true) {
                //Effettua l'aggiornamento a intervalli regolari
                try{ Thread.sleep(1000);}
                catch(InterruptedException ex){}
                
                response = this.SendMessageToServer(TypeMessage.CheckTurn, /*chat message*/null);
                
                boolean somethingChanged = false;
                
                //Elimina i giocatori che hanno abbandonato
                if(response.PedineDTO != null)
                {
                    List<Integer> idPedine = response.PedineDTO.stream().map(x->x.IdPedina).collect(Collectors.toList());
                    Client_Lib.verificaElimina_giocatore(idPedine);
                }
                
                //Recupero la lista dei nomi e delle posizioni delle pedine presenti nel gioco
                String listaPedinePosizioni = "Pedine (id)(pos) = ";
                for(PedinaDTO pedinaServer : response.PedineDTO)
                {
                    listaPedinePosizioni += String.format("%d:%d - ", pedinaServer.IdPedina, pedinaServer.NumCasella);
                    
                    Giocatore playerClient = Client_Lib.getGiocatoreById(pedinaServer.IdPedina);
                    
                    //se playerClient è nullo è stato aggiunto lato server
                    if(playerClient == null)
                    {
                        Giocatore giocatore = new Giocatore(pedinaServer.IdPedina, pedinaServer.NomePedina, Color.web(pedinaServer.Colore));
                        Client_Lib.aggiungi_giocatore(giocatore);
                        //Client_Lib.ricevuto_in_log(giocatore.NomePedina + "si è unito alla partita!");
                        somethingChanged = true;
                    }
                    else if(playerClient.NumCasella != pedinaServer.NumCasella)
                    {
                        Client_Lib.Lancia_e_muovi(pedinaServer.IdPedina, pedinaServer.NumCasella, 0, 0);
                        somethingChanged = true;
                    }
                    if (pedinaServer.NumCasella == 63) {
                        oca_client_fx.OCA_Client_FX.qualcunoHaVinto = true;
                    }
                }
                
                //Aggiorna le pedine lato client
                if(somethingChanged)
                {
                    //Client_Lib.ricevuto_in_log(listaPedinePosizioni);
                }
                //Aggiorna l'effetto della casella
                if(response.ServiceMessage != null && !response.ServiceMessage.equals(""))
                {
                    Client_Lib.ricevuto_in_log(response.ServiceMessage);
                }                
                //Aggiorna la chat
                if(!response.ChatMessage.trim().equals(""))
                {
                    Client_Lib.ricevuto_in_chat(response.ChatMessage);
                }
                
                if(response.mioTurno)
                    Client_Lib.tuo_turno();                
            }
        }
        catch(NoSuchElementException e) {
            System.out.println("Connection closed");
        }
        finally {
            CloseClient();
        }
    }
    
    //Manda il messaggio al server e ritorna la risposta
    public MessageFromServer SendMessageToServer(TypeMessage tipoMessaggio, String chatMessage)
    {
        try 
        {
            //In questo modo prima di mandare una richiesta al server si deve aver ricevuto un'eventuale risposta in sospeso
            mutex.acquire();
                    
            //Inizializza una nuova pedina per la prima comunicazione al server
            PedinaDTO pedinaDTO = null;
            if(tipoMessaggio == TypeMessage.NewPedina)
                pedinaDTO = GetPedinaDTO();
            
            MessageToServer messaggioAlServer = new MessageToServer(tipoMessaggio, pedinaDTO, chatMessage);
            
            //Scrivo il messaggio al server
            oos.writeObject(messaggioAlServer);
            oos.flush();
            
            //Aspetto la risposta da parte del server
            MessageFromServer receivedMessage = (MessageFromServer)ois.readObject();
            
            if(receivedMessage.TipoMessaggio != TypeMessage.CheckTurn)
                System.out.println(receivedMessage.TipoMessaggio);
            
            mutex.release();
            
            return receivedMessage;
        } 
        catch (Exception ex) 
        {
            Logger.getLogger(ServerCommunication.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public void CloseClient() throws IOException
    {
        ois.close();
        oos.close();
        socket.close();
    }
    
    //Metodo per inizializzare la pedina lato client
    public PedinaDTO GetPedinaDTO()
    {
        PedinaDTO pedinaDTO = new PedinaDTO();
        
        pedinaDTO.IdPedina = 0;
        pedinaDTO.NomePedina = this.nomePedina;
        pedinaDTO.Colore = this.colore.toString();
        pedinaDTO.NumCasella = 0;
        pedinaDTO.Xcoord = 0;
        pedinaDTO.Ycoord = 0;
            
        return pedinaDTO;
    }
    
    //Metodo per creare le pedine da mandare al server dato il giocatore
    public PedinaDTO CreatePedinaDTOByGiocatore(Giocatore player)
    {
        PedinaDTO pedinaDTO = new PedinaDTO();
        
        pedinaDTO.IdPedina = player.IdPedina;
        pedinaDTO.NomePedina = player.NomePedina;
        pedinaDTO.Colore = player.colore.toString();
        pedinaDTO.NumCasella = player.NumCasella;
        pedinaDTO.Xcoord = player.papera_X;
        pedinaDTO.Ycoord = player.papera_Y;
            
        return pedinaDTO;
    }

}
