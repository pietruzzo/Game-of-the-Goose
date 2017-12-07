package Messages;

import java.io.Serializable;
import java.util.List;

/**
 * @author Michele
 */
public class MessageFromServer implements Serializable{
    public TypeMessage TipoMessaggio;
    public int IdPedina;
    public List<PedinaDTO> PedineDTO;
    public Boolean mioTurno;
    public String ChatMessage;
    public String ServiceMessage;
    public int Dado1;
    public int Dado2;
    
    
    public MessageFromServer(TypeMessage tipoMessaggio, int idPedina, List<PedinaDTO> pedineDTO, Boolean mioTurno, String chatMessage, String messaggio, int dado1, int dado2)
    {
        this.TipoMessaggio = tipoMessaggio;
        this.IdPedina = idPedina;
        this.PedineDTO = pedineDTO;
        this.mioTurno = mioTurno;
        this.ChatMessage = chatMessage;
        
        this.ServiceMessage = messaggio;
        this.Dado1 = dado1;
        this.Dado2 = dado2;
    }
}
