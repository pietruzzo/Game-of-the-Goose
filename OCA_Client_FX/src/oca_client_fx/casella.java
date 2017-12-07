/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oca_client_fx;

import static oca_client_fx.OCA_Client_FX.vettore_caselle;

/**
 *
 * @author pietro
 */
public class casella {
    private String effetto;
    private int x;
    private int y;
    public int n_papere;
    
    public casella(int x, int y, String effetto){
        this.x=x;
        this.y=y;
        this.effetto=effetto;
        this.n_papere=0;
    }
    
    public casella(int x, int y){
        this.x=x;
        this.y=y;
        this.effetto="No Effect";
    }
    
    public int getX(){
        return x;
    }
    
    public int getY(){
        return y;
    }
    
    static public void azzera_contatori_caselle(){
        for (int i = 0; i < vettore_caselle.length; i++) {
            vettore_caselle[i].n_papere=0;
        }
    }
    
    
    
}
