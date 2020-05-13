/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oca_client_fx;

import java.util.ArrayList;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import static oca_client_fx.OCA_Client_FX.scala;

/**
 *
 * @author pietro
 */
public class Giocatore {
            
    public int IdPedina;
    public String NomePedina;
    public int NumCasella;
    public Group pedina;
    public Color colore;
    
    public static final int papera_X=100;
    public static final int papera_Y=80;
    
    private String dir_pap;
    
    public Giocatore(int IdPedina, String NomePedina, Color colore){
        this.colore=colore;
        this.IdPedina=IdPedina;
        this.NomePedina=NomePedina;
        this.NumCasella = 0;
        this.pedina=new Group();
        
        ImageView vi_pap= new ImageView(carica_im_papera());
        pedina.getChildren().add(vi_pap);
    }
    
    public static void ridimensiona_pedine(ArrayList<Giocatore> lista_giocatori){
        for (int i = 0; i < lista_giocatori.size(); i++) {
            lista_giocatori.get(i).pedina.getChildren().clear();
            ImageView vi_pap = new ImageView(lista_giocatori.get(i).carica_im_papera());
            lista_giocatori.get(i).pedina.getChildren().add(vi_pap);
        }
    }
    
    public Image carica_im_papera(int W, int H){
        
         if (colore==Color.BLACK || colore.toString().equals(Color.BLACK.toString())) {
            dir_pap="paperella-black.png";
        }
        else if(colore == Color.RED || colore.toString().equals(Color.RED.toString())){
            dir_pap="paperella-red.png";
        }
        else if (colore == Color.BLUE || colore.toString().equals(Color.BLUE.toString())) {
            dir_pap="paperella-blue.png";
        }
        else if (colore == Color.WHITE || colore.toString().equals(Color.WHITE.toString())) {
            dir_pap="paperella-white.png";
        }
        else if (colore == Color.YELLOW || colore.toString().equals(Color.YELLOW.toString())) {
            dir_pap="paperella-yellow.png";
        }
        else if (colore == Color.GREEN || colore.toString().equals(Color.GREEN.toString())) {
            dir_pap="paperella-green.png";
        }
        else if (colore == Color.VIOLET || colore.toString().equals(Color.VIOLET.toString())) {
            dir_pap="paperella-violet.png";
        }
        else if (colore == Color.AZURE || colore.toString().equals(Color.AZURE.toString())) {
            dir_pap="paperella-azure.png";
        }
        else{
            dir_pap="paperella.png";
        }
        return new Image (dir_pap, W, H, true, true);
    }
    
    private Image carica_im_papera(){
        return this.carica_im_papera((int) (papera_X*scala), (int) (papera_Y*scala));       
    }
}
