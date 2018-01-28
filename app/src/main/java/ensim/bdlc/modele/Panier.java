package ensim.bdlc.modele;

import android.widget.Toast;

import java.util.ArrayList;

import ensim.bdlc.Activities.ActivityCommande;

/**
 * Created by jojo- on 10/10/2017.
 */

public class Panier {

    private ArrayList<Article> list_article;
    private ArrayList<Formule> list_formule;
    private float prix_total;
    private String id_panier;
    private String state;
    private String id_owner;

    public Panier(String id_owner){
        this.id_owner = id_owner;
        this.list_article = new ArrayList<>();
        this.list_formule = new ArrayList<>();
        this.prix_total = 0;
    }



    public Panier(String id_panier,float prix_total,String state,String id_owner){
        this.id_panier = id_panier;
        this.prix_total = prix_total;
        this.state = state;
        this.id_owner = id_owner;
    }


    public ArrayList<Article> getList_article() {
        return list_article;
    }

    public void setList_article(ArrayList<Article> list_article) {
        this.list_article = list_article;
    }

    public String getId_panier() {
        return id_panier;
    }

    public void setId_panier(String id_panier) {
        this.id_panier = id_panier;
    }

    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }

    public float getPrix_total() {
        return prix_total;
    }

    public void setPrix_total(float prix_total) {
        this.prix_total = prix_total;
    }

    public void add_article(Article article){
        this.list_article.add(article);
        this.prix_total = this.prix_total + (article.getQuantite() * article.getProduit().getPrix_vente());

        //TODO verifier le solde client si possibilité d'ajout.
    }



    public void add_formule(Formule formule){
        this.list_formule.add(formule);
        this.prix_total = this.prix_total + (formule.getQuantite() * formule.getPrix_vente());

        //TODO verifier le solde client si possibilité d'ajout.
    }

    public int produit_present(String nom){
        int placement = -1;

        for(int cpt=0;cpt<getList_article().size();cpt++){
            if(getList_article().get(cpt).getProduit().getNom().equals(nom)){
                placement = cpt;
            }
        }
        if(placement == -1){
            for(int cpt=0;cpt<getList_formule().size();cpt++){
                if(getList_formule().get(cpt).getNom().equals(nom)){
                    placement = cpt;
                }
            }
        }
        return placement;
    }

    public void set_quantite_article(int placement,int quantite,int quantite_old){

        int diff = quantite - quantite_old ;//ex 2- 5 = -3 ou 0-5
        this.prix_total = this.prix_total + (diff * this.getList_article().get(placement).getProduit().getPrix_vente());

        if(quantite==0){
            getList_article().remove(placement);
        }else{
            this.getList_article().get(placement).setQuantite(quantite);
        }


    }
    public void set_quantite_formule(int placement,int quantite,int quantite_old){

        int diff = quantite - quantite_old ;//ex 2- 5 = -3 ou 0-5
        this.prix_total = this.prix_total + (diff * this.getList_formule().get(placement).getPrix_vente());

        if(quantite==0){
            getList_formule().remove(placement);
        }else{
            this.getList_formule().get(placement).setQuantite(quantite);
        }


    }


    public String getId_owner() {
        return id_owner;
    }

    public void setId_owner(String id_owner) {
        this.id_owner = id_owner;
    }

    public ArrayList<Formule> getList_formule() {
        return list_formule;
    }

    public void setList_formule(ArrayList<Formule> list_formule) {
        this.list_formule = list_formule;
    }
}
