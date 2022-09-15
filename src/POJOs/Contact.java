package POJOs;
/**This class will store data for contacts. */
public class Contact {
    private int contactID;
    private String email;
    private String name;
    /**Standard constructor. */
    public Contact(int id, String email, String name){
        this.setID(id);
        this.setEmail(email);
        this.setName(name);
    }
    /**Determines how to show this object in a table. */
    public String toString(){
        return name;
    }
    //getters and setters
    /**Allows changing this object's Contact ID. */
    public void setID(int id){
        this.contactID = id;
    }
    /**Returns this object's Contact ID.*/
    public int getID() {
        return this.contactID;
    }
    /**Allows changing this object's email field. */
    public void setEmail(String email) {
        this.email = email;
    }
    /**Returns this object's name value. */
    public String getName() {
        return name;
    }
    /**Allows changing this object's name field. */
    public void setName(String name) {
        this.name = name;
    }
}
