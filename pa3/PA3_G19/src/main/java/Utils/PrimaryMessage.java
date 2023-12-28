package Utils;

/**
 *
 * create primary message
 */
public class PrimaryMessage extends Message {
    
    int ID;
    
    /**
    *
    * primary message constructor
    * 
    * @param ID
    */
    public PrimaryMessage(int ID) {
        super(CodeMessages.PRIMARY);
        this.ID = ID;
    }
    
    /**
    *
    * get ID
    * 
    * @return message
    */
    public int ID() {
        return ID;
    }
    
}
