package Domain;

/**
 * @author Michele
 */
public class Pedina {
    
    //<editor-fold defaultstate="collapsed" desc="Proprietà">
    protected int IdPedina;
    protected String Nome;
    protected Casella CasellaAttuale;
    protected String Colore;
    public int TurniDaSaltare;
    //</editor-fold>
    
    public Pedina(int idPedina, String nomePedina, Casella casella, String colorePedina)
    {
        this.IdPedina = idPedina;
        this.Nome = nomePedina;
        this.CasellaAttuale = casella;
        this.Colore = colorePedina;
        this.TurniDaSaltare = 0;
    }
        
    //Ritorna l'id della pedina
    public int GetId()
    {
        return this.IdPedina;
    }
    
    //Ritorna il nome della pedina
    public String GetNome()
    {
        return this.Nome;
    }
    
    public Casella GetCasellaAttuale()
    {
        return this.CasellaAttuale;
    }
    
    //Ritorna il colore della pedina
    public String GetColore()
    {
        return this.Colore;
    }
    
    //Sposta la pedina nella casella selezionata
    public void spostaPedina(Casella casella)
    {
        this.CasellaAttuale = casella;
    }
}
