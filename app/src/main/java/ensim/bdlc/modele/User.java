package ensim.bdlc.modele;

/**
 * Created by jojo- on 13/09/2017.
 */

public class User {

    private String username;
    private String num_etu;
    private String categorie;
    private String password;
    private float credit;
    private int version;

    //Constructor
    public User(String num_etu,String password){
        this.num_etu = num_etu;
        this.password = password;
    }

    public User(String username, String num_etu, String categorie, float credit, String password,int version){
        this.username = username;
        this.num_etu = num_etu;
        this.categorie = categorie;
        this.password = password;
        this.credit = credit;
        this.version = version;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNum_etu() {
        return num_etu;
    }

    public void setNum_etu(String num_etu) {
        this.num_etu = num_etu;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public float getCredit() {
        return credit;
    }

    public void setCredit(float credit) {
        this.credit = credit;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
