package ensim.bdlc.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

import ensim.bdlc.R;
import ensim.bdlc.database.TableUser;
import ensim.bdlc.modele.Article;
import ensim.bdlc.modele.User;
import ensim.bdlc.utils.Methodes;

public class ActivityCommandeOfferte extends AppCompatActivity {

    /**
     * ATTRIBUTES
     */
    //INTERFACE
    private Toolbar toolbar;
    private Button button_close;
    private CollapsingToolbarLayout toolbar_layout;
    private LinearLayout linearLayout_scroll;
    private Button button_scanner, button_valider,button_ok;
    private LinearLayout linearLayout_scanner;
    private EditText editText_num_etu;
    private ProgressDialog mProgressDialog;


    //BDD
    private TableUser tableUser;
    //ARRAYLIST

    //OTHER
    private Activity activity;
    private User user, cadeau;
    private boolean user_exist = false;
    private String num_etu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //GET INTERFACE
        setContentView(R.layout.activity_commande_offerte);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_layout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        button_scanner = (Button) findViewById(R.id.button_scanner);
        linearLayout_scanner = (LinearLayout) findViewById(R.id.linearLayout_scanner);
        editText_num_etu = (EditText) findViewById(R.id.editText_num_etu);
        button_valider = (Button) findViewById(R.id.button_valider);

        //INITIALIZE
        activity = this;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = new Gson().fromJson(extras.getString("user"), User.class);
        }
        mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setTitle("Veuillez patienter");
        mProgressDialog.setMessage("Connexion en cours...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(false);

        setSupportActionBar(toolbar);
        toolbar_layout.setTitle("Offrir un cadeau");

        LayoutInflater factory = LayoutInflater.from(activity);

        final View alertDialogView = factory.inflate(R.layout.dialog_info_gift, null);

        AlertDialog.Builder adb = new AlertDialog.Builder(activity);
        //GET INTERFACE
        button_close = (Button) alertDialogView.findViewById(R.id.button_close);
        button_ok = (Button) alertDialogView.findViewById(R.id.button_ok);


        adb.setView(alertDialogView);
        final AlertDialog alertDialog = adb.show();

        button_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        //LISTENERS
        button_scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanner();

            }
        });
        linearLayout_scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanner();
            }
        });
        button_valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Methodes.internet_diponible(activity)) {
                    num_etu = editText_num_etu.getText().toString();
                    new Loading().execute();

                }
            }
        });
    }

    public void scanner() {
        if (Methodes.internet_diponible(activity)) {

            IntentIntegrator integrator = new IntentIntegrator(activity);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
            integrator.setPrompt("Veuillez scanner le QrCode du profil");
            integrator.setCameraId(0);
            integrator.setBeepEnabled(false);
            integrator.setBarcodeImageEnabled(false);
            integrator.initiateScan();
        }
    }

    private class Loading extends AsyncTask<Void, Void, Void> {
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
                tableUser = new TableUser(activity.getBaseContext());
                if (tableUser.user_exist(num_etu)) {
                    user_exist = true;
                    cadeau = tableUser.get_user(num_etu);
                } else {
                    user_exist = false;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mProgressDialog.hide();
            mProgressDialog.dismiss();
            if (Methodes.internet_diponible(activity)) {


                if (user_exist) {
                    if(user.getNum_etu().equals(cadeau.getNum_etu())){
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setCancelable(false);
                        builder.setMessage("Oooooh c'est mignon ça veut s'offrir un cadeau. Aucun problème personne ne te jugera :p")
                                .setPositiveButton("Je m'aime trop donc oui je veux mon cadeau", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        if (Methodes.internet_diponible(activity)) {
                                            Intent intent = new Intent(activity, ActivityCommande.class);
                                            intent.putExtra("user", new Gson().toJson(user));
                                            intent.putExtra("cadeau", new Gson().toJson(cadeau));
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.pull_in, R.anim.push_out);
                                            finish();
                                        }
                                    }
                                })
                                .setNegativeButton("En fait j'ai changé d'avis", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        if (Methodes.internet_diponible(activity)) {
                                        }
                                    }
                                });
                        builder.create();
                        builder.show();
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setCancelable(false);
                        builder.setMessage("Vous allez offrir une commande pour "+cadeau.getUsername()+".\n Voulez vous continuer ?")
                                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        if (Methodes.internet_diponible(activity)) {
                                            Intent intent = new Intent(activity, ActivityCommande.class);
                                            intent.putExtra("user", new Gson().toJson(user));
                                            intent.putExtra("cadeau", new Gson().toJson(cadeau));
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.pull_in, R.anim.push_out);
                                            finish();
                                        }
                                    }
                                })
                                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        if (Methodes.internet_diponible(activity)) {
                                        }
                                    }
                                });
                        builder.create();
                        builder.show();
                    }



                    //  Toast.makeText(activity, "trouvé :p", Toast.LENGTH_SHORT).show();
                } else {
                    Methodes.info_dialog("Utilisateur inconnu", activity);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        retour();
    }

    public void retour() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setMessage("Voulez vous abandonner la création de commande ?")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        if (Methodes.internet_diponible(activity)) {
                            Intent intent = new Intent(activity, ActivityAccueil.class);
                            intent.putExtra("user", new Gson().toJson(user));
                            startActivity(intent);
                            overridePendingTransition(R.anim.pull_in, R.anim.push_out);
                            finish();
                        }
                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        if (Methodes.internet_diponible(activity)) {
                        }
                    }
                });
        builder.create();
        builder.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {
                if (Methodes.internet_diponible(activity)) {
                    Methodes.info_dialog("Aucun QRCode détecté", activity);
                }
            } else {
                if (Methodes.internet_diponible(activity)) {
                    editText_num_etu.setText(result.getContents());
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
