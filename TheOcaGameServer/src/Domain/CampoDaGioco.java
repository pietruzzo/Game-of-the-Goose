package Domain;

import java.util.*;

/**
 * @author Michele
 */
public class CampoDaGioco {
    
    //<editor-fold defaultstate="collapsed" desc="Proprietà">
    private List<Casella> Caselle;
    private List<Pedina> Pedine;
    private List<ChatMessage> ChatMessages;
    private Pedina Turno; //Rappresenta la pedina che sta giocando
    private Dado Dado;
    private int NextIdPedina;
    //</editor-fold>
    
    public CampoDaGioco()
    {
        Caselle = new ArrayList<>();
        Pedine = new ArrayList<>();
        ChatMessages = new ArrayList<>();
        this.Dado = new Dado(6);
        this.NextIdPedina = 1;
        
        //Aggiunge le 63 + 1(inizio, casella 0)  caselle al campo di gioco
        for(int i = 0; i <= 63; i++)
        {
            Casella casella = new Casella(i);
            
            switch(i)
            {
                case 5: case 58:
                    casella.SetEffettoCasella(EffettoCasella.TornaInizio);
                    break;
                
                case 6:
                    casella.SetEffettoCasella(EffettoCasella.RaddoppiaDado);
                    break;
                    
                case 9: case 23: case 41: case 50: case 59:
                    casella.SetEffettoCasella(EffettoCasella.RilanciaDado);
                    break;
                    
                case 14: case 36: case 45: case 54:
                    casella.SetEffettoCasella(EffettoCasella.TornaPrecedente);
                    break;
                    
                case 19: case 27: case 31: case 32: case 42: case 52: 
                    casella.SetEffettoCasella(EffettoCasella.SaltaTurno);
                    break;
                    
                case 63:
                    casella.SetEffettoCasella(EffettoCasella.Vittoria);
                    break;
                    
                default:
                    casella.SetEffettoCasella(EffettoCasella.NoEffect);
                    break;
            }
            
            Caselle.add(casella);
        }
    }
    
    //Ritorna il numero di caselle presenti nel campo da gioco 
    public int NumeroCaselle()
    {
        return Caselle.size();
    }
    
    //Ritorna il numero di pedine presenti nel campo da gioco 
    public int NumeroPedine()
    {
        return Pedine.size();
    }
    
    public List<Casella> GetCaselle()
    {
        return Caselle;
    }
    
    public List<Pedina> GetPedine()
    {
        return Pedine;
    }
    
    public Pedina GetTurno()
    {
        return Turno;
    }
    
    //Crea e aggiunge una nuova pedina, ritornando l'id 
    public int AddPedina(String nomePedina, String colorePedina)
    {
        //Recupera l'id della pedina da inserire
        int idPedina =  this.NextIdPedina;
        
        //Incrementa l'id per la prossima pedina
        NextIdPedina++;
                
        //Recupera la casella iniziale
        Casella casellaPartenza = this.Caselle.get(0);
        
        //Crea e aggiunge la nuova pedina
        this.Pedine.add(new Pedina(idPedina, nomePedina, casellaPartenza, colorePedina));
        
        return idPedina;
    }
    
    //Rimuove la pedina dalla lista dei giocatori
    public void RemovePedina(int idPedina)
    {
        //Se un client abbandona durante il suo turno, questo viene incrementato
        if(Turno.GetId() == idPedina)
            this.IncrementaTurno();
        
        //Poi la pedina viene eliminata
        this.Pedine.removeIf(x -> x.GetId() == idPedina);
        
        //Se non rimane nessuna pedina il turno viene settato a null
        if(this.NumeroPedine() == 0)
            this.Turno = null;
    }
    
