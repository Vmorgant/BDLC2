package ensim.bdlc.database;


import android.content.Context;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import ensim.bdlc.modele.User;

/**
 * Created by Jonathan Daumont on 11/10/2017.
 */

public class TableUser{

    private String TABLE_USER = "TABLE_USER";

    private String USERNAME = "USERNAME";
    private String NUM_ETU = "NUM_ETU";
    private String CATEGORIE = "CATEGORIE";
    private String CREDIT = "CREDIT";
    private String MDP = "MDP";
    private String VERSION = "VERSION";


    public TableUser(Context context) {
        Back4App.getInstance(context);
    }

    public void add_user(User user) {

        ParseObject parse_code = new ParseObject(TABLE_USER);
        parse_code.put(USERNAME, user.getUsername());
        parse_code.put(NUM_ETU, user.getNum_etu());
        parse_code.put(CATEGORIE, user.getCategorie());
        parse_code.put(CREDIT, "" + user.getCredit());
        parse_code.put(MDP, "" + user.getPassword());
        parse_code.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e != null) {
                    Log.d("ONLINE", "ERROR TableUser - add_user()");
                } else {
                    Log.d("ONLINE : ", "OK TableUser - add_user() ");

                }
            }
        });
    }

    public User get_user(String num_etu) {

        ParseQuery query = ParseQuery.getQuery(TABLE_USER);
        query.whereEqualTo(NUM_ETU, num_etu);
        ParseObject parseObject = null;
        User user = null;
        try {
            parseObject = query.getFirst();

            user = new User(
                    parseObject.get(USERNAME).toString(),
                    parseObject.get(NUM_ETU).toString(),
                    parseObject.get(CATEGORIE).toString(),
                    Float.parseFloat(parseObject.get(CREDIT).toString()),
                    parseObject.get(MDP).toString(),
                    Integer.parseInt(parseObject.get(VERSION).toString())
            );
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("ONLINE", "Problème pour get_user() : " + e);
        }


        return user;
    }

    public User connect_user(String num_etu, String mdp) {

        ParseQuery query = ParseQuery.getQuery(TABLE_USER);
        query.whereEqualTo(NUM_ETU, num_etu);
        query.whereEqualTo(MDP, mdp);
        User user = null;
        try {
            ParseObject parseObject = query.getFirst();
            if (parseObject == null) {
                user = null;
            } else {
                user = new User(
                        parseObject.get(USERNAME).toString(),
                        parseObject.get(NUM_ETU).toString(),
                        parseObject.get(CATEGORIE).toString(),
                        Float.parseFloat(parseObject.get(CREDIT).toString()),
                        parseObject.get(MDP).toString(),
                        Integer.parseInt(parseObject.get(VERSION).toString()));
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("ONLINE", "Problème connect_user() : " + e);
        }
        return user;

    }


    public void changer_mot_de_passe(User user) {
        ParseQuery query = ParseQuery.getQuery(TABLE_USER);
        query.whereEqualTo(NUM_ETU, user.getNum_etu());
        int retour = -1;
        try {
            ParseObject parseObject = query.getFirst();
            if (parseObject == null) {
                retour = -1;
            } else {
                parseObject.put(MDP, user.getPassword());
                parseObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.d(getClass().getSimpleName(), "Problème update mdp " + e);
                        } else {
                            Log.d(getClass().getSimpleName(), "update mdp ok" + e);
                        }
                    }
                });
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("ONLINE", "Problème changer_mot_de_passe() : " + e);
        }

    }

    public void changer_credit(User user) {
        ParseQuery query = ParseQuery.getQuery(TABLE_USER);
        query.whereEqualTo(NUM_ETU, user.getNum_etu());
        int retour = -1;
        try {
            ParseObject parseObject = query.getFirst();
            if (parseObject == null) {
                retour = -1;
            } else {
                parseObject.put(CREDIT, ""+user.getCredit());
                parseObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.d(getClass().getSimpleName(), "Problème changer_credit mdp " + e);
                        } else {
                            Log.d(getClass().getSimpleName(), "update changer_credit ok" + e);
                        }
                    }
                });
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("ONLINE", "Problème changer_credit() : " + e);
        }

    }

    public boolean user_exist(String num_etu) {
        ParseQuery query = ParseQuery.getQuery(TABLE_USER);
        query.whereEqualTo(NUM_ETU, num_etu);
        boolean etat=false;
        try {
            ParseObject parseObject = query.getFirst();
            if(parseObject==null){
                etat = false;
            }
            else{
                etat =true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("ONLINE", "Problème user_exist() : " + e);
        }
        return etat;


    }


    public void changer_version(User user) {
        ParseQuery query = ParseQuery.getQuery(TABLE_USER);
        query.whereEqualTo(NUM_ETU, user.getNum_etu());
        int retour = -1;
        try {
            ParseObject parseObject = query.getFirst();
            if (parseObject == null) {
                retour = -1;
            } else {
                parseObject.put(VERSION, ""+user.getVersion());
                parseObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.d(getClass().getSimpleName(), "Problème changer_version mdp " + e);
                        } else {
                            Log.d(getClass().getSimpleName(), "update changer_version ok" + e);
                        }
                    }
                });
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("ONLINE", "Problème changer_version() : " + e);
        }

    }
}
