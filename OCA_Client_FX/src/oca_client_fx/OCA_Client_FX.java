/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oca_client_fx;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.ToggleGroup;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Toggle;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.WindowEvent;

import javax.swing.*;
import javax.swing.border.LineBorder;

/**
 *
 * @author pietro

 OCA_Client_FX.java gestisce la parte GRAFICA

 start(): primo metodo eseguito elementi_condivisi(): pannello laterale destro
 inizializzazione(): inizializzazione variabili globali campo_gioco():
 costruisce il campo gioco menu_gioco(): costruisce la schermata iniziale

 append_chatlog(): scrive una stringa o nella chat o nel log
 aggiungi_griglia_giocatori(): aggiunge un giocatore alla schermata iniziale
 *
 */
public class OCA_Client_FX extends Application {

    public static casella[] vettore_caselle;
    public static ArrayList<Giocatore> lista_giocatori;
    public static Client sessione;

    public static Double scala;
    public static boolean qualcunoHaVinto;
    public static final int terreno_X = 1179;
    public static final int terreno_Y = 848;

    private static TextArea chat_ricevuto;
    private static TextField chat_invio;
    private static TextArea log;
    private static GridPane griglia_giocatori;
    private static Button lancia_dadi;
    private static ImageView [][] dadi;
    private static Pane terreno;
    private static Text header_chat;
    private static Text header_log;
    
    
    @Override
    public void start(Stage finestra_principale) throws IOException {

        finestra_principale.setTitle("Gioco dell'Oca");
        finestra_principale.getIcons().add(new Image("paperella.png"));
        finestra_principale.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                try {
                    sessione.client_exit();
                } catch (IOException ex) {
                    Logger.getLogger(OCA_Client_FX.class.getName()).log(Level.SEVERE, null, ex);
                }
                Platform.exit();
                System.exit(0);
            }
        });
        
        //Creazione menu
        Scene menu = menu_gioco(finestra_principale);
        finestra_principale.setScene(menu);
        finestra_principale.show();        
        inizializzazione();
    }

    /**
     * @param args the command line arguments
     */
    public static void run(String[] args) {
        sessione = new Client();
        launch(args);
    }

    private void inizializzazione() throws FileNotFoundException, IOException {
        
        lista_giocatori= new ArrayList<Giocatore>();
        //Dimensioni terreno e pedine Predefinite
        scala = 0.7;
        qualcunoHaVinto = false;
        vettore_caselle = new casella[64];
        
        //Bottone lancia_dadi
        lancia_dadi = new Button("Lancia dadi");
        lancia_dadi.setDisable(true);
        lancia_dadi.setPrefSize(150, 30);
        lancia_dadi.setAlignment(Pos.CENTER);
        lancia_dadi.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(10), new Insets(0.0))));
        lancia_dadi.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                sessione.clicked_lancia_dado();
                lancia_dadi.setDisable(true);
                lancia_dadi.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(10), new Insets(0.0))));
            }
        });
        
        //Terreno di gioco
        Image sfondo = new Image("Tab2.jpg", terreno_X * scala, terreno_Y * scala, true, true);
        ImageView vi_sfondo = new ImageView(sfondo);
        terreno = new Pane(vi_sfondo);
        
        //Caricamento facce dadi
        dadi = new ImageView [2][6];
        String percorso_faccia;
        for (int i = 0; i < dadi[0].length; i++) {
            for (int j = 0; j < 2; j++) {
                percorso_faccia = "dado_"+(i+1)+".png";
                dadi[j][i]= new ImageView(new Image(percorso_faccia));
                dadi[j][i].setVisible(false);
                if (j==0) {
                   dadi[j][i].setTranslateX((int)(terreno_X*scala/2.5));
                   dadi[j][i].setTranslateY((int)(terreno_Y*scala/2.3)); 
                }
                else{
                    dadi[j][i].setTranslateX((int)(terreno_X*scala/2.5)+90);
                    dadi[j][i].setTranslateY((int)(terreno_Y*scala/2.3)); 
                }
                terreno.getChildren().add(dadi[j][i]);
            }
        }
        
        
        //AGGIUNGERE COORDINATE
        InputStream is = getClass().getClassLoader().getResourceAsStream("coordinate_caselle.txt");
        Reader file_caselle = new InputStreamReader(is);
        //FileReader file_caselle = new FileReader("/home/pietro/NetBeansProjects/OCA_Client_FX/src/files/coordinate_caselle.txt");

        BufferedReader br_caselle = new BufferedReader(file_caselle);
        String linea_testo;
        String elementi_stringa[];
        for (int i = 0; i < 64; i++) {
            linea_testo = br_caselle.readLine();
            elementi_stringa = linea_testo.split("\t");
            if (elementi_stringa.length > 3) {
                vettore_caselle[i] = new casella(Integer.parseInt(elementi_stringa[1]), Integer.parseInt(elementi_stringa[2]), elementi_stringa[3]);
            } else if (elementi_stringa.length > 2) {
                vettore_caselle[i] = new casella(Integer.parseInt(elementi_stringa[1]), Integer.parseInt(elementi_stringa[2]));
            } else {
                System.out.println("ERRORE caricamento elemento" + i + "dal file 'coordinate_caselle.txt'");
                System.out.println(linea_testo);
            }
        }
        is.close();
        
        //Inizializzazione Elementi ChatLog (chat_ricevuto, chat_invio, log, griglia_giocatori
        header_chat = new Text("Chat di gruppo:");
        chat_invio = new TextField();
        chat_invio.setPromptText("Scrivi messaggio..");
        chat_ricevuto = new TextArea();
        chat_ricevuto.setEditable(false);
        //chat_ricevuto.appendText("Welcome\n");
        
        header_log = new Text("Eventi di gioco:");
        log = new TextArea();
        log.setEditable(false);
        log.setPrefRowCount(4);
        log.setMinHeight(120);
        
        griglia_giocatori = new GridPane();
        Text header_player = new Text("Giocatori Online:");
        Text header_player1 = new Text("");
        griglia_giocatori.add(header_player, 0, 0);
        griglia_giocatori.add(header_player1, 1, 0);
        
        
        lancia_dadi.setFont(Font.font("Trebuchet MS", 13));
        header_chat.setFont(Font.font("Trebuchet MS", 15));
        chat_invio.setFont(Font.font("Trebuchet MS", 15));
        chat_ricevuto.setFont(Font.font("Trebuchet MS", 12));
        header_log.setFont(Font.font("Trebuchet MS", 15));
        log.setFont(Font.font("Trebuchet MS", 12));
        header_player.setFont(Font.font("Trebuchet MS", 15));

    }

    private Scene campo_gioco() {
        
        //Creazione contenitore (per gli oggetti a destra)
        GridPane chatlog = new GridPane();
        chatlog.setAlignment(Pos.TOP_LEFT);
        chatlog.setMaxWidth(300);
        chatlog.setVgap(15);
        chatlog.setPadding(new Insets(20, 20, 20, 20));
        

        //Aggiunta Elementi chatlog
        chatlog.add(header_chat, 0, 0);
        chatlog.add(chat_invio, 0, 1);
        chatlog.add(chat_ricevuto, 0, 2);
        
        chatlog.add(header_log, 0, 3);
        chatlog.add(log, 0, 4);
        
        chatlog.add(lancia_dadi, 0, 5);
        
        chatlog.add(griglia_giocatori, 0, 7);
        GridPane.setMargin(lancia_dadi, new Insets(5,10,5,5));
        
        chat_invio.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode()==KeyCode.ENTER){
                   sessione.chat_to_server(chat_invio.getText());
                   chat_ricevuto.appendText(String.format("Io: %s\n", chat_invio.getText()));
                   chat_invio.clear();
                } 
            }
        });

        //Opzioni dimensione campo
        Text headerDimRadio = new Text("Dimensione campo:");
        RadioButton button_small = new RadioButton("S");
        RadioButton button_medium = new RadioButton("M");
        RadioButton button_large = new RadioButton("L");
        ToggleGroup bottoni = new ToggleGroup();
        button_small.setToggleGroup(bottoni);
        button_medium.setToggleGroup(bottoni);
        button_large.setToggleGroup(bottoni);
        bottoni.selectToggle(button_medium);
        
        button_small.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                 Client_Lib.ridimensiona_interfaccia(0.5);
            }
        });
        button_medium.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                 Client_Lib.ridimensiona_interfaccia(0.7);
            }
        });
        button_large.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                 Client_Lib.ridimensiona_interfaccia(1.0);
            }
        });
        
        //Creazione TopBar
        HBox bottom = new HBox(headerDimRadio, button_small, button_medium, button_large);
        bottom.setPadding(new Insets(0,20,2,20));
        bottom.setSpacing(30);
        //HBox.setMargin(lancia_dadi, new Insets(0,0,0,150));

        //Layout pagina
        BorderPane pagina = new BorderPane();

        pagina.setCenter(terreno);
        pagina.setRight(chatlog);
        pagina.setBottom(bottom);
        chat_invio.setFocusTraversable(false);
        lancia_dadi.requestFocus();
           
        BackgroundFill myBF = new BackgroundFill(Color.ALICEBLUE, new CornerRadii(1), null);
        
        //BackgroundImage myBI= new BackgroundImage(new Image("Img/background.jpg", 500,500,true, false),
        //BackgroundRepeat.ROUND, BackgroundRepeat.ROUND, BackgroundPosition.DEFAULT,
        //  BackgroundSize.DEFAULT);
        
        pagina.setBackground(new Background(myBF));
        
        return new Scene(pagina);
    }

    private Scene menu_gioco(Stage finestra_principale) {
        //Creazione e Caricamento Layout Menu
        GridPane griglia = new GridPane();
        griglia.setPadding(new Insets(35, 20, 25, 20));
        
        //Contenitori
        GridPane pannello_radio = new GridPane(); //Griglia Radiobutton
        GridPane pannello_play = new GridPane(); //Griglia "conferma", "gioca"
        pannello_radio.setVgap(5);
        Text scelta_nome_text = new Text("Nome giocatore:");
        Text scelta_colore_text = new Text("Colore pedina:");
        Text serverIP_text = new Text("ipServer:port");
        TextArea scelta_nome_area = new TextArea();
        TextArea serverIP_area = new TextArea("127.0.0.1:1337");
        
        scelta_nome_text.setFont(Font.font("Trebuchet MS", 15));
        scelta_colore_text.setFont(Font.font("Trebuchet MS", 15));
        serverIP_text.setFont(Font.font("Trebuchet MS", 15));
        scelta_nome_area.setFont(Font.font("Trebuchet MS", 15));
        serverIP_area.setFont(Font.font("Trebuchet MS", 15));


        
        scelta_nome_area.setPrefWidth(220);
        scelta_nome_area.setPrefRowCount(1);
        serverIP_area.setPrefWidth(220);
        serverIP_area.setPrefRowCount(1);
        pannello_play.setHgap(10);
        pannello_play.setVgap(10);

        Button play = new Button("Accedi");
        play.setFont(Font.font("Trebuchet MS", 15));
        play.setPrefWidth(220);
                
        //Radiobutton
        ToggleGroup scelta_colore = new ToggleGroup();

        pannello_radio.add(scelta_colore_text, 0, 0);
        RadioButton button_red = new RadioButton("\t\t\t");
        button_red.setToggleGroup(scelta_colore);
        button_red.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(10), new Insets(0.0))));
        pannello_radio.add(button_red, 0, 1);
        RadioButton button_blu = new RadioButton("\t\t\t");
        button_blu.setToggleGroup(scelta_colore);
        button_blu.setBackground(new Background(new BackgroundFill(Color.BLUE, new CornerRadii(10), new Insets(0.0))));
        pannello_radio.add(button_blu, 1, 1);
        RadioButton button_yellow = new RadioButton("\t\t\t");
        button_yellow.setToggleGroup(scelta_colore);
        button_yellow.setBackground(new Background(new BackgroundFill(Color.YELLOW, new CornerRadii(10), new Insets(0.0))));
        pannello_radio.add(button_yellow, 0, 2);
        RadioButton button_black = new RadioButton("\t\t\t");
        button_black.setToggleGroup(scelta_colore);
        button_black.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(10), new Insets(0.0))));
        pannello_radio.add(button_black, 1, 2);
        RadioButton button_white = new RadioButton("\t\t\t");
        button_white.setToggleGroup(scelta_colore);
        button_white.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), new Insets(0.0))));
        pannello_radio.add(button_white, 0, 3);
        RadioButton button_green = new RadioButton("\t\t\t");
        button_green.setToggleGroup(scelta_colore);
        button_green.setBackground(new Background(new BackgroundFill(Color.GREEN, new CornerRadii(10), new Insets(0.0))));
        pannello_radio.add(button_green, 1, 3);
        RadioButton button_azure = new RadioButton("\t\t\t");
        button_azure.setToggleGroup(scelta_colore);
        button_azure.setBackground(new Background(new BackgroundFill(Color.AQUA, new CornerRadii(10), new Insets(0.0))));
        pannello_radio.add(button_azure, 0, 4);
        RadioButton button_violet = new RadioButton("\t\t\t");
        button_violet.setToggleGroup(scelta_colore);
        button_violet.setBackground(new Background(new BackgroundFill(Color.VIOLET, new CornerRadii(10), new Insets(0.0))));
        pannello_radio.add(button_violet, 1, 4);
        scelta_colore.selectToggle(button_red);
        GridPane.setMargin(button_red, new Insets(0,20,0,0));
        
        
        pannello_play.add(scelta_nome_text, 0, 0);
        pannello_play.add(scelta_nome_area, 1, 0);
        pannello_play.add(serverIP_text, 0, 1);
        pannello_play.add(serverIP_area, 1, 1);
        pannello_play.add(play, 1, 2);
        GridPane.setHalignment(play, HPos.CENTER);
        GridPane.setMargin(pannello_play, new Insets(15,0,10,0));
        
        play.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("CLICKED_play");

                //Verifica stinga IP e Porta
                String ip;
                int porta;

                Pattern pattern = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}:\\d{1,5}");

                String stringaCompleta = serverIP_area.getText().trim().replace(" ", "");

                Matcher matcher = pattern.matcher(stringaCompleta);

                if (!matcher.matches()) {
                    serverIP_area.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(2))));
                    serverIP_text.setText("malformed IP:port address");
                    return;
                }
                ip = stringaCompleta.split(":")[0];
                porta = Integer.parseInt(stringaCompleta.split(":")[1]);

                Toggle scelta = scelta_colore.getSelectedToggle();
                Color colore_scelto;
                String colore_stringa; 
                if (scelta.equals(button_black)) {
                    colore_scelto=Color.BLACK;
                    colore_stringa = "-black";
                } else if (scelta.equals(button_red)) {
                    colore_scelto=Color.RED;
                    colore_stringa = "-red";
                } else if (scelta.equals(button_blu)) {
                    colore_scelto=Color.BLUE;
                    colore_stringa = "-blue";
                } else if (scelta.equals(button_white)) {
                    colore_scelto=Color.WHITE;
                    colore_stringa = "-white";
                } else if (scelta.equals(button_yellow)) {
                    colore_scelto=Color.YELLOW;
                    colore_stringa = "-yellow";
                } else if (scelta.equals(button_green)) {
                    colore_scelto=Color.GREEN;
                    colore_stringa = "-green";
                } else if (scelta.equals(button_violet)) {
                    colore_scelto=Color.VIOLET;
                    colore_stringa = "-violet";
                } else if (scelta.equals(button_azure)) {
                    colore_scelto=Color.AZURE;
                    colore_stringa = "-azure";
                } else {
                    colore_scelto=Color.TRANSPARENT;
                    colore_stringa = "";
                    System.out.println("ERRORE nella selezione radiobutton");
                }

                Boolean esito = null;
                try {
                    esito = sessione.proposta_pedine(colore_scelto, scelta_nome_area.getText(), ip, porta);
                } catch (IllegalArgumentException e) {
                    serverIP_area.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(2))));
                    serverIP_text.setText("port out of range 0-65535");
                    return;
                } catch (IOException e) {
                    serverIP_area.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(2))));
                    serverIP_text.setText("connection failed");
                    return;
                }

                if (esito == true) {
                    sessione.setta_pedina(colore_scelto, scelta_nome_area.getText());
                    finestra_principale.getIcons().clear();
                    finestra_principale.getIcons().add(new Image(String.format("paperella%s.png", colore_stringa)));
                    finestra_principale.setTitle(String.format("Gioco dell'Oca - Giocatore: %s", scelta_nome_area.getText()));
                    finestra_principale.setScene(campo_gioco());
                    finestra_principale.show();
                }
                else{
                    Stage stage_conferma = new Stage();
                    stage_conferma.setTitle("colore già scelto");
                    Button ok= new Button("OK");
                    Button no= new Button("NO");
                    Text procedi= new Text("Procedere comunque?");
                    BorderPane pane_conferma = new BorderPane ();
                    pane_conferma.setTop(procedi);
                    pane_conferma.setLeft(no);
                    pane_conferma.setRight(ok);
                    stage_conferma.setScene(new Scene(pane_conferma));
                    stage_conferma.show();
                    ok.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            sessione.setta_pedina(colore_scelto, scelta_nome_area.getText());
                            finestra_principale.setScene(campo_gioco());
                            finestra_principale.show();
                            stage_conferma.close();
                        }
                    });
                    no.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            stage_conferma.close();
                        }
                    });
                }
            }

        });

        griglia.add(pannello_radio, 0, 2);
        griglia.add(pannello_play, 0, 3);

        //BackgroundFill myBF = new BackgroundFill(Color.BEIGE, new CornerRadii(1), null);
        
        BackgroundImage myBI= new BackgroundImage(new Image("background.jpg", 250,250,false, true),
        BackgroundRepeat.ROUND, BackgroundRepeat.ROUND, BackgroundPosition.DEFAULT,
          BackgroundSize.DEFAULT);
         
        griglia.setBackground(new Background(myBI));

        return new Scene(griglia);

    }

    public static void append_chatlog(String Messaggio, boolean chat) {
        //Appendi stringa al chatlog
        if (chat == true) {
            chat_ricevuto.appendText(Messaggio);
        } else {
            log.appendText(Messaggio+"\n");
        }
    }

    public static void aggiungi_giocatore_griglia(String nome_giocatore, ImageView pedina) {
        Text text_giocatore = new Text(nome_giocatore);
        griglia_giocatori.add(text_giocatore, 0, lista_giocatori.size() + 1);
        griglia_giocatori.add(pedina, 1, lista_giocatori.size() + 1);
    }
    
    public static void rimuovi_giocatore_griglia(String nome_giocatore) {
        
         ObservableList<Node> childrens = griglia_giocatori.getChildren();
         Node nodeToDelete = null;
         String testoNodo = null;
         
         for (Node node : childrens) {
             try{
                testoNodo = ((Text)node).getText();
             } catch(Exception e){}
             
             if(testoNodo != null && testoNodo.equals(nome_giocatore)){
                 nodeToDelete = node;
                 break;
             }
         }
         
        if(nodeToDelete != null)
            griglia_giocatori.getChildren().remove(nodeToDelete);
    }

    public static void ridisegna_giocatore_griglia() {
        
        griglia_giocatori.getChildren().clear();
         
        Text header_player = new Text("Giocatori Online:");
        Text header_player1 = new Text("");
        griglia_giocatori.add(header_player, 0, 0);
        griglia_giocatori.add(header_player1, 1, 0);
        int i = 1;
        
        for (Giocatore player : lista_giocatori) {
            ImageView miniatura_pedina = new ImageView(player.carica_im_papera(30, 30));
            Text text_giocatore = new Text(player.NomePedina);
            griglia_giocatori.add(text_giocatore, 0, ++i);
            griglia_giocatori.add(miniatura_pedina, 1, i);
        
            //OCA_Client_FX.aggiungi_giocatore_griglia(player.NomePedina, miniatura_pedina);
        }
         
    }
            
    public static void aggiungi_giocatore_al_campo(Group pedina, int x, int y, int angolo) {
        pedina.relocate(x, y);
        pedina.rotateProperty().setValue(angolo);
        terreno.getChildren().add(pedina);
    }

     public static void rimuovi_giocatore_dal_campo(Group pedina) {
         terreno.getChildren().remove(pedina);
    }
     
    public static void ridimensiona_terreno() {
        //Sostituisci l'immagine
        terreno.getChildren().clear();
        terreno.getChildren().add(new ImageView(new Image("Tab2.jpg", (int) (terreno_X * scala), (int) (terreno_Y * scala), true, true)));
        
        //Riposiziona dadi
        for (int i = 0; i < dadi[0].length; i++) {
            for (int j = 0; j < 2; j++) {
                if (j==0) {
                   dadi[j][i].setTranslateX((int)(terreno_X*scala/2.5));
                   dadi[j][i].setTranslateY((int)(terreno_Y*scala/2.3)); 
                }
                else{
                    dadi[j][i].setTranslateX((int)(terreno_X*scala/2.5)+90);
                    dadi[j][i].setTranslateY((int)(terreno_Y*scala/2.3)); 
                }
                terreno.getChildren().add(dadi[j][i]);
            }
        }
    }
    
    public static void abilita_lancia_dadi(Boolean ab){
        if(ab==true && !qualcunoHaVinto){
            lancia_dadi.setBackground(new Background(new BackgroundFill(Color.GREENYELLOW, new CornerRadii(10), new Insets(0.0))));
            lancia_dadi.setDisable(false);
            lancia_dadi.setText("\tLancia dadi\t");
        }
        else{
            lancia_dadi.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(10), new Insets(0.0))));
            lancia_dadi.setDisable(true);
            lancia_dadi.setText("Non è il tuo turno");
        }
    }
    
    public static void set_faccia_dado(int faccia1, int faccia2){
        for (int i = 0; i < dadi[0].length; i++) {
            if (i==faccia1) {
                dadi[0][i].setVisible(true);
            }
            else{
                dadi[0][i].setVisible(false);
            }
            if (i==faccia2) {
                dadi[1][i].setVisible(true);
            }
            else{
                dadi[1][i].setVisible(false);
            }
        }
    }
     
}