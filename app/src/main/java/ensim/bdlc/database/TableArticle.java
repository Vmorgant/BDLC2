package ensim.bdlc.database;

import android.content.Context;
import android.util.Log;
import android.widget.ProgressBar;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import ensim.bdlc.modele.Article;
import ensim.bdlc.modele.Produit;

/**
 * Created by Jonathan Daumont on 11/10/2017.
 */

public class TableArticle{

    private String TABLE_ARTICLE = "TABLE_ARTICLE";
    private String ID_PRODUIT = "ID_PRODUIT";
    private String ID_PANIER = "ID_PANIER";
    private String QUANTITE = "QUANTITE";

    private Context context;



    public TableArticle(Context context) {
        Back4App.getInstance(context);
        this.context = context;
    }

    public void add_article(Article article,String id_panier) {

        ParseObject parse_code = new ParseObject(TABLE_ARTICLE);
        parse_code.put(ID_PRODUIT, article.getProduit().getId());
        parse_code.put(ID_PANIER, id_panier);
        parse_code.put(QUANTITE, article.getQuantite());

        parse_code.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e != null) {
                    Log.d("ONLINE", "ERROR TableArticle - add_article()");
                } else {
                    Log.d("ONLINE : ", "OK TableArticle - add_article() ");

                }
            }
        });
    }

    public ArrayList<Article> get_list_article(String id_panier) {
        ArrayList<Article> list_article = new ArrayList<>();
        ParseQuery query = ParseQuery.getQuery(TABLE_ARTICLE);
        query.whereEqualTo(ID_PANIER, id_panier);
        List<ParseObject> parseObject = null;

        try {
            parseObject = query.find();

            for (int i = 0; i < parseObject.size(); i++) {
                TableProduit tableProduit = new TableProduit(context);
                Produit produit = tableProduit.get_produit(Integer.parseInt(parseObject.get(i).get(ID_PRODUIT).toString()));
                Article article = new Article(
                        produit,
                        Integer.parseInt(parseObject.get(i).get(QUANTITE).toString())
                );
                list_article.add(article);


            }


        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("ONLINE", "Problème pour get_list_article() : " + e);
        }


        return list_article;
    }

    public void delete_article(String id_panier) {
        ParseQuery query = ParseQuery.getQuery(TABLE_ARTICLE);
        query.whereEqualTo(ID_PANIER, id_panier);
        boolean etat = false;
        try {
            List<ParseObject> parseObject = query.find();

            for (int i = 0; i < parseObject.size(); i++) {
                parseObject.get(i).delete();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


}
