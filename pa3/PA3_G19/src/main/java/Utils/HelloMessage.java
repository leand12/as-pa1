package Utils;

/**
*
* Hello Message class
*/
public class HelloMessage extends Message {
    
    /* Server ID. */
    int ID;

    String function;

    /**
    *
    * Hello Message constructor
    * 
     * @param ID
     * @param type
     * @param function primary/secondary
    */
    public HelloMessage(int ID, String type, String function) {
        super(CodeMessages.HELLO);
        this.ID = ID;
        this.type = type;
        this.function = function;
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
    
    /**
    *
    * get function
    * 
    * @return function
    */
    public String function(){
        return function;
    }
    
    
}
