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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import ensim.bdlc.R;
import ensim.bdlc.database.TableUser;
import ensim.bdlc.modele.User;
import ensim.bdlc.utils.Methodes;

public class ActivityChargeSolde extends AppCompatActivity {


    /**
     * ATTRIBUTES
     */
    //INTERFACE
    private Button button_scanner,button_pos, button_neg, button_charge;
    private LinearLayout linearLayout_scanner;
    private EditText editText_montant, editText_num_etu;
    private Toolbar toolbar;
    private ProgressDialog mProgressDialog;
    private CollapsingToolbarLayout toolbar_layout;
    //BDD
    private TableUser tableUser;
    //ARRAYLIST
    //OTHER
    private Activity activity;
    private float montant = 5.0f;
    private User user;
    private boolean user_exist = false;
    private String num_etu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //GET INTERFACE
        setContentView(R.layout.activity_charge_solde);
        button_scanner = (Button) findViewById(R.id.button_scanner);
        linearLayout_scanner = (LinearLayout) findViewById(R.id.linearLayout_scanner);
        button_pos = (Button) findViewById(R.id.button_pos);
        button_neg = (Button) findViewById(R.id.button_neg);
        button_charge = (Button) findViewById(R.id.button_charge);
        editText_montant = (EditText) findViewById(R.id.editText_montant);
        editText_num_etu = (EditText) findViewById(R.id.editText_num_etu);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_layout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

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
        editText_montant.setText("" + montant);
        setSupportActionBar(toolbar);
        toolbar_layout.setTitle("Rechargement");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);


        //LISTENERS
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retour();
            }
        });

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

        button_pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Methodes.internet_diponible(activity)) {
                    montant = Float.parseFloat(editText_montant.getText().toString());
                    montant = montant + 1.0f;
                    editText_montant.setText("" + montant);
                }
            }
        });

        button_neg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Methodes.internet_diponible(activity)) {
                    montant = Float.parseFloat(editText_montant.getText().toString());
                    if (montant > 1) {
                        montant = montant - 1.0f;
                        editText_montant.setText(montant + "");
                    }
                }
            }
        });

        button_charge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Methodes.internet_diponible(activity)) {
                    if (editText_num_etu.getText().toString().equals("")) {
                        Methodes.info_dialog("Veuillez identifier l'utilisateur", activity);
                    } else {
                        num_etu = editText_num_etu.getText().toString();
                        montant = Float.parseFloat(editText_montant.getText().toString());

                        new Loading().execute();
                    }
                }
            }
        });
    }

    public void scanner(){
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

    @Override
    public void onBackPressed() {
        retour();
    }

    public void retour() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setMessage("Voulez vous abandonner la recharge du compte ?")
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
                    AlertDialog.Builder builder_confirmation = new AlertDialog.Builder(activity);
                    builder_confirmation.setCancelable(false);
                    builder_confirmation.setMessage("Voulez vous sur de vouloir recharger la somme de " + editText_montant.getText().toString() + "€ pour l'utilisateur " + editText_num_etu.getText().toString() + " ?")
                            .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    new Loading_charge_credit().execute();
                                }
                            })
                            .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    if (Methodes.internet_diponible(activity)) {
                                    }
                                }
                            });
                    builder_confirmation.create();
                    builder_confirmation.show();
                } else {
                    Methodes.info_dialog("Utilisateur inconnu", activity);
                }
            }
        }
    }

    private class Loading_charge_credit extends AsyncTask<Void, Void, Void> {
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
                User user_charge = tableUser.get_user(num_etu);
                user_charge.setCredit(user_charge.getCredit() + montant);
                tableUser.changer_credit(user_charge);
                user = tableUser.get_user(user.getNum_etu());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mProgressDialog.hide();
            mProgressDialog.dismiss();
            if (Methodes.internet_diponible(activity)) {
                AlertDialog.Builder builder_info = new AlertDialog.Builder(activity);
                builder_info.setCancelable(false);
                builder_info.setMessage("Le compte a été rechargé")
                        .setPositiveButton("Revenir à l'accueil", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                if (Methodes.internet_diponible(activity)) {
                                    Intent intent = new Intent(activity, ActivityAccueil.class);
                                    intent.putExtra("user", new Gson().toJson(user));
                                    activity.startActivity(intent);
                                    activity.overridePendingTransition(R.anim.pull_in, R.anim.push_out);
                                    activity.finish();
                                }
                            }
                        });
                builder_info.create();
                builder_info.show();
            }
        }
    }
}
