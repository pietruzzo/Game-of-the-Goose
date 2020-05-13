/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oca_client_fx;

import Messages.MessageFromServer;
import Messages.TypeMessage;
import ServerCommunication.ServerCommunication;
import java.io.IOException;
import javafx.scene.paint.Color;

/**
 *
 * @author pietro
 *
 * Tutte le classi dichiarate di seguito devono essere implementate
 *
 */
public class Client {

    private ServerCommunication clientCommunication;

     /*
    I seguenti metodi si abilitano a seguito dell'interazione con l'interfaccia
    grafica e devono essere implementati
    
        Medodo chiamato con click sul bottone gioca
        Verifica che il colore non sia già stato scelto
        FALSE la pedina è già stata scelta
        TRUE  la pedina non è stata scelta
     */
    public Boolean proposta_pedine(Color colore, String nome, String ipServer, int port) throws IOException, IllegalArgumentException{
        System.out.println("PropostaPedine");
        if (this.clientCommunication == null) {
            this.clientCommunication = new ServerCommunication(ipServer, port, colore, nome);
            Thread thread = new Thread(this.clientCommunication);
            thread.start();
        }
        return true;
    }

    /*
        Medodo chiamato con click sul bottone gioca o successivamente alla forzatura
            della scelta della casella
        Istanziare un oggetto Giocatore (Giocatore del client)
        chiamare Client_Lib.addplayer(Giocatore)
        (Può essere utile salvare IdGiocatore del Client)
     */
    public void setta_pedina(Color colore, String nome) {
        System.out.println("SettaPedine " + nome);

    }

    /*
        Spedire Messaggio_Chat al server
        Messaggio è solo la stringa di testo senza additivi o ogm
     */
    public void chat_to_server(String messaggio) {
        System.out.println(messaggio + " -in uscita");

        //Se non è presente del testo è inutile mandare messaggi
        if (!messaggio.trim().equals("")) {
            this.sendMessageToServer(TypeMessage.Chat, messaggio);
        }
    }

    /*
        Medodo chiamato con click sul bottone LANCIA
        OSS LANCIA deve essere attivato al turno giusto attraverso Client_Lib.tuo_turno()
            e si disabiliterà automaticamente alla fine del turno
     */
    public void clicked_lancia_dado() {
        this.sendMessageToServer(TypeMessage.Gioca, null);
    }

    public void sendMessageToServer(TypeMessage tipoMessaggio, String chatMessage) {
        MessageFromServer response;
        try {
            response = clientCommunication.SendMessageToServer(tipoMessaggio, chatMessage);
        } catch (Exception ex) {
            response = null;
        }

        if (response != null && response.TipoMessaggio == TypeMessage.Gioca) {
            if (response.ServiceMessage != null && !response.ServiceMessage.equals("")) {
                Client_Lib.ricevuto_in_log(response.ServiceMessage);
            }

            int idPedina = response.IdPedina;
            int numCasella = response.PedineDTO.stream().filter(x -> x.IdPedina == idPedina)
                    .findFirst().get().NumCasella;

            Client_Lib.Lancia_e_muovi(response.IdPedina, numCasella, response.Dado1, response.Dado2);
        }
    }
    
    //Metodo che viene invocato quando un client decide di uscire o vince
    public void client_exit() throws IOException {
        System.out.println("Chiusura comunicazione con server");
        TypeMessage messaggio_chiusura = TypeMessage.Exit;
        try{ //Non deve essere chiamato prima che si instaurino le connessioni
            clientCommunication.SendMessageToServer(messaggio_chiusura, "");
            clientCommunication.CloseClient();
        }catch(Exception ex){}
        System.out.println("Canali chiusi");
    }
}
