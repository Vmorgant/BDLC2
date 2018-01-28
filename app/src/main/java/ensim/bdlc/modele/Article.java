package ensim.bdlc.modele;

/**
 * Created by jojo- on 10/10/2017.
 */

public class Article {

    private Produit produit;
    private int quantite;

    public Article(){
    }

    public Article(Produit produit,int quantite){
        this.produit = produit;
        this.quantite = quantite;
    }


    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

}
