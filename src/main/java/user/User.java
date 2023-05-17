package user;

import stock.Portfolio;

import java.util.ArrayList;

public class User {

    private String person_id;
    private String password;
    private String email;
    private String pasSalt;

    private ArrayList<Portfolio> portfolios;
    public User() {

        password = getPassword();
        person_id = getPerson_id();
        portfolios = new ArrayList<>();
    }

    public User(String person_id, String password) {
        this.person_id = person_id;
        this.password = password;
        portfolios = new ArrayList<>();
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double calculateTotal() {
        Double total = 0.0;
        for (Portfolio portfolio : portfolios) {
            total = portfolio.calculateTotal();
        }
        return total;
    }

    public String getPasSalt() {
        return pasSalt;
    }



    public void setPasSalt(String pasSalt) {
        this.pasSalt = pasSalt;
    }

}
