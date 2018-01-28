package ensim.bdlc.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import ensim.bdlc.R;
import ensim.bdlc.database.TableArticle;
import ensim.bdlc.database.TablePanier;
import ensim.bdlc.database.TableUser;
import ensim.bdlc.modele.Panier;
import ensim.bdlc.modele.User;
import ensim.bdlc.utils.Methodes;

public class ActivityRecapCommande extends AppCompatActivity {


    protected int position_navigation;
    /**
     * ATTRIBUTES
     */
    //INTERFACE
    private FloatingActionButton fab_state;
    private Toolbar toolbar;
    private LinearLayout layout_infos;
    private TextView textView_id_user, textView_credit, textView_username;
    private ImageView imageView_qrcode;
    private ProgressDialog mProgressDialog;
    private CollapsingToolbarLayout toolbar_layout;
    private LinearLayout linearLayout_scroll;
    private ListView listView_produit;
    private BottomNavigationView navigation;
    //BDD
    private TablePanier tablePanier;
    private TableUser tableUser;
    //ARRAYLIST
    private String split[] = new String[3];
    private HashMap<String, String> map_produit_panier;
    private ArrayList<HashMap<String, String>> listItem_produit_panier = new ArrayList<>();
    //OTHER
    private int state_commande = 0;
    private Activity activity;
    private Panier panier;
    private User user, user_owner;
    private Bitmap bitmap;
    private String state;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (Methodes.internet_diponible(activity)) {
                switch (item.getItemId()) {
                    case R.id.navigation_details_commande:
                        position_navigation = 0;
                        layout_infos.setVisibility(View.GONE);
                        listView_produit.setVisibility(View.VISIBLE);
                        toolbar_layout.setTitle("Commande N°" + split[2]);

                        return true;
                    case R.id.navigation_details_user:
                        position_navigation = 1;
                        layout_infos.setVisibility(View.VISIBLE);
                        listView_produit.setVisibility(View.GONE);
                        toolbar_layout.setTitle("Profil de " + user_owner.getUsername());

                        return true;
                }
            }

            return false;
        }

    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setCancelable(false);
                builder.setMessage("Etes-vous sur de vouloir annuler la commande ?")
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                new DeleteCommande().execute();
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


                return true;

            case R.id.action_accueil:
               retour();


                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //GET INTERFACE
        setContentView(R.layout.activity_recap_commande);
        fab_state = (FloatingActionButton) findViewById(R.id.fab_state);
        listView_produit = (ListView) findViewById(R.id.listView_produit);
        layout_infos = (LinearLayout) findViewById(R.id.layout_infos);
        linearLayout_scroll = (LinearLayout) findViewById(R.id.linearLayout_scroll);
        textView_id_user = (TextView) findViewById(R.id.textView_id_user);
        textView_credit = (TextView) findViewById(R.id.textView_credit);
        textView_username = (TextView) findViewById(R.id.textView_username);
        imageView_qrcode = (ImageView) findViewById(R.id.imageView_qrcode);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_layout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        navigation = (BottomNavigationView) findViewById(R.id.navigation_recap_commande);

        //INITIALIZE
        activity = this;
        setSupportActionBar(toolbar);
        layout_infos.setVisibility(View.GONE);
        listView_produit.setVisibility(View.VISIBLE);
        //listView_produit.setNestedScrollingEnabled(true);
        //linearLayout_scroll.setNestedScrollingEnabled(true);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setTitle("Veuillez patienter");
        mProgressDialog.setMessage("Connexion en cours...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(false);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.getString("user") != null) {
                user = new Gson().fromJson(extras.getString("user"), User.class);
            }
            if (extras.getString("panier") != null) {
                panier = new Gson().fromJson(extras.getString("panier"), Panier.class);
            }
        }

        split = panier.getId_panier().split("_");

        new Loading().execute();

        //LISTENER

        fab_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Methodes.internet_diponible(activity)) {

                    //TODO changer state commande
                    state = "finie";
                    new ChangeState().execute();
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setCancelable(false);
                    builder.setMessage("Confirmez-vous que la commande a été donnée")
                            .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    fab_state.setImageResource(R.drawable.icon_valider_noir);
                                    mProgressDialog.dismiss();
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

                                }
                            });
                    builder.create();
                    builder.show();

                }


            }
        });


    }

    @Override
    public void onBackPressed() {
        retour();
    }

    public void retour() {
        if (Methodes.internet_diponible(activity)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setCancelable(false);
            builder.setMessage("Voulez vous abandonner la préparation de commande ?")
                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            mProgressDialog.dismiss();
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
                user = tableUser.get_user(user.getNum_etu());
                tablePanier = new TablePanier(activity.getBaseContext());
                user_owner = tableUser.get_user(panier.getId_owner());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (Methodes.internet_diponible(activity)) {
                mProgressDialog.hide();
                String prix = "" + String.format(" %.2f", user_owner.getCredit());
                prix = prix.replace(",", "€");
                prix = prix.replace(".", "€");
                textView_credit.setText("Votre soldes : " + prix);
                textView_id_user.setText("" + user_owner.getNum_etu());
                textView_username.setText("" + user_owner.getUsername());
                bitmap = Methodes.generateQRBitmap("" + user_owner.getNum_etu());
                imageView_qrcode.setImageBitmap(bitmap);
                if (panier.getList_article().size() > 0) {
                    for (int i = 0; i < panier.getList_article().size(); i++) {
                        map_produit_panier = new HashMap<>();
                        map_produit_panier.put("id", "" + panier.getList_article().get(i).getProduit().getId());


                        String prix_vente = String.format(" %.2f", +panier.getList_article().get(i).getProduit().getPrix_vente());
                        prix_vente = prix_vente.replace(",", "€");
                        prix_vente = prix_vente.replace(".", "€");

                        map_produit_panier.put("info", panier.getList_article().get(i).getProduit().getNom() + "\n" +
                                prix_vente + "\nquantité : " +
                                panier.getList_article().get(i).getQuantite());
                        listItem_produit_panier.add(map_produit_panier);
                    }
                } else {
                    retour();
                }

                SimpleAdapter mSchedule_snacks = new SimpleAdapter(activity.getBaseContext(),
                        listItem_produit_panier, R.layout.layout_produit, new String[]{"info"}, new int[]{R.id.textView_info}) {
                    public View getView(int position, View convertView, ViewGroup parent) {
                        HashMap<String, String> map = (HashMap<String, String>) listView_produit
                                .getItemAtPosition(position);
                        View view = super.getView(position, convertView, parent);
                        ImageView image_view = (ImageView) view.findViewById(R.id.image_view);
                        image_view.setImageResource(R.drawable.icon_product);
                        return view;
                    }
                };
                toolbar_layout.setTitle("Commande N°" + split[2]);
                listView_produit.setAdapter(mSchedule_snacks);
            }


        }

    }

    private class ChangeState extends AsyncTask<Void, Void, Void> {
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
                panier.setState(state);
                tablePanier.change_state_panier(panier);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mProgressDialog.hide();


        }
    }


        private class DeleteCommande extends AsyncTask<Void, Void, Void> {
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
                    //ajout argent panier sur compte user
                    User client = tableUser.get_user(panier.getId_owner());
                    client.setCredit(client.getCredit()+panier.getPrix_total());
                    tableUser.changer_credit(client);

                    String id_panier =panier.getId_panier();
                    tablePanier.delete_panier(id_panier);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                mProgressDialog.hide();


                if (Methodes.internet_diponible(activity)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setCancelable(false);
                    builder.setMessage("La commande a été supprimé")
                            .setPositiveButton("Accueil", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
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
        }

}
