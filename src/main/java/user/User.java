package user;
public class User {
    private String person_id; // personnummer
    private String password;
    private String pasSalt;
    public User(String person_id, String password) {
        this.person_id = person_id;
        this.password = password;
    }
    public String getPerson_id() {
        return person_id;
    }
    public void setPerson_id(String person_id) {
        this.person_id = person_id;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPasSalt() {
        return pasSalt;
    }
    public void setPasSalt(String pasSalt) {
        this.pasSalt = pasSalt;
    }

}
