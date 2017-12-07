package Domain;

/**
 * @author Michele
 */
public class Casella {
    
    //<editor-fold defaultstate="collapsed" desc="Proprietà">
    
    //Indica il numero della casella sul campo di gioco
    protected int NumCasella;
    
    protected EffettoCasella Effetto;
    
    //Indicano le coordinate nelle quali verranno disegnate le pedine
    protected int Xcoord;
    protected int Ycoord;
    //</editor-fold>
    
    public Casella(int numCasella)
    {   
        this.NumCasella = numCasella;
    }
    
    public void SetEffettoCasella(EffettoCasella effetto)
    {
        this.Effetto = effetto;
    }
    
    public int GetNumCasella()
    {
        return NumCasella;
    }
}
