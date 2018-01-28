package ensim.bdlc.database;

import android.content.Context;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import ensim.bdlc.modele.Panier;
import ensim.bdlc.modele.Produit;
import ensim.bdlc.modele.User;
import ensim.bdlc.utils.Methodes;

/**
 * Created by Jonathan Daumont on 11/10/2017.
 */

public class TablePanier{

    private String TABLE_PANIER = "TABLE_PANIER";
    private String ID_PANIER = "ID_PANIER";
    private String PRIX_TOTAL = "PRIX_TOTAL";
    private String STATE = "STATE";
    private String ID_OWNER = "ID_OWNER";

    private Context context;

    public TablePanier(Context context) {
        Back4App.getInstance(context);
        this.context = context;
    }

    public String add_panier(Panier panier) {

        String id_panier = generate_id(panier.getId_owner());

        ParseObject parse_code = new ParseObject(TABLE_PANIER);
        parse_code.put(ID_PANIER, id_panier);
        parse_code.put(PRIX_TOTAL, panier.getPrix_total());
        parse_code.put(STATE, panier.getState());
        parse_code.put(ID_OWNER, panier.getId_owner());


        //TODO AJOUTER ARTICLE
        if(panier.getList_article().size()!=0){
            TableArticle tableArticle = new TableArticle(context);
            for(int cpt = 0;cpt<panier.getList_article().size();cpt++){
                tableArticle.add_article(panier.getList_article().get(cpt),id_panier);
            }
        }

        parse_code.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e != null) {
                    Log.d("ONLINE", "ERROR TablePanier - add_panier()");
                } else {
                    Log.d("ONLINE : ", "OK TablePanier - add_panier() ");

                }
            }
        });
        return id_panier;
    }

    public String generate_id(String id_owner) {
        String id_panier_old=null;
        String id_panier=null;
        ParseQuery query = ParseQuery.getQuery(TABLE_PANIER);
        query.orderByDescending("createdAt");
        ParseObject parseObject = null;
        Produit produit=null;
        try {
            parseObject = query.getFirst();

            if(parseObject==null){
                id_panier = Methodes.date()+"_"+id_owner+"_"+1;
            }else{
                id_panier_old =  parseObject.get(ID_PANIER).toString();
                String tab[] = id_panier_old.split("_");
                String data =Methodes.date();
                if(tab[0].equals(Methodes.date())){
                    id_panier = Methodes.date()+"_"+id_owner+"_"+(Integer.parseInt(tab[2])+1);
                }else{
                    id_panier = Methodes.date()+"_"+id_owner+"_1";
                }



            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return id_panier;
    }

    public Panier get_panier(String id_panier) {

        ParseQuery query = ParseQuery.getQuery(TABLE_PANIER);
        query.whereEqualTo(ID_PANIER, id_panier);
        ParseObject parseObject = null;
        Panier panier = null;
        try {
            parseObject = query.getFirst();

            panier = new Panier(
                    parseObject.get(ID_PANIER).toString(),
                    Float.parseFloat(parseObject.get(PRIX_TOTAL).toString()),
                    parseObject.get(STATE).toString(),
                    parseObject.get(ID_OWNER).toString()

            );
            TableArticle tableArticle = new TableArticle(context);
            panier.setList_article(tableArticle.get_list_article(id_panier));



        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("ONLINE", "Problème pour get_user() : " + e);
        }


        return panier;
    }


    public void change_state_panier(Panier panier){
        ParseQuery query = ParseQuery.getQuery(TABLE_PANIER);
        query.whereEqualTo(ID_PANIER, panier.getId_panier());
        int retour = -1;
        try {
            ParseObject parseObject = query.getFirst();
            if(parseObject==null){
                retour = -1;
            }
            else{
                parseObject.put(STATE,panier.getState());
                parseObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.d(getClass().getSimpleName(), "Problème change_state_panier " + e);
                        }else{
                            Log.d(getClass().getSimpleName(), "change_state_panier ok" + e);
                        }
                    }
                });
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("ONLINE", "Problème change_state_panier() : " + e);
        }

    }


    public ArrayList<Panier> get_panier(String id_user,String state) {
        ArrayList<Panier> list_panier = new ArrayList<>();
        ParseQuery query = ParseQuery.getQuery(TABLE_PANIER);
        query.whereEqualTo(ID_OWNER, id_user);
        query.whereEqualTo(STATE, state);
        List<ParseObject> parseObject = null;


        try {
            parseObject = query.find();
            for (int i = (parseObject.size()-1); i >= 0; i--) {

                // int id,String nom,float prix_vente,int stock,float prix_achat,String categorie){
                Panier panier = new Panier(
                        parseObject.get(i).get(ID_PANIER).toString(),
                        Float.parseFloat(parseObject.get(i).get(PRIX_TOTAL).toString()),
                        parseObject.get(i).get(STATE).toString(),
                        parseObject.get(i).get(ID_OWNER).toString()

                );
                list_panier.add(panier);


            }


        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("ONLINE", "Problème pour get_panier() : " + e);
        }


        return list_panier;
    }

    public boolean delete_panier(String id_panier) {
        ParseQuery query = ParseQuery.getQuery(TABLE_PANIER);
        query.whereEqualTo(ID_PANIER, id_panier);

        TableArticle tableArticle = new TableArticle(context);
        tableArticle.delete_article(id_panier);
        boolean etat = false;
        try {
            ParseObject parseObject = query.getFirst();
            if (parseObject == null) {
                etat = false;
            } else {
                parseObject.delete();
                etat = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return etat;
    }






}