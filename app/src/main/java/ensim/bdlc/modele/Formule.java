package ensim.bdlc.modele;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Jonathan Daumont on 21/10/2017.
 */

public class Formule {

    private int id;
    private String nom;
    private float prix_vente;
    private int stock;
    private float prix_achat;
    private Bitmap picture;
    private ArrayList<Article> list_article;
    private int quantite;

    public Formule(String nom){
        this.nom = nom;
    }

    public Formule(int id,String nom,float prix_vente,int stock,float prix_achat){
        this.id = id;
        this.nom = nom;
        this.prix_vente = prix_vente;
        this.stock = stock;
        this.prix_achat = prix_achat;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getPrix_vente() {
        return prix_vente;
    }

    public void setPrix_vente(float prix_vente) {
        this.prix_vente = prix_vente;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public float getPrix_achat() {
        return prix_achat;
    }

    public void setPrix_achat(float prix_achat) {
        this.prix_achat = prix_achat;
    }


    public ArrayList<Article> getList_article() {
        return list_article;
    }

    public void setList_article(ArrayList<Article> list_article) {
        this.list_article = list_article;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
}
