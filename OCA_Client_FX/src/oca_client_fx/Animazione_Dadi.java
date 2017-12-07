/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oca_client_fx;

import java.util.concurrent.ThreadLocalRandom;
import javafx.animation.AnimationTimer;

/**
 *
 * @author pietro
 */
public class Animazione_Dadi extends AnimationTimer {

    private final int indice_faccia1;
    private final int indice_faccia2;
    private long incremento;

    Animazione_Dadi(int faccia1, int faccia2) {
        super();
        this.indice_faccia1 = faccia1-1;
        this.indice_faccia2 = faccia2-1;
        incremento = 0;
        this.start();
    }

    @Override
    public void handle(long now) {
        incremento++;
        if (incremento % 10 == 0) {
            int rn1 = ThreadLocalRandom.current().nextInt(0, 5);
            int rn2 = ThreadLocalRandom.current().nextInt(0, 5);
            OCA_Client_FX.set_faccia_dado(rn1, rn2);
        } else if (incremento > 90) {
            OCA_Client_FX.set_faccia_dado(indice_faccia1, indice_faccia2);
            //System.out.println("Dadi: "+ (indice_faccia1+1) +", "+ (indice_faccia2+1));
            stop();
        }
    }

}
