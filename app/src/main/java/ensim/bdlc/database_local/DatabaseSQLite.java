package ensim.bdlc.database_local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * JONATHAN DAUMONT
 * 2017
 */
public class DatabaseSQLite {

    // Database version
    protected final static int DATABASE_VERSION = 1;
    // Database name
    private static final String DATABASE_NAME = "BDLC_BDD";

    protected SQLiteDatabase bdd = null;
    protected DatabaseManager mHandler = null;

    public DatabaseSQLite(Context pContext) {
        this.mHandler = new DatabaseManager(pContext, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public SQLiteDatabase open() {
        bdd = mHandler.getWritableDatabase();
        return bdd;
    }

    public void close() {
        bdd.close();
    }

    public SQLiteDatabase getDb() {
        return bdd;
    }
}