    public GiocaResult Gioca()
    {
        String messaggio = "";
        GiocaResult result = new GiocaResult();
        
        //Ricavo la posizione attuale della pedina
        Casella casellaAttuale = Turno.GetCasellaAttuale();
        int numeroCasellaAttuale = casellaAttuale.GetNumCasella();

        //Tiro il dado per valutare lo spostamento della pedina e lo "applico"
        result.dado1 = Dado.lancia();
        result.dado2 = Dado.lancia();
        
        int incremento = result.dado1 + result.dado2;
        
        if(casellaAttuale.Effetto == EffettoCasella.RaddoppiaDado)
        {
            messaggio = String.format("Hai fatto %d, raddoppia a %d!\nVai alla casella %d." ,  incremento, incremento*2, numeroCasellaAttuale + incremento*2);
            incremento += incremento;
        }
        else if(casellaAttuale.Effetto == EffettoCasella.SaltaTurno)
        {
            if(Turno.TurniDaSaltare > 0)
            {   //Non dovrebbe mai capitarci
                Turno.TurniDaSaltare --;
                this.IncrementaTurno();
                result.messaggio = "Questo turno sei fermo";
                return result;
            }
        }
        
        numeroCasellaAttuale += incremento;
        
        if(numeroCasellaAttuale > 63)
            numeroCasellaAttuale = 63 - (numeroCasellaAttuale-63);
            
        Casella nuovaCasella = this.GetCasella(numeroCasellaAttuale);
        
        if(nuovaCasella.Effetto == EffettoCasella.TornaInizio)
        {
            messaggio = String.format("Hai fatto %d, vai alla casella %d,\nma si torna all'inizio!", incremento, numeroCasellaAttuale);
            nuovaCasella = this.GetCasella(1);
        }
        if(nuovaCasella.Effetto == EffettoCasella.SaltaTurno)
        {
            messaggio = String.format("Hai fatto %d,vai alla casella %d,\ndovrai saltare un turno!", incremento, numeroCasellaAttuale);
            Turno.TurniDaSaltare = 1;
        }
        else if(nuovaCasella.Effetto == EffettoCasella.Vittoria)
        {
            messaggio = String.format("Hai fatto %d, e hai vinto!!", incremento);
        }
        
        if(nuovaCasella.Effetto != EffettoCasella.TornaPrecedente)
            Turno.spostaPedina(nuovaCasella);
        else
            messaggio = String.format("Hai fatto %d, vai alla casella %d,\nma devi tornare alla casella precedente!", incremento, numeroCasellaAttuale);
        
        if(nuovaCasella.Effetto != EffettoCasella.RilanciaDado)
            this.IncrementaTurno();
        else
            messaggio = String.format("Hai fatto %d, vai alla casella %d,\ne rilanci pure!", incremento, numeroCasellaAttuale);
        
        if(messaggio.equals(""))
            messaggio = String.format("Hai fatto %d, vai alla casella %d! ", incremento, numeroCasellaAttuale);
        
        result.messaggio = messaggio;
        return result;
    }
    
    //Ritorna l'id della nuova pedina turno
    public int IncrementaTurno()
    {
        int numeroPedine = this.NumeroPedine();
        
        if(this.Turno != null)
        {
            int indiceTurno = this.Pedine.indexOf(Turno);
            
            if(indiceTurno == numeroPedine-1)
                Turno = this.Pedine.get(0);
            else 
                Turno = this.Pedine.get(indiceTurno+1);
        }
        else if(numeroPedine != 0)
        {
            Turno = this.Pedine.get(0);
        }
        else
            Turno = null;
        
        if(Turno.CasellaAttuale.Effetto == EffettoCasella.SaltaTurno && Turno.TurniDaSaltare > 0)
        {
            Turno.TurniDaSaltare --;
            IncrementaTurno();
        }
        
        return Turno.IdPedina;
    }
    
    //Ritorna la casella avente il numero specificato, se non la trova viene ritornata la prima casella
    public Casella GetCasella(int numeroCasella)
    {
       return this.Caselle.stream().filter(x -> x.GetNumCasella() == numeroCasella)
                            .findFirst()
                            .orElse(Caselle.get(0));
    }
    
    //Aggiunge un messaggio alla lista 
    public void AddChatMessage(String message, int idPedina)
    {
        //Recupera l'id del messaggio da inserire
        int idMessage = this.ChatMessages.size()+1;
        Pedina pedina = this.GetPedinaById(idPedina);
        
        //Crea e aggiunge il nuovo messaggio
        this.ChatMessages.add(new ChatMessage(idMessage, message, pedina));
    }
    
    //Ritorna il testo che verrà mostrato nella textarea della chat del client
    public String GetChatMessage(int idPedina, int[] lastIdChatMessageReadArray)
    {
        //Viene passato come array perchè in java non esiste il passaggio per riferimento (mah....)
        int lastIdChatMessageRead = lastIdChatMessageReadArray[0];
        
        //Recupera i messaggi da mostrare lato client
        ChatMessage[] messages = this.ChatMessages.stream()
                                        .filter(x -> x.Pedina.IdPedina != idPedina && x.IdChatMessage > lastIdChatMessageRead)
                                        .toArray(ChatMessage[]::new);
        
        //Costruisce la stringa da tornare
        String chatMessage = "";
        
        for(int i=0; i < messages.length; i++)
        {
            chatMessage += String.format("%s: %s\n", messages[i].Pedina.Nome, messages[i].Message);
        }
        
        //Imposto il valore dell'ultimo messaggio letto dal client
        if(messages.length > 0)
            lastIdChatMessageReadArray[0] = messages[messages.length- 1].IdChatMessage;
        
        return chatMessage;
    }
    
    //Torna la Pedina dato l'id
    public Pedina GetPedinaById(int idPedina)
    {
        return this.Pedine.stream().filter(x -> x.IdPedina == idPedina)
                                .findFirst()
                                .orElse(null);
    }
}
