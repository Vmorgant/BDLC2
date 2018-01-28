package ensim.bdlc.modele;

import android.graphics.Bitmap;

/**
 * Created by jojo- on 13/09/2017.
 */

public class Produit {

    private int id;
    private String nom;
    private float prix_vente;
    private int stock;
    private float prix_achat;
    private String categorie;
    private String image;
    private Bitmap picture;


    public Produit(int id,String nom,float prix_vente,int stock,float prix_achat,String categorie,String image){
        this.id = id;
        this.nom = nom;
        this.prix_vente = prix_vente;
        this.stock = stock;
        this.prix_achat = prix_achat;
        this.categorie  = categorie;
        this.image = image;
    }

    public Produit(String nom,float prix_vente,String image){
        this.nom = nom;
        this.prix_vente = prix_vente;
        this.image = image;
    }

    public Produit(String nom){
        this.nom = nom;
    }

    public Produit(String nom,float prix_vente,int stock,float prix_achat,String categorie,String image){
        this.nom = nom;
        this.prix_vente = prix_vente;
        this.stock = stock;
        this.prix_achat = prix_achat;
        this.categorie  = categorie;
        this.image = image;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
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

    public float getPrix_achat() {
        return prix_achat;
    }

    public void setPrix_achat(float prix_achat) {
        this.prix_achat = prix_achat;
    }


    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }
}
