package ensim.bdlc.database;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import ensim.bdlc.modele.Article;
import ensim.bdlc.modele.Produit;
import ensim.bdlc.modele.User;

/**
 * Created by Jonathan Daumont on 11/10/2017.
 */

public class TableProduit{

    private String TABLE_PRODUIT = "TABLE_PRODUIT";
    private String ID_PRODUIT = "ID_PRODUIT";
    private String NOM = "NOM";
    private String PRIX_VENTE = "PRIX_VENTE";
    private String STOCK = "STOCK";
    private String PRIX_ACHAT = "PRIX_ACHAT";
    private String CATEGORIE = "CATEGORIE";
    private String IMAGE = "IMAGE";




    public TableProduit(Context context){
        Back4App.getInstance(context);
    }

    public int add_produit(Produit produit) {

        int id_produit = generate_id();

        ParseObject parse_code = new ParseObject(TABLE_PRODUIT);
        parse_code.put(ID_PRODUIT, id_produit);
        parse_code.put(NOM, produit.getNom());
        parse_code.put(PRIX_VENTE, ""+produit.getPrix_vente());
        parse_code.put(STOCK, produit.getStock());
        parse_code.put(PRIX_ACHAT, ""+produit.getPrix_achat());
        parse_code.put(CATEGORIE, ""+produit.getCategorie());
        parse_code.put(IMAGE, ""+produit.getImage());

        parse_code.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e != null) {
                    Log.d("ONLINE", "ERROR TableProduit - add_produit()");
                } else {
                    Log.d("ONLINE : ", "OK TableProduit - add_produit() ");

                }
            }
        });
        return id_produit;
    }

    public int generate_id() {
        int qrcode=0;
        ParseQuery query = ParseQuery.getQuery(TABLE_PRODUIT);
        List<ParseObject> parseObject = null;
        Produit produit=null;
        try {
            parseObject = query.find();

            if(parseObject.size()==0){
                qrcode = 1;
            }else{
                qrcode =  (int)parseObject.get(parseObject.size()-1).get(ID_PRODUIT)+1;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return qrcode;
    }

    public Produit get_produit(int id_produit) {


        ParseQuery query = ParseQuery.getQuery(TABLE_PRODUIT);
        query.whereEqualTo(ID_PRODUIT, id_produit);
        ParseObject parseObject;
        Produit produit = null;
        try {
            parseObject = query.getFirst();

            produit = new Produit(
                    (int) parseObject.get(ID_PRODUIT),
                    parseObject.get(NOM).toString(),
                    Float.parseFloat(parseObject.get(PRIX_VENTE).toString()),
                    Integer.parseInt(parseObject.get(STOCK).toString()),
                    Float.parseFloat(parseObject.get(PRIX_ACHAT).toString()),
                    parseObject.get(CATEGORIE).toString(),
                    parseObject.get(IMAGE).toString()
            );
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("ONLINE", "Problème pour get_user() : " + e);
        }
        return produit;
    }

    public void change_stock(ArrayList<Article> list_article) {
        ParseQuery query = ParseQuery.getQuery(TABLE_PRODUIT);

        for(int i=0;i<list_article.size();i++){
            query.whereEqualTo(ID_PRODUIT, list_article.get(i).getProduit().getId());
            Produit produit_tmp = this.get_produit( list_article.get(i).getProduit().getId());
            try {
                ParseObject parseObject = query.getFirst();
                if (parseObject == null) {

                } else {
                    parseObject.put(STOCK, (produit_tmp.getStock()-list_article.get(i).getQuantite()));
                    parseObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.d(getClass().getSimpleName(), "Problème change_stock  " + e);
                            } else {
                                Log.d(getClass().getSimpleName(), "change_stock ok" + e);
                            }
                        }
                    });
                }
            } catch (ParseException e) {
                e.printStackTrace();
                Log.d("ONLINE", "Problème change_stock() : " + e);
            }
        }



    }

    public ArrayList<Produit> get_produit(String type) {
        ArrayList<Produit> list_produit = new ArrayList<Produit>();
        ParseQuery query = ParseQuery.getQuery(TABLE_PRODUIT);
        query.whereEqualTo(CATEGORIE, type);
        query.orderByAscending(ID_PRODUIT);
        List<ParseObject> parseObject = null;
        try {
            parseObject = query.find();

            for (int i = 0; i < parseObject.size(); i++) {

               // int id,String nom,float prix_vente,int stock,float prix_achat,String categorie){
                Produit produit = new Produit(
                        (int) parseObject.get(i).get(ID_PRODUIT),
                        parseObject.get(i).get(NOM).toString(),
                        Float.parseFloat(parseObject.get(i).get(PRIX_VENTE).toString()),
                        Integer.parseInt(parseObject.get(i).get(STOCK).toString()),
                        Float.parseFloat(parseObject.get(i).get(PRIX_ACHAT).toString()),
                        parseObject.get(i).get(CATEGORIE).toString(),
                        parseObject.get(i).get(IMAGE).toString()
                );

                /*ParseFile fileObject = (ParseFile) parseObject.get(i).get("PICTURE");
                byte[] data= fileObject.getData();
                produit.setPicture(BitmapFactory.decodeByteArray(data, 0,data.length));*/

                list_produit.add(produit);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("ONLINE", "Problème pour list_cd() : " + e);
        }


        return list_produit;
    }

    public ArrayList<Produit> get_formule() {
        ArrayList<Produit> list_produit = new ArrayList<Produit>();
        ParseQuery query = ParseQuery.getQuery(TABLE_PRODUIT);
        List<ParseObject> parseObject = null;
        try {
            parseObject = query.find();

            for (int i = 0; i < parseObject.size(); i++) {
                if( !parseObject.get(i).get(CATEGORIE).toString().equals("snack") && !parseObject.get(i).get(CATEGORIE).toString().equals("boisson")){
                    // int id,String nom,float prix_vente,int stock,float prix_achat,String categorie){
                    Produit produit = new Produit(
                            (int) parseObject.get(i).get(ID_PRODUIT),
                            parseObject.get(i).get(NOM).toString(),
                            Float.parseFloat(parseObject.get(i).get(PRIX_VENTE).toString()),
                            Integer.parseInt(parseObject.get(i).get(STOCK).toString()),
                            Float.parseFloat(parseObject.get(i).get(PRIX_ACHAT).toString()),
                            parseObject.get(i).get(CATEGORIE).toString(),
                            parseObject.get(i).get(IMAGE).toString()
                    );
                    list_produit.add(produit);
                }

            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("ONLINE", "Problème pour list_cd() : " + e);
        }


        return list_produit;
    }


}
