/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oca_client_fx;

import javafx.animation.AnimationTimer;
import javafx.scene.Group;

/**
 *
 * @author pietro
 */
public class Traslazione extends AnimationTimer {

    private int x, y, ax, ay, aangolo;
    private Group papera;

    public Traslazione(Group papera, int ax, int ay, int aangolo) {
        super();
        this.x= (int) papera.getLayoutX();
        this.y= (int) papera.getLayoutY();
        this.ax = ax;
        this.ay = ay;
        this.papera = papera;
        this.aangolo= aangolo;
    }

    public void handle(long now) {
        Boolean traslato= false;
        Boolean ruotato= false;
        //Traslazione
        papera.relocate(x, y);
        if (x == ax && y == ay) {
            traslato=true;
        }
        else{
        //Calcolo nuova posizione
        if (x < ax) {
           if(Math.abs(ax-x)<2)
               x = x + 1;
           else
                x = x + 2;
        }
        if (y < ay) {
            if(Math.abs(ay-y)<2)
               y = y + 1;
            else
                y = y + 2;
        }
        if (x > ax) {
            if(Math.abs(ax-x)<2)
                x = x - 1;
            else
                x = x - 2;
        }
        if (y > ay) {
            if(Math.abs(ay-y)<2)
                y = y - 1;
            else
                y = y - 2;
        }
        }
        //Rotazione Rispetto al centro
        int angolo = papera.rotateProperty().intValue();
        
        if (angolo==aangolo) {
            ruotato=true;
            
        }
        else{
            if(Math.abs(angolo-aangolo) > 45)
                angolo = aangolo;
                
            if (angolo<aangolo) {
                if(Math.abs(aangolo-angolo)<3)
                    angolo = angolo + 1;
                else
                    angolo = angolo + 3;
            }
            else if(angolo>aangolo){
                 if(Math.abs(aangolo-angolo)<3)
                    angolo = angolo - 1;
                else
                    angolo = angolo - 3;
                
            }
        }
        papera.rotateProperty().setValue(angolo);
        
        //Verifica uscita ciclo
        if (ruotato==true&& traslato==true) {
            synchronized(this){
            notify();
        }
            stop();
        }
    }
}
