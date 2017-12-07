package ClientCommunication;

import Domain.Casella;
import Domain.EffettoCasella;
import Domain.GiocaResult;
import Domain.Pedina;
import Messages.*;
import javafx.scene.paint.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Michele
 */
public class ClientHandler  implements Runnable {
    private Socket socket;
    private Server server;
    
    
    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }
    
    public void run() 
    { 
        int idPedina = 0;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        String chatMessage = null;
        int[] lastIdChatMessageRead = new int[] {0}; //Array per poterlo passare come riferimento
        
        try 
        {
            Boolean mioTurno = false;
            
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(socket.getInputStream());
            
            while (true) {
                //Aspetta una chiamata da parte di un client
                MessageToServer receivedMessage = (MessageToServer) ois.readObject();
                MessageFromServer responseMessage;
                GiocaResult giocaResult = null;
                
                switch(receivedMessage.TipoMessaggio)
                {
                    case NewPedina:
                        //Aggiunge la pedina
                        idPedina = server.campoDaGioco.AddPedina(receivedMessage.PedinaDTO.NomePedina, receivedMessage.PedinaDTO.Colore);
                        //Inizializza il turno alla prima pedina ricevuta
                        if(server.campoDaGioco.GetTurno() == null)
                            server.campoDaGioco.IncrementaTurno();
                         
                        break;
                        
                    case CheckTurn:
                        if(server.campoDaGioco.GetTurno().GetId() == idPedina)
                            mioTurno = true;
                        else
                            mioTurno = false;

                        break;
                        
                    case Gioca:
                        Pedina pedinaAttuale = server.campoDaGioco.GetTurno();
                        
                        if(pedinaAttuale.GetId() == idPedina)
                        {
                            giocaResult = this.server.campoDaGioco.Gioca();
                        }
                        break;
                        
                    case  Chat:
                        this.server.campoDaGioco.AddChatMessage(receivedMessage.ChatMessage, idPedina);
                        break;
                        
                    case Exit:
                        this.server.campoDaGioco.RemovePedina(idPedina);
                        break;
                }
                                  
                //Costruzione del messaggio di ritorno
                List<PedinaDTO> pedineDTO = this.GetPedineDTO(server.campoDaGioco.GetPedine());
                chatMessage = this.server.campoDaGioco.GetChatMessage(idPedina, lastIdChatMessageRead);
                
                if(giocaResult != null)
                    responseMessage = new MessageFromServer(receivedMessage.TipoMessaggio, idPedina, pedineDTO, mioTurno, chatMessage, giocaResult.messaggio, giocaResult.dado1, giocaResult.dado2);
                else
                    responseMessage = new MessageFromServer(receivedMessage.TipoMessaggio, idPedina, pedineDTO, mioTurno, chatMessage, null, 0, 0);
                    
                oos.writeObject(responseMessage);
                oos.flush();
            }
        } 
        catch (IOException e) 
        {
            System.err.println(e.getMessage());
        } 
        catch (ClassNotFoundException ex) {
            System.err.println(ex.getMessage());
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            //Chiudo le connessioni con il client
            try {
                if(oos != null)
                    oos.close();
                if(ois != null)
                    ois.close();
                
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //Se il client viene chiuso, rimuove la pedina dal gioco
            this.server.campoDaGioco.RemovePedina(idPedina);
        }
    }
    
    
    public List<PedinaDTO> GetPedineDTO(List<Pedina> pedine)
    {
        List<PedinaDTO> pedineDTO = new ArrayList();
        
        for(Pedina pedina : pedine)
        {
            PedinaDTO pedinaDTO = new PedinaDTO();
            pedinaDTO.IdPedina = pedina.GetId();
            pedinaDTO.NomePedina = pedina.GetNome();
            pedinaDTO.NumCasella = pedina.GetCasellaAttuale().GetNumCasella();
            pedinaDTO.Colore = pedina.GetColore();
            pedinaDTO.Xcoord = 0;
            pedinaDTO.Ycoord = 0;
            
            pedineDTO.add(pedinaDTO);
        }
        
        return pedineDTO;
    }
}
