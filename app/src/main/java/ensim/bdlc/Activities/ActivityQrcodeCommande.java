package ensim.bdlc.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import ensim.bdlc.R;
import ensim.bdlc.database.TablePanier;
import ensim.bdlc.database.TableProduit;
import ensim.bdlc.database.TableUser;
import ensim.bdlc.modele.Panier;
import ensim.bdlc.modele.User;
import ensim.bdlc.utils.Methodes;

public class ActivityQrcodeCommande extends AppCompatActivity {

    /**
     * ATTRIBUTES
     */
    //INTERFACE
    private ImageView imageView;
    private TextView textView_soldes, textView_idpanier, textView_info;
    private CollapsingToolbarLayout toolbar_layout;
    private Toolbar toolbar;
    private ProgressDialog mProgressDialog;
    //BDD
    //ARRAYLIST
    //OTHER
    private Panier panier;
    private Bitmap bitmap;
    private User user, client, cadeau;
    private float restant;


    private Activity activity;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_accueil, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //GET INTERFACE
        setContentView(R.layout.activity_qrcode_commande);
        imageView = (ImageView) findViewById(R.id.imageView_qrcode);
        textView_soldes = (TextView) findViewById(R.id.textView_soldes);
        textView_info = (TextView) findViewById(R.id.textView_info);

        textView_idpanier = (TextView) findViewById(R.id.textView_idpanier);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_layout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        //INITIALIZE
        activity = this;
        mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setTitle("Veuillez patienter");
        mProgressDialog.setMessage("Connexion en cours...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(false);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_accueil);
        toolbar_layout.setTitle("Fin de commande");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.getString("mon_panier") != null) {
                panier = new Gson().fromJson(extras.getString("mon_panier"), Panier.class);
            }
            if (extras.getString("user") != null) {
                user = new Gson().fromJson(extras.getString("user"), User.class);
            }
            if (extras.getString("client") != null) {
                client = new Gson().fromJson(extras.getString("client"), User.class);
            }
            if (extras.getString("cadeau") != null) {
                cadeau = new Gson().fromJson(extras.getString("cadeau"), User.class);
            }
        }

        if (client != null) {
            restant = client.getCredit() - panier.getPrix_total();
        } else {
            if (cadeau != null) {
                restant = user.getCredit() - panier.getPrix_total();
            } else {
                restant = user.getCredit() - panier.getPrix_total();
            }
        }


        new Connexion().execute();

        //LISTENER
        /*fab_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) == true)
                        {
                            Toast.makeText(activity, "Le QrCode ne peut être exporter sans les authorisations.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 2);
                            }
                        }
                    }
                }
                else
                {
                    export_qrcode();
                }


            }
        });*/

        toolbar.setOnMenuItemClickListener(new android.support.v7.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_accueil:
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setCancelable(false);
                        builder.setMessage("Etes-vous sur de vouloir quitter. Vous pouvez retrouver votre commande dans votre profil.")
                                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
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

                                    }
                                });
                        builder.create();
                        builder.show();
                        return true;
                    default:
                        return false;
                }
            }
        });


    }

    public void export_qrcode(){
        OutputStream fOut = null;
        File fileDir = new File(Environment.getExternalStorageDirectory() +
                "/commande_qr_code");
        fileDir.mkdirs();

        String filename;
        if (client != null) {
            filename = ""+panier.getId_panier();
        } else {
            if (cadeau != null) {
                filename = ""+ panier.getId_panier();
            } else {
                filename = ""+ panier.getId_panier();
            }
        }


        filename = filename.replace("/", "");
        filename = filename.replace(".", "");
        filename = filename.replace(" ", "");
        File file = new File(fileDir, filename + ".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
        try {
            fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
            fOut.flush(); // Not really required
            fOut.close(); // do not forget to close the stream
            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
            Methodes.info_dialog("Exportation du QrCode réussie\nIl est dans le dossier commande_qr_code", activity);
        } catch (FileNotFoundException e) {
            Methodes.info_dialog("Un problème est survenu", activity);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ERROR", "export ->" + e);
            Methodes.info_dialog("Un problème est survenu", activity);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if(requestCode == 2)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                export_qrcode();
            }
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(shouldShowRequestPermissionRationale(permissions[0]) == false)
                {
                    Toast.makeText(activity, "Le QrCode ne peut être exporter sans les authorisations.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //Toast.makeText(activity, "Le QrCode ne peut être exporter sans les authorisations.", Toast.LENGTH_SHORT).show();
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        retour();
    }

    public void retour() {
        String message_info;
        if (client != null) {
            message_info = "Etes-vous sur de vouloir quitter? Le client pourra retrouver son QrCode dans son profil.";
        } else {
            if (cadeau != null) {
                message_info = "Etes-vous sur de vouloir quitter? " + cadeau.getUsername() + "\nIl pourra retrouver son QrCode dans son profil.";
            } else {
                message_info = "Etes-vous sur de vouloir quitter? Vous pouvez retrouver votre commande dans votre profil.";
            }
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setMessage(message_info)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent intent = new Intent(activity, ActivityAccueil.class);
                        intent.putExtra("user", new Gson().toJson(user));
                        if (client != null) {
                            intent.putExtra("client", new Gson().toJson(client));
                        }
                        if (cadeau != null) {
                            intent.putExtra("cadeau", new Gson().toJson(cadeau));
                        }
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
                if (restant >= 0) {
                    if (client != null) {
                        client.setCredit(client.getCredit() - panier.getPrix_total());
                        tableUser.changer_credit(client);
                    } else {

                        user.setCredit(user.getCredit() - panier.getPrix_total());
                        tableUser.changer_credit(user);

                    }
                }
                TableProduit tableProduit = new TableProduit(activity.getBaseContext());
                tableProduit.change_stock(panier.getList_article());



            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mProgressDialog.hide();

            if (restant >= 0) {
                Methodes.info_dialog("Le compte a été débité", activity);
                textView_info.setText("Commande validée ! ");
                TablePanier tablePanier = new TablePanier(activity.getBaseContext());
                if (client != null) {
                    panier.setState("finie");
                }else{
                    panier.setState("commande");
                }

                panier.setId_panier(tablePanier.add_panier(panier));
                bitmap = Methodes.generateQRBitmap("" + panier.getId_panier());
                String split[] = panier.getId_panier().split("_");
                textView_idpanier.setText("Numéro de commande : " + split[2]);

            } else {

                if (Methodes.internet_diponible(activity)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setCancelable(false);
                    builder.setMessage("Il n'y a pas assez d'argent sur le compte. Veuillez recharger.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
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
            String prix = null;
            if (client != null) {
                prix = "" + String.format(" %.2f", client.getCredit());
                prix = prix.replace(",", "€");
                prix = prix.replace(".", "€");
                textView_soldes.setText("Il reste actuellement à " + client.getUsername() + " la somme de " + prix);
            } else {
                prix = "" + String.format(" %.2f", user.getCredit());
                prix = prix.replace(",", "€");
                prix = prix.replace(".", "€");
                textView_soldes.setText("Il vous reste actuellement" + prix);
            }
            if (cadeau != null) {
                Methodes.info_dialog("Vous venez d'offrir un cadeau à " + cadeau.getUsername() + " ! ", activity);
            }


            imageView.setImageBitmap(bitmap);
        }
    }
}

