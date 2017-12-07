package Messages;

import java.io.Serializable;

/**
 * @author Michele
 */
public enum TypeMessage implements Serializable {
    CheckTurn, //Chiamata periodica verso il server per sapere se è il proprio turno (viene ritornata anche la lista delle pedine)
    Gioca, //Chiamata al server quando il client fa una mossa
    NewPedina, //Chimata al server quando si connette un nuovo client
    Chat, //Chimata al server quando si invia un messaggio in chat
    Exit //Chiamata in caso di chiusura del CLient
}
