package Utils;

import java.io.Serializable;
import java.util.List;

/*
 *
 * Message type class
 */
public class Message implements Serializable {

    /* Message code. */
    CodeMessages code;
    
    /* Requests being processed by a server. */
    List<ServerStatusMessage> serversStatus;

    /* Generic type for identification */
    String type;
    /* Sender port number. */
    int port;

    /**
    *
    * Message class constructor
     * @param code
    */
    public Message(CodeMessages code) {
        this.code = code;
    }
    
    /**
    *
    * set message code
    * 
    * @param code
    * @return message
    */
    public Message code(CodeMessages code) {
        this.code = code;
        return this;
    }

    /**
    *
    * get message code
    * 
    * @return code message
    */
    public CodeMessages code() {
        return code;
    }
    
    /**
    *
    * set server status 
    * 
    * @param serversStatus
    * @return message
    */
    public Message serversStatus(List<ServerStatusMessage> serversStatus) {
        this.serversStatus = serversStatus;
        return this;
    }
    
    /**
    *
    * get server status list
    * 
    * @return message
    */
    public List<ServerStatusMessage> serversStatus() {
        return serversStatus;
    }
    
    /**
    *
    * set message type
    * 
    * @param type
    * @return message
    */
    public Message type(String type) {
        this.type = type;
        return this;
    }
    
    /**
    *
    * get message code
    * 
    * @return message
    */
    public String type() {
        return type;
    }
    
    /**
    *
    * set message port
    * 
    * @param port
    * @return message
    */
    public Message port(int port) {
        this.port = port;
        return this;
    }
    
    /**
    *
    * get message port
    * 
    * @return message
    */
    public int port() {
        return port;
    }
}
