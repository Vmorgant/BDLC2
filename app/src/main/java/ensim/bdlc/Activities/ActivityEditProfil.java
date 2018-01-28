package ensim.bdlc.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import ensim.bdlc.R;
import ensim.bdlc.database.TableUser;
import ensim.bdlc.modele.Panier;
import ensim.bdlc.modele.User;
import ensim.bdlc.utils.Methodes;

public class ActivityEditProfil extends AppCompatActivity {


    /**
     * ATTRIBUTES
     */
    //INTERFACE
    private Toolbar toolbar;
    private EditText editText_ancien_mdp,editText_password_1,editText_password_2;
    private ProgressDialog mProgressDialog;
    private Button button_save;
    private AppBarLayout toolbar_layout;
    //OTHER
    private Activity activity;
    private User user;
    private String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //GET INTERFACE
        setContentView(R.layout.activity_edit_profil);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_layout = (AppBarLayout) findViewById(R.id.toolbar_layout);
        editText_ancien_mdp  =(EditText)findViewById(R.id.editText_ancien_mdp);
        editText_password_1  =(EditText)findViewById(R.id.editText_password_1);
        editText_password_2  =(EditText)findViewById(R.id.editText_password_2);
        button_save = (Button)findViewById(R.id.button_save);

        //INITIALIZE
        activity = this;
        mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setTitle("Veuillez patienter");
        mProgressDialog.setMessage("Connexion en cours...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(false);

        toolbar.setTitle("Changement du mot de passe");
        setSupportActionBar(toolbar);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.getString("user") != null) {
                user = new Gson().fromJson(extras.getString("user"), User.class);
            }
        }

        editText_password_2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    test_champ();
                    handled = true;
                }
                return handled;
            }
        });

        //LISTENERS
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test_champ();
            }
        });

    }

    public void test_champ(){
        if (Methodes.internet_diponible(activity)) {
            if (editText_password_1.length() < 4) {
                Toast.makeText(activity, "Le mot de passe ne peut avoir une taille inférieur à 4 caratères", Toast.LENGTH_SHORT).show();
            }else{

                if (!editText_password_1.getText().toString().equals(editText_password_2.getText().toString())) {
                    Toast.makeText(activity, "Mot de passe non identique", Toast.LENGTH_SHORT).show();
                } else {
                    if(editText_ancien_mdp.getText().toString().equals(user.getPassword())){
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setCancelable(false);
                        builder.setMessage("Voulez vous sauvegarder votre nouveau mot de passe ?")
                                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        if (Methodes.internet_diponible(activity)) {
                                            password = editText_password_1.getText().toString();
                                            new Modification().execute();
                                        }
                                    }
                                })
                                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        if (Methodes.internet_diponible(activity)) {
                                        }
                                    }
                                });
                        builder.create();
                        builder.show();

                    }else{
                        Toast.makeText(activity, "L'ancien mot de passe est incorrect", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        retour();
    }

    public void retour() {
        if (Methodes.internet_diponible(activity)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setCancelable(false);
            builder.setMessage("Voulez vous annuler les modifications ?")
                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            Intent intent = new Intent(activity, ActivityProfil.class);
                            intent.putExtra("user", new Gson().toJson(user));
                            startActivity(intent);
                            overridePendingTransition(R.anim.pull_in, R.anim.push_out);
                            finish();
                        }
                    })
                    .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            builder.create();
            builder.show();
        }

    }

    private class Modification extends AsyncTask<Void, Void, Void> {
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
                user.setPassword(password);
                tableUser.changer_mot_de_passe(user);

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mProgressDialog.hide();
            Methodes.info_dialog("Nouveau mot de passe sauvegardé",activity);
            Intent intent = new Intent(activity, ActivityProfil.class);
            intent.putExtra("user", new Gson().toJson(user));
            startActivity(intent);
            overridePendingTransition(R.anim.pull_in, R.anim.push_out);
            finish();

        }
    }
}
