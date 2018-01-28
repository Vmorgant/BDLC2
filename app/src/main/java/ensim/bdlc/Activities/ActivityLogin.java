package ensim.bdlc.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.journeyapps.barcodescanner.Util;

import ensim.bdlc.R;
import ensim.bdlc.database.TableUser;
import ensim.bdlc.database_local.DatabaseSQLite;
import ensim.bdlc.database_local.TableUserLocal;
import ensim.bdlc.modele.User;
import ensim.bdlc.utils.Methodes;

/**
 * JONATHAN DAUMONT
 * 2017
 */
public class ActivityLogin extends AppCompatActivity {

    /**
     * ATTRIBUTES
     */
    //INTERFACE
    private Button btn_login;
    private EditText editText_num_etu, editText_password;
    private ProgressDialog mProgressDialog;
    //DATABASE
    private TableUserLocal tableUserLocal;
    //OTHER
    private Activity activity;
    private String num_etu, password;
    private User user, user_local;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //GET INTERFACE
        setContentView(R.layout.activity_login);
        editText_num_etu = (EditText) findViewById(R.id.editText_num_etu);
        editText_password = (EditText) findViewById(R.id.editText_password);
        btn_login = (Button) findViewById(R.id.btn_login);

        //INITIALIZE
        activity = this;
        mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setTitle("Veuillez patienter");
        mProgressDialog.setMessage("Connexion en cours...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(false);

        DatabaseSQLite database_sqLite = new DatabaseSQLite(this);
        database_sqLite.open();
        tableUserLocal = new TableUserLocal(database_sqLite.getDb());

        if (tableUserLocal.user_exist()) {
            user_local = tableUserLocal.get_user();
            editText_num_etu.setText(user_local.getNum_etu());
            editText_password.setText(user_local.getPassword());
        }

        //LISTENER
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Methodes.internet_diponible(activity)) {
                    start_connexion();
                }
            }
        });
        editText_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    start_connexion();
                    handled = true;
                }
                return handled;
            }
        });

    }

    public void start_connexion(){
        num_etu = editText_num_etu.getText().toString();
        password = editText_password.getText().toString();

        if(num_etu.equals("")||password.equals("")){
            Methodes.info_dialog("Veuillez remplir tous les champs",activity);
        }else{
            new Connexion().execute();
        }


    }

    private class Connexion extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (Methodes.internet_diponible(activity)) {
                mProgressDialog.show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (Methodes.internet_diponible(activity)) {
                TableUser tableUser = new TableUser(activity.getBaseContext());
                user = tableUser.connect_user(num_etu, password);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mProgressDialog.hide();
            if (user != null) {
                if (Methodes.internet_diponible(activity)) {

                    if (tableUserLocal.user_exist()) {//un user existe dans la bdd local
                        if (tableUserLocal.same_user(user)) {//si c'est le même
                            mProgressDialog.dismiss();
                            mProgressDialog.cancel();

                            Intent intent = new Intent(activity, ActivityAccueil.class);
                            intent.putExtra("user", new Gson().toJson(user));
                            startActivity(intent);
                            overridePendingTransition(R.anim.pull_in, R.anim.push_out);
                            finish();
                        } else {//pas le même
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setCancelable(false);
                            builder.setMessage("Voulez-vous modifier le compte enregistré ?")
                                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            mProgressDialog.dismiss();
                                            mProgressDialog.cancel();

                                            tableUserLocal.update_user(user);

                                            Intent intent = new Intent(activity, ActivityAccueil.class);
                                            intent.putExtra("user", new Gson().toJson(user));
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.pull_in, R.anim.push_out);
                                            finish();


                                        }
                                    })
                                    .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            mProgressDialog.dismiss();
                                            mProgressDialog.cancel();

                                            Intent intent = new Intent(activity, ActivityAccueil.class);
                                            intent.putExtra("user", new Gson().toJson(user));
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.pull_in, R.anim.push_out);
                                            finish();
                                        }
                                    });
                            builder.create();
                            builder.show();
                        }


                    } else {//aucun user sauvegarder
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setCancelable(false);
                        builder.setMessage("Voulez-vous sauvegarder votre compte ?")
                                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        mProgressDialog.cancel();
                                        mProgressDialog.dismiss();

                                        tableUserLocal.add_user(user);

                                        Intent intent = new Intent(activity, ActivityAccueil.class);
                                        intent.putExtra("user", new Gson().toJson(user));
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.pull_in, R.anim.push_out);
                                        finish();


                                    }
                                })
                                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        mProgressDialog.cancel();
                                        mProgressDialog.dismiss();
                                        Intent intent = new Intent(activity, ActivityAccueil.class);
                                        intent.putExtra("user", new Gson().toJson(user));
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.pull_in, R.anim.push_out);
                                        finish();
                                    }
                                });
                        builder.create();
                        builder.show();
                    }

                }
            } else {
                Methodes.info_dialog("Numéro étudiant ou mot de passe incorrect", activity);
            }

        }
    }
}
