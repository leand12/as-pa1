package Utils;

/**
*
* Heart beat Message
* 
*/
public class HeartBeatMessage extends Message{
    
    
    int ID;
    
    /**
    *
    * Heart beat Message constructor 
    * 
    * @param ID
    */
    public HeartBeatMessage(int ID) {
        super(CodeMessages.HEARTBEAT);
        this.ID = ID;
    }
    
    /**
    *
    * get ID
    * 
    * @return ID
    */
    public int ID() {
        return ID;
    }
}
