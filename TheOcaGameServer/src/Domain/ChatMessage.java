/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Domain;

/**
 *
 * @author Portatile
 */
public class ChatMessage {
    //<editor-fold defaultstate="collapsed" desc="Proprietà">
    
    //Indica il numero del messaggio
    protected int IdChatMessage;
    
    protected String Message;
    protected Pedina Pedina;
    //</editor-fold>
    
    public ChatMessage(int idChatMessage, String message, Pedina pedina)
    {   
        this.IdChatMessage = idChatMessage;
        this.Message = message;
        this.Pedina = pedina;
    }
}
