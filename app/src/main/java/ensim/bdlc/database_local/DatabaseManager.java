package ensim.bdlc.database_local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * JONATHAN DAUMONT
 * 2017
 */
public class DatabaseManager extends SQLiteOpenHelper {

    /**
     * Table Names
     ********************************************************************************/
    private static final String TABLE_USER = "TABLE_USER";

    /**
     * Champs tables
     *******************************************************************************/
    //TABLE_USER
    private static final String ID_USER = "ID_USER";
    private static String NUM_ETU = "NUM_ETU";
    private static String MDP = "MDP";



    /**
     * REQUETE de création de tables
     ***************************************************************/

    private static final String CREATE_TABLE_USER =
            "CREATE TABLE " + TABLE_USER + "(" +
                    ID_USER + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    NUM_ETU + " TEXT," +
                    MDP + " TEXT);";


    public DatabaseManager(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating required tables
        db.execSQL(CREATE_TABLE_USER);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        // create new tables
        onCreate(db);
    }

}