package ensim.bdlc.modele;

import android.graphics.Bitmap;
import android.media.Image;

/**
 * Created by Jonathan Daumont on 21/10/2017.
 */

public class News {

    private int id_news;
    private String title;
    private String details;
    private Bitmap picture;
    private String link;
    private String date;

    public News(int id_news,String title,String details,String link,String date){
        this.id_news = id_news;
        this.title = title;
        this.details =details;
        this.link = link;
        this.date = date;
    }

    public News(String title,String details, Bitmap picture,String link,String date){
        this.title = title;
        this.details =details;
        this.picture = picture;
        this.link = link;
        this.date = date;
    }

    public News(int id_news,String title,String details, Bitmap picture,String link,String date){
        this.id_news = id_news;
        this.title = title;
        this.details =details;
        this.picture = picture;
        this.link = link;
        this.date = date;
    }

    public int getId_news() {
        return id_news;
    }

    public void setId_news(int id_news) {
        this.id_news = id_news;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
