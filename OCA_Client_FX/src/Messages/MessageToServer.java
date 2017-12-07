package Messages;

import java.io.Serializable;

/**
 * @author Portatile
 */
public class MessageToServer  implements Serializable{
    
    public TypeMessage TipoMessaggio;
    public PedinaDTO PedinaDTO; //Probabilmente sarebbe sufficiente una stringa con il nome della pedina (il colore lato server non è interessante)
    public String ChatMessage;
    
    public MessageToServer(TypeMessage tipoMessaggio, PedinaDTO pedina, String chatMessage)
    {
        this.TipoMessaggio = tipoMessaggio;
        this.PedinaDTO = pedina;
        this.ChatMessage = chatMessage;
    }
}
