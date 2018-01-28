package ensim.bdlc.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import ensim.bdlc.modele.Article;
import ensim.bdlc.modele.News;
import ensim.bdlc.modele.Produit;

/**
 * Created by Jonathan Daumont on 11/10/2017.
 */

public class TableNews {

    private String TABLE_NEWS = "TABLE_NEWS";
    private String ID_NEWS = "ID_NEWS";
    private String TITLE = "TITLE";
    private String DETAILS = "DETAILS";
    private String PICTURE = "PICTURE";
    private String LINK = "LINK";
    private String DATE = "DATE";

    private Context context;



    public TableNews(Context context) {
        Back4App.getInstance(context);
        this.context = context;
    }

    public void add_news(News news) {

        ParseObject parse_code = new ParseObject(TABLE_NEWS);
        parse_code.put(ID_NEWS,generate_id());
        parse_code.put(TITLE, news.getTitle());
        parse_code.put(DETAILS, news.getDetails());
        parse_code.put(LINK, news.getLink());


        Bitmap bitmap = news.getPicture();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bitmapBytes = stream.toByteArray();

        ParseFile image = new ParseFile(PICTURE, bitmapBytes);
        try {
            image.save();
        } catch (ParseException e) {
           Log.e("ERROR","e"+e);
        }


        parse_code.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e != null) {
                    Log.d("ONLINE", "ERROR TableNews - add_news()"+e);
                } else {
                    Log.d("ONLINE : ", "OK TableNews - add_news() ");

                }
            }
        });
    }

    public int generate_id() {
        int id_article=0;
        ParseQuery query = ParseQuery.getQuery(TABLE_NEWS);
        List<ParseObject> parseObject;
        try {
            parseObject = query.find();

            if(parseObject.size()==0){
                id_article = 1;
            }else{
                id_article =  (int)parseObject.get(parseObject.size()-1).get(ID_NEWS)+1;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return id_article;
    }

    public News get_news(int id_news) {
        //TODO à finir

        ParseQuery query = ParseQuery.getQuery(TABLE_NEWS);
        query.whereEqualTo(ID_NEWS, id_news);
        ParseObject parseObject;
        News news = null;
        try {
            parseObject = query.getFirst();
            news = new News(
                    (int) parseObject.get(ID_NEWS),
                    parseObject.get(TITLE).toString(),
                    parseObject.get(DETAILS).toString(),
                    parseObject.get(LINK).toString(),
                    parseObject.get(DATE).toString()
            );

            ParseFile fileObject = (ParseFile) parseObject.get("PICTURE");
            byte[] data= fileObject.getData();
            news.setPicture(BitmapFactory.decodeByteArray(data, 0,data.length));
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("TableNews", "Problème pour get_news() : " + e);
        }
        return news;
    }



    public ArrayList<News> get_list_news() {
        ArrayList<News> list_news = new ArrayList<>();
        ParseQuery query = ParseQuery.getQuery(TABLE_NEWS);
        query.orderByAscending(ID_NEWS);
        List<ParseObject> parseObject = null;

        try {
            parseObject = query.find();
            for (int i = (parseObject.size()-1); i >= 0; i--) {
                News news = new News(
                        (int) parseObject.get(i).get(ID_NEWS),
                        parseObject.get(i).get(TITLE).toString(),
                        parseObject.get(i).get(DETAILS).toString(),
                        parseObject.get(i).get(LINK).toString(),
                        parseObject.get(i).get(DATE).toString()
                );

                ParseFile fileObject = (ParseFile) parseObject.get(i).get("PICTURE");
                byte[] data= fileObject.getData();
                news.setPicture(BitmapFactory.decodeByteArray(data, 0,data.length));
                list_news.add(news);


            }


        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("ONLINE", "Problème pour get_list_article() : " + e);
        }


        return list_news;
    }

    /*public void delete_article(int id_panier) {
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
    }*/


}
