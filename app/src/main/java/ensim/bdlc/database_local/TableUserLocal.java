package ensim.bdlc.database_local;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import ensim.bdlc.modele.User;


/**
 * JONATHAN DAUMONT
 * 2017
 */
public class TableUserLocal {


    //TABLE_USER
    private static final String TABLE_USER = "TABLE_USER";
    private static final String ID_USER = "ID_USER";
    private String NUM_ETU = "NUM_ETU";
    private String MDP = "MDP";
    /**
     * Variables
     ***********************************************************************************/
    protected SQLiteDatabase mDb;

    /**
     * Constructeur
     ********************************************************************************/
    public TableUserLocal(SQLiteDatabase mDb) {
        this.mDb = mDb;
    }

    /**
     * Méthodes
     ************************************************************************************/
    public void add_user(User user) {

        ContentValues value = new ContentValues();
        value.put(ID_USER, 1);
        value.put(NUM_ETU, user.getNum_etu().toString());
        value.put(MDP, user.getPassword().toString());
        mDb.insert(TABLE_USER, null, value);
    }

    public boolean same_user(User user) {
        boolean retour = false;
        Cursor cursor = mDb.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " + NUM_ETU + " = ? AND " + MDP + " = ? ", new String[]{String.valueOf(user.getNum_etu()), String.valueOf(user.getPassword())});
        if (cursor.moveToFirst()) {
            retour = true;
        }

        return retour;
    }

    public boolean user_exist() {
        boolean retour = false;
        Cursor cursor = mDb.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " + ID_USER + " = ? ", new String[]{String.valueOf("1")});
        if (cursor.moveToFirst()) {
            retour = true;
        }

        return retour;
    }

    public void update_user(User user) {
        ContentValues value = new ContentValues();
        value.put(NUM_ETU, user.getNum_etu());
        value.put(MDP, user.getPassword());
        mDb.update(TABLE_USER, value, ID_USER + " = ?", new String[]{String.valueOf("1")});
    }

    public User get_user() {
        User user;
        Cursor cursor = mDb.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " + ID_USER + " = ? ", new String[]{String.valueOf("1")});
        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getString(1),
                    cursor.getString(2));
            cursor.close();
        } else {
            cursor.close();
            return null;
        }
        return user;
    }
}
