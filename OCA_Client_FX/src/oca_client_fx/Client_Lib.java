/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oca_client_fx;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import static oca_client_fx.OCA_Client_FX.lista_giocatori;
import static oca_client_fx.OCA_Client_FX.vettore_caselle;
import static oca_client_fx.OCA_Client_FX.scala;

/**
 *
 * @author pietro
 *
 * Insieme di metodi statici per il Client
 */
public class Client_Lib {

    /*
        Esegue tutto il necessario per aggiornare la posizione del player (IdPlayer)
        nella nuova casella (n_casella) e per visualizzare il risultato del lancio di dadi
     */
    public static void Lancia_e_muovi(int IdPlayer, int n_casella, int dado1, int dado2) {
        //Gestisci condizione Vittoria
        if (n_casella == 63) {
            ricevuto_in_log("il giocatore "+ getGiocatoreById(IdPlayer).NomePedina+" ha vinto la partita!");
        }
        //Recupera le info del giocatore
        Giocatore giocatore = getGiocatoreById(IdPlayer);
        int n_casella_old = giocatore.NumCasella;
        //Aggiorna la posizione attuale del giocatore
        giocatore.NumCasella = n_casella;

        if (dado1 == 0) {
            Muovi(giocatore, n_casella_old);
        } else {
            //Se la pedina finisce su una casella che fa tornare indietro esegui 2 transazioni di fila
            int somma_dadi = dado1 + dado2;
            int n_casella_int;
            if (n_casella_old + somma_dadi > 63) {
                n_casella_int = 63;
            } else {
                n_casella_int = n_casella_old + somma_dadi;
            }
            System.out.println("casella OLD " + n_casella_old + ", INT " + n_casella_int + ", NEW" + n_casella);

            Platform.runLater(
                    () -> {
                        //Lancia Dado
                        new Animazione_Dadi(dado1, dado2);
                        //if (n_casella_int == 5 || n_casella_int == 58 || n_casella_int == 14 || n_casella_int == 36 || n_casella_int == 45 || n_casella_int == 54 || n_casella_int == 6 || n_casella == 63) {
                        if (n_casella_int != n_casella) {
                            Timer timer = new Timer();
                            Timer timer2 = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    Muovi(giocatore, n_casella_old, n_casella_int);
                                }
                            }, 1000);
                            timer2.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    Muovi(giocatore, n_casella_int);
                                }
                            }, 2000);
                        } else {
                            //Lancia dado
                            //new Animazione_Dadi(dado1, dado2);
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    Muovi(giocatore, n_casella_old);
                                }
                            }, 900);
                        }
                    });
        }
    }

    static Semaphore mutex_tran_in_corso = new Semaphore(1);

    private static void Muovi(Giocatore giocatore, int n_casella_old) {
        Muovi(giocatore, n_casella_old, giocatore.NumCasella);
    }
  
    private static void Muovi(Giocatore giocatore, int n_casella_old, int n_casella_new) {
        //Sposta (se necessario)
        if (giocatore != null && n_casella_old != n_casella_new) {
           
            try {
                mutex_tran_in_corso.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(Client_Lib.class.getName()).log(Level.SEVERE, null, ex);   
            }
            System.out.println("Movimento");
            
            //Se la traslazione è molto grande non passa per tutte le caselle
            int i = n_casella_old;
            if(Math.abs(n_casella_old - n_casella_new) > 12)
                i = n_casella_new;
            
            do
            {
                if(i < n_casella_new)
                    i++;
                else if(i > n_casella_new)
                    i--;
                
                //Posizione di arrivo (centro della casella + Offset)
                int[] coord_arrivo = coord_con_offset_angolo(i);

                //Effettua Movimento (Attraverso Traslazione.java)
                int x_arrivo = coord_arrivo[0];
                int y_arrivo = coord_arrivo[1];
                int angolo = coord_arrivo[2];
                Traslazione movimento = new Traslazione(giocatore.pedina, x_arrivo, y_arrivo, angolo);
                movimento.start();
                synchronized (movimento) {
                    try {
                        System.out.println("Waiting for Traslazione to complete...");
                        movimento.wait();
                        System.out.println("Traslazione Completed.");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            //}
            } while(i != n_casella_new);
            
            mutex_tran_in_corso.release();

            //Aggiornamento contatore casella
            vettore_caselle[n_casella_old].n_papere = vettore_caselle[n_casella_old].n_papere - 1;
            vettore_caselle[n_casella_new].n_papere = vettore_caselle[n_casella_new].n_papere + 1;

            //Aggiorno LOG
            //OCA_Client_FX.append_chatlog("-MOVED " + giocatore.NomePedina + " to " + n_casella_new, false);
        }
    }
    
    /*
        Aggiunge un giocatore al gioco
     */
    public static void aggiungi_giocatore(Giocatore player) {
        //Necessario per la gestione dei Thread, in questo modo l'operazione viene svolta nel thread dell'interfaccia
        Platform.runLater(
                () -> {
                    //Aggiungere Giocatore alla lista_giocatori
                    lista_giocatori.add(player);
                    //Aggiungere Giocatore alla lista menu
                    ImageView miniatura_pedina = new ImageView(player.carica_im_papera(30, 30));
                    OCA_Client_FX.aggiungi_giocatore_griglia(player.NomePedina, miniatura_pedina);
                    //Aggiungere il giocatore al campo di gioco (Casella 0)
                    int[] coord_angolo = coord_con_offset_angolo(0);
                    //Aggiornare contatore casella
                    vettore_caselle[0].n_papere = vettore_caselle[0].n_papere + 1;
                    OCA_Client_FX.aggiungi_giocatore_al_campo(player.pedina, coord_angolo[0], coord_angolo[1], coord_angolo[2]);
                }
        );
    }
    
      /*
        Verifica l'eliminazione di un giocatore e nel caso, lo elimina
     */
    public static void verificaElimina_giocatore(List<Integer> idGiocatoriOnline) {
        //Cerca se ci sono giocatori eliminati
        Giocatore giocatoreEliminato = lista_giocatori.stream().filter(x -> !idGiocatoriOnline.contains(x.IdPedina)).findFirst().orElse(null);

        //Se è presente lo elimino
        if(giocatoreEliminato != null)
        {
            lista_giocatori.remove(giocatoreEliminato);
            Client_Lib.ricevuto_in_log(giocatoreEliminato.NomePedina + " ha abbandonato la partita!");
            
            //Necessario per la gestione dei Thread, in questo modo l'operazione viene svolta nel thread dell'interfaccia
            Platform.runLater(
                () -> {
                    //Elimino il giocatore dall'interfaccia
                    OCA_Client_FX.ridisegna_giocatore_griglia();
                    OCA_Client_FX.rimuovi_giocatore_dal_campo(giocatoreEliminato.pedina);
                });
        }
    }

    /*
        Metodo Gestito dall'interfaccia Grafica
     */
    public static void ridimensiona_interfaccia(Double scala_di_gioco) {
        int[] coord_con_angolo;
        //Aggiorna Variabile scala
        scala = scala_di_gioco;
        //Ridimensiona terreno
        OCA_Client_FX.ridimensiona_terreno();
        //Ridimensiona papere
        Giocatore.ridimensiona_pedine(lista_giocatori);
        //Azzera contatore n_papere
        casella.azzera_contatori_caselle();
        //Piazza papere sul terreno
        for (int i = 0; i < lista_giocatori.size(); i++) {
            coord_con_angolo = coord_con_offset_angolo(lista_giocatori.get(i).NumCasella);
            OCA_Client_FX.aggiungi_giocatore_al_campo(lista_giocatori.get(i).pedina, coord_con_angolo[0], coord_con_angolo[1], coord_con_angolo[2]);
            vettore_caselle[lista_giocatori.get(i).NumCasella].n_papere = vettore_caselle[lista_giocatori.get(i).NumCasella].n_papere + 1;
        }
    }

    /*
        Si occupa di appendere in chat il messaggio ricevuto
     */
    public static void ricevuto_in_chat(String Messaggio) {
        //Necessario per la gestione dei Thread, in questo modo l'operazione viene svolta nel thread dell'interfaccia
        Platform.runLater(
                () -> {
                    OCA_Client_FX.append_chatlog(Messaggio, true);
                });
    }

    /*
        Si occupa di appendere in chat il messaggio ricevuto
     */
    public static void ricevuto_in_log(String Messaggio) {
        //Necessario per la gestione dei Thread, in questo modo l'operazione viene svolta nel thread dell'interfaccia
        Platform.runLater(
                () -> {
                    OCA_Client_FX.append_chatlog(Messaggio, false);
                });
    }

    /*
        Abilita il tasto Lancia Dadi
     */
    public static void tuo_turno() {
        //Necessario per la gestione dei Thread, in questo modo l'operazione viene svolta nel thread dell'interfaccia
        Platform.runLater(
                () -> {
                    OCA_Client_FX.abilita_lancia_dadi(true);
                });
    }

    /*
        Ritorna le info di un giocatore dato l'id
     */
    public static Giocatore getGiocatoreById(int idPedina) {
        Giocatore giocatore = lista_giocatori.stream().filter(x -> x.IdPedina == idPedina)
                .findFirst()
                .orElse(null);

        return giocatore;
    }

    /*
        Metodi privati  
     */
    private static int[] coord_con_offset_angolo(int n_casella) {

        int pedine_in_casella = 0;//numero di giocatori nella casella di destinazione

        for (int i = 0; i < lista_giocatori.size(); i++) {
            if (lista_giocatori.get(i).NumCasella == n_casella) {
                pedine_in_casella = pedine_in_casella + 1;
            }
        }

        //Posizione di arrivo
        int x = vettore_caselle[n_casella].getX();
        int y = vettore_caselle[n_casella].getY();

        //Posizione di arrivo (- Offset sagoma papera)
        x = (x - Giocatore.papera_X / 2);
        y = (y - Giocatore.papera_Y / 2);

        //Aggiungo Offset in caso di pedine sovrapposte
        x = x + 10 * vettore_caselle[n_casella].n_papere;
        y = y + 10 * vettore_caselle[n_casella].n_papere;

        //Calcolo centro terreno
        int centroX = (int) (OCA_Client_FX.terreno_X / 2);
        int centroY = (int) (OCA_Client_FX.terreno_Y / 2);

        //Applico correttivi sul centro adattandolo alla forma del tabellone
        final Double corr = 6.5;
        if (x - centroX > OCA_Client_FX.terreno_X / corr) {
            centroX = (int) (centroX + OCA_Client_FX.terreno_X / corr);
        } else if (x - centroX < -(OCA_Client_FX.terreno_X / corr)) {
            centroX = (int) (centroX - OCA_Client_FX.terreno_X / corr);
        }
        //Calcoli successivi per l'angolo
        Double rot;
        rot = Math.asin((x - centroX) / Math.sqrt(Math.pow(x - centroX, 2.0) + Math.pow(y - centroY, 2)));
        if (y - centroY < 0) { //Estendo il codominio limitato dell'asin()
            rot = -rot - Math.PI;
        }
        rot = -rot;

        //Converto da rad a gradi
        rot = rot * 180 / (Math.PI);

        /*Aggiunta di correttivi OPZIONALI
        if (rot < 25 && rot > 25) {
            rot = 0.0;
        } else if (rot > 65 && rot < 115) {
            rot = 90.0;
        } else if (rot > 245 && rot < 295) {
            rot = 270.0;
        } else if (rot > 155 && rot < 205) {
            rot = 180.0;
        }
         //*/
        //In Scala
        x = (int) (x * scala);
        y = (int) (y * scala);

        int[] coord_angolo = new int[3];
        coord_angolo[0] = x;
        coord_angolo[1] = y;
        coord_angolo[2] = rot.intValue();
        return coord_angolo;
    }

    private static int get_indice_player(int IdPlayer) {
        int n_player = 0;
        for (int i = 0; i < lista_giocatori.size(); i++) {
            if (lista_giocatori.get(i).IdPedina == IdPlayer) {
                n_player = i;
                break;
            }
        }
        return n_player;
    }
}
