package ensim.bdlc.database;

import android.content.Context;
import android.util.Log;

import com.parse.Parse;

/**
 * Created by jonathan daumont
 * 2017
 */

public class Back4App{


    private Back4App(Context context) {
        try {

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR", "erreur constructeur Back4App = " + e);
        }
    }

    /**
     * Instance unique non préinitialisée
     */
    private static Back4App INSTANCE = null;

    /**
     * Point d'accès pour l'instance unique du singleton
     */
    public static synchronized Back4App getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new Back4App(context);
        }
        return INSTANCE;
    }

}