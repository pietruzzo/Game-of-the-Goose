package Domain;

import java.util.Random;

public class Dado
{
    //Proprietà
    private Random generatore;
    private int facce;
  
    // Metodo Costruttore inizializzato con il numero di facce del dado
    public Dado(int s)
    {
      facce = s;
      // Generatore di numeri casuali
      generatore = new Random();
    }

    // Metodo costruttore del dado classico con 4 facce
    public Dado(){
      facce = 4;
      // Generatore di numeri casuali
      generatore = new Random();
    }

    // Metodo che simula il lancio del dado
    public int lancia()
    {
        return 1 + generatore.nextInt(facce);
    }
}