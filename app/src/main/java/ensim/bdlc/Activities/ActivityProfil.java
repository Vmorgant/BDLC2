package ensim.bdlc.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import ensim.bdlc.R;
import ensim.bdlc.database.TablePanier;
import ensim.bdlc.database.TableUser;
import ensim.bdlc.modele.Article;
import ensim.bdlc.modele.Panier;
import ensim.bdlc.modele.Produit;
import ensim.bdlc.modele.User;
import ensim.bdlc.utils.Methodes;

public class ActivityProfil extends AppCompatActivity {


    protected int position_navigation;
    /**
     * ATTRIBUTES
     */
    //INTERFACE
    private Toolbar toolbar;
    private TextView textView_id_user, textView_credit, textView_idpanier, textView_prix_panier;
    private ImageView imageView_qrcode, imageView_qrcode_alertDialogView;
    private ListView listView, listView_articles;
    private FloatingActionButton fab_edit;
    private LinearLayout layout_infos;
    private LinearLayout linearLayout_scroll;
    private ProgressDialog mProgressDialog;
    private CollapsingToolbarLayout toolbar_layout;
    private BottomNavigationView navigation;
    private Button button_delete, button_close, button_donwload;
    private View alertDialogView;
    private AlertDialog.Builder adb;
    private MyCustomAdapterPanier dataAdapter = null;
    private MyCustomAdapterProduit dataAdapter_produit = null;
    private TextView textView_info, textView_prix;
    //BDD
    private TableUser tableUser;
    //ARRAYLIST
    private HashMap<String, String> map_commandes, map_history;
    private ArrayList<HashMap<String, String>> listItem_commandes = new ArrayList<>();
    private ArrayList<HashMap<String, String>> listItem_history = new ArrayList<>();
    private ArrayList<Panier> list_commandes = new ArrayList<>();
    private ArrayList<Panier> list_history = new ArrayList<>();
    //OTHER
    private Activity activity;
    private Bitmap bitmap, bitmap_commande;
    private User user;
    private TablePanier tablePanier;
    private Panier panier;
    private String id_panier;
    private boolean list_commandes_empty, list_history_empty;


    /**
     * BOTTOM MENU
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (Methodes.internet_diponible(activity)) {
                switch (item.getItemId()) {
                    case R.id.navigation_details_user:
                        position_navigation = 0;
                        load_profil();
                        return true;
                    case R.id.navigation_mes_commandes:
                        position_navigation = 1;
                        load_commandes();
                        return true;
                    case R.id.navigation_historique:
                        position_navigation = 2;
                        load_history();
                        return true;
                }

            }
            return false;
        }

    };

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //GET INTERFACE
        setContentView(R.layout.activity_profil);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_layout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        linearLayout_scroll = (LinearLayout) findViewById(R.id.linearLayout_scroll);
        listView = (ListView) findViewById(R.id.listView);
        layout_infos = (LinearLayout) findViewById(R.id.layout_infos);
        navigation = (BottomNavigationView) findViewById(R.id.navigation_profil);
        textView_credit = (TextView) findViewById(R.id.textView_credit);
        textView_id_user = (TextView) findViewById(R.id.textView_id_user);
        imageView_qrcode = (ImageView) findViewById(R.id.imageView_qrcode);
        fab_edit = (FloatingActionButton) findViewById(R.id.fab_edit);

        //INITIALIZE
        activity = this;
       // listView.setNestedScrollingEnabled(true);
        //linearLayout_scroll.setNestedScrollingEnabled(true);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        position_navigation = 0;
        list_commandes_empty = false;
        list_history_empty = false;
        mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setTitle("Veuillez patienter");
        mProgressDialog.setMessage("Connexion en cours...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(false);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.getString("user") != null) {
                user = new Gson().fromJson(extras.getString("user"), User.class);
            }
        }

        new Loading().execute();






        //LISTENERS
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retour();
            }
        });
        fab_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.dismiss();

                Intent intent = new Intent(activity, ActivityEditProfil.class);
                intent.putExtra("user", new Gson().toJson(user));
                startActivity(intent);
                overridePendingTransition(R.anim.pull_in, R.anim.push_out);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        retour();
    }

    public void retour() {
        mProgressDialog.dismiss();

        if (Methodes.internet_diponible(activity)) {
            Intent intent = new Intent(activity, ActivityAccueil.class);
            intent.putExtra("user", new Gson().toJson(user));
            startActivity(intent);
            overridePendingTransition(R.anim.pull_in, R.anim.push_out);
            finish();
        }
    }

    public void load_profil() {
        listView.setAdapter(null);
        listView.setVisibility(View.GONE);
        layout_infos.setVisibility(View.VISIBLE);
    }

    public void load_commandes() {
        layout_infos.setVisibility(View.GONE);
        listView.setAdapter(null);
        listView.setVisibility(View.VISIBLE);

        if (list_commandes.size() == 0) {
            list_commandes_empty = true;
            list_commandes.add(new Panier("Aucune commande"));
        }

        dataAdapter = new MyCustomAdapterPanier(this, R.layout.layout_produit, list_commandes);
        listView.setAdapter(dataAdapter);
    }

    public void load_history() {
        layout_infos.setVisibility(View.GONE);
        listView.setAdapter(null);
        listView.setVisibility(View.VISIBLE);
        if (list_history.size() == 0) {
            list_history_empty = true;
            list_history.add(new Panier("Aucune historique"));
        }

        dataAdapter = new MyCustomAdapterPanier(this, R.layout.layout_produit, list_history);
        listView.setAdapter(dataAdapter);
    }


    private class Loading extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (Methodes.internet_diponible(activity)) {
                TablePanier tablePanier = new TablePanier(activity.getBaseContext());
                ArrayList<Panier> list_commande = tablePanier.get_panier(user.getNum_etu(), "commande");
                ArrayList<Panier> list_preparation = tablePanier.get_panier(user.getNum_etu(), "preparation");
                list_commandes.addAll(list_commande);
                list_commandes.addAll(list_preparation);
                list_history = tablePanier.get_panier(user.getNum_etu(), "finie");
                tableUser = new TableUser(activity.getBaseContext());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (Methodes.internet_diponible(activity)) {
                mProgressDialog.hide();

                user = tableUser.get_user(user.getNum_etu());
                toolbar_layout.setTitle("Bonjour " + user.getUsername());
                bitmap = Methodes.generateQRBitmap("" + user.getNum_etu());
                imageView_qrcode.setImageBitmap(bitmap);
                String prix = "" + String.format(" %.2f", user.getCredit());
                prix = prix.replace(",", "€");
                prix = prix.replace(".", "€");
                textView_credit.setText("Votre solde :" + prix);
                textView_id_user.setText("" + user.getNum_etu());
                load_profil();
            }


        }

    }


    private class Loading_panier extends AsyncTask<Void, Void, Void> {
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
                tablePanier = new TablePanier(activity.getBaseContext());
                panier = tablePanier.get_panier(id_panier);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mProgressDialog.hide();

            bitmap_commande = Methodes.generateQRBitmap("" + panier.getId_panier());
            imageView_qrcode_alertDialogView.setImageBitmap(bitmap_commande);
            String split[] = panier.getId_panier().split("_");
            String prix_panier = String.format(" %.2f", panier.getPrix_total());
            prix_panier = prix_panier.replace(",", "€");
            prix_panier = prix_panier.replace(".", "€");
            textView_prix_panier.setText("Prix panier :" + prix_panier);
            textView_idpanier.setText("Numéro de commande : " + split[2]);

            ArrayList<Article> list_articles = panier.getList_article();

            dataAdapter_produit = new MyCustomAdapterProduit(activity, R.layout.layout_produit, list_articles);
            listView_articles.setAdapter(dataAdapter_produit);

            adb.setView(alertDialogView);
            final AlertDialog alertDialog = adb.show();

            //LISTENERS
            button_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Methodes.internet_diponible(activity)) {
                        AlertDialog.Builder builder_delete = new AlertDialog.Builder(activity);
                        builder_delete.setCancelable(false);
                        builder_delete.setMessage("Etes-vous sur de supprimer cette commande ? Vous serez remboursé.")
                                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        //TODO DELETE
                                    }
                                })
                                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();

                                    }
                                });
                        builder_delete.create();
                        builder_delete.show();
                    }

                }
            });

            button_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    if (Methodes.internet_diponible(activity)) {
                    }
                }
            });
        }
    }


    private class MyCustomAdapterPanier extends ArrayAdapter<Panier> {

        private ArrayList<Panier> groupeList;


        public MyCustomAdapterPanier(Context context, int textViewResourceId,
                                     ArrayList<Panier> groupeList) {
            super(context, textViewResourceId, groupeList);
            this.groupeList = groupeList;

        }

        @Override
        public View getView(final int position, View convertViewProduit, ViewGroup parent) {

            ViewHolder holder;

            if (convertViewProduit == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertViewProduit = vi.inflate(R.layout.layout_produit, null);

                //GET INTERFACE
                holder = new ViewHolder();
                holder.image_view = (ImageView) convertViewProduit.findViewById(R.id.image_view);
                holder.textView_info = (TextView) convertViewProduit.findViewById(R.id.textView_info);
                holder.frameLayout = (FrameLayout) convertViewProduit.findViewById(R.id.frameLayout);
                holder.textView_prix = (TextView) convertViewProduit.findViewById(R.id.textView_prix);

                //INITIALIZE
                holder.image_view.setImageResource(R.drawable.icon_basket);
                if (list_commandes_empty && (position_navigation == 1)) {
                    holder.frameLayout.setVisibility(View.GONE);
                    holder.textView_info.setText("Aucune commande passée");
                    holder.textView_info.setGravity(Gravity.CENTER_HORIZONTAL);
                } else {

                    if (list_history_empty && (position_navigation == 2)) {
                        holder.frameLayout.setVisibility(View.GONE);
                        holder.textView_info.setText("Aucun historique à afficher");
                        holder.textView_info.setGravity(Gravity.CENTER_HORIZONTAL);
                    } else {
                        holder.frameLayout.setVisibility(View.VISIBLE);
                        String prix = String.format(" %.2f", groupeList.get(position).getPrix_total());
                        prix = prix.replace(",", "€");
                        prix = prix.replace(".", "€");
                        String split[] = groupeList.get(position).getId_panier().split("_");
                        holder.textView_info.setText("Commande n°" + split[2]);
                        if (groupeList.get(position).getState().equals("commande")) {
                            holder.textView_prix.setText("Le " + split[0] + "\nPrix panier :" + prix + "\nCommande passée");
                        } else {
                            if (groupeList.get(position).getState().equals("finie")) {
                                holder.textView_prix.setText("Le " + split[0] + "\nPrix panier :" + prix + "\n Commande récupérée");
                            } else {
                                holder.textView_prix.setText("Le " + split[0] + "\nPrix panier :" + prix + "\nEn cours de préparation");
                            }
                        }
                    }


                }
                convertViewProduit.setTag(holder);
            } else {
                holder = (ViewHolder) convertViewProduit.getTag();
            }

            //INITIALIZE
            holder.image_view.setImageResource(R.drawable.icon_basket);
            if (list_commandes_empty && (position_navigation == 1)) {
                holder.frameLayout.setVisibility(View.GONE);
                holder.textView_info.setText("Aucune commande passée");
                holder.textView_info.setGravity(Gravity.CENTER_HORIZONTAL);
            } else {

                if (list_history_empty && (position_navigation == 2)) {
                    holder.frameLayout.setVisibility(View.GONE);
                    holder.textView_info.setText("Aucun historique à afficher");
                    holder.textView_info.setGravity(Gravity.CENTER_HORIZONTAL);
                } else {
                    holder.frameLayout.setVisibility(View.VISIBLE);
                    String prix = String.format(" %.2f", groupeList.get(position).getPrix_total());
                    prix = prix.replace(",", "€");
                    prix = prix.replace(".", "€");
                    String split[] = groupeList.get(position).getId_panier().split("_");
                    holder.textView_info.setText("Commande n°" + split[2]);
                    if (groupeList.get(position).getState().equals("commande")) {
                        holder.textView_prix.setText("Le " + split[0] + "\nPrix panier :" + prix + "\nCommande passée");
                    } else {
                        if (groupeList.get(position).getState().equals("finie")) {
                            holder.textView_prix.setText("Le " + split[0] + "\nPrix panier :" + prix + "\n Commande récupérée");
                        } else {
                            holder.textView_prix.setText("Le " + split[0] + "\nPrix panier :" + prix + "\nEn cours de préparation");
                        }
                    }
                }
            }


            //LISTENER
            convertViewProduit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    LayoutInflater factory = LayoutInflater.from(activity);
                    alertDialogView = factory.inflate(R.layout.dialog_visualiser_commande, null);
                    adb = new AlertDialog.Builder(activity);

                    //GET INTERFACE
                    button_delete = (Button) alertDialogView.findViewById(R.id.button_delete);
                    button_close = (Button) alertDialogView.findViewById(R.id.button_close);
                    button_donwload = (Button) alertDialogView.findViewById(R.id.button_donwload);
                    imageView_qrcode_alertDialogView = (ImageView) alertDialogView.findViewById(R.id.imageView_qrcode);
                    textView_idpanier = (TextView) alertDialogView.findViewById(R.id.textView_idpanier);
                    textView_prix_panier = (TextView) alertDialogView.findViewById(R.id.textView_prix_panier);
                    listView_articles = (ListView) alertDialogView.findViewById(R.id.listView_articles);

                    id_panier = groupeList.get(position).getId_panier();
                    if (!list_commandes_empty && position_navigation ==1) {
                        new Loading_panier().execute();
                    }
                    if (!list_history_empty && position_navigation ==2) {
                        new Loading_panier().execute();
                    }

                    button_donwload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            OutputStream fOut = null;
                            File fileDir = new File(Environment.getExternalStorageDirectory() +
                                    "/commande_qr_code");
                            fileDir.mkdirs();

                            String filename = ""+panier.getId_panier();
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
                    });


                }
            });


            return convertViewProduit;
        }

        private class ViewHolder {
            ImageView image_view;
            TextView textView_info;
            FrameLayout frameLayout;
            TextView textView_prix;

        }


    }

    private class MyCustomAdapterProduit extends ArrayAdapter<Article> {

        private ArrayList<Article> groupeList;
        private ArrayList<MyCustomAdapterProduit.ViewHolder> list_holder;


        public MyCustomAdapterProduit(Context context, int textViewResourceId,
                                      ArrayList<Article> groupeList) {
            super(context, textViewResourceId, groupeList);
            this.groupeList = new ArrayList<>();
            this.groupeList.addAll(groupeList);
            this.list_holder = new ArrayList<>();
        }

        private class ViewHolder {
            ImageView image_view;
            TextView textView_info;
            FrameLayout frameLayout;
            TextView textView_prix;

        }

        @Override
        public View getView(final int position, View convertViewProduit, ViewGroup parent) {

            MyCustomAdapterProduit.ViewHolder holder;

            if (convertViewProduit == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertViewProduit = vi.inflate(R.layout.layout_produit, null);

                //GET INTERFACE
                holder = new MyCustomAdapterProduit.ViewHolder();

                holder.image_view = (ImageView) convertViewProduit.findViewById(R.id.image_view);
                holder.textView_info = (TextView) convertViewProduit.findViewById(R.id.textView_info);
                holder.frameLayout = (FrameLayout) convertViewProduit.findViewById(R.id.frameLayout);
                holder.textView_prix = (TextView) convertViewProduit.findViewById(R.id.textView_prix);


                //INITIALIZE

                String prix = String.format(" %.2f", groupeList.get(position).getProduit().getPrix_vente());
                prix = prix.replace(",", "€");
                prix = prix.replace(".", "€");
                holder.textView_info.setText(groupeList.get(position).getProduit().getNom());
                holder.textView_prix.setText("" + prix);

                int placement_tmp = panier.produit_present(groupeList.get(position).getProduit().getNom());
                if (placement_tmp != -1) {
                    holder.textView_prix.setText("Prix unitaire:" + prix + "\nQuantité achetée : " + panier.getList_article().get(placement_tmp).getQuantite());

                } else {
                    holder.textView_prix.setText("Prix unitaire: " + prix);
                }

                if (groupeList.get(position).getProduit().getImage().equals("icon_formule")) {
                    holder.image_view.setImageResource(R.drawable.icon_formule);
                } else {
                    try {
                        Picasso.with(activity).cancelRequest(holder.image_view);
                        Picasso.with(activity).load(groupeList.get(position).getProduit().getImage()).centerCrop().fit().into(holder.image_view);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                        if (groupeList.get(position).getProduit().getCategorie().equals("boisson")) {
                            holder.image_view.setImageResource(R.drawable.icon_drink);
                        } else {
                            holder.image_view.setImageResource(R.drawable.icon_product);
                        }
                    }
                }


                convertViewProduit.setTag(holder);
                list_holder.add(holder);
            } else {
                holder = (MyCustomAdapterProduit.ViewHolder) convertViewProduit.getTag();
            }

            //INITIALIZE
            if (groupeList.get(position).getProduit().getImage().equals("icon_formule")) {
                holder.image_view.setImageResource(R.drawable.icon_formule);
            } else {
                try {
                    Picasso.with(activity).cancelRequest(holder.image_view);
                    Picasso.with(activity).load(groupeList.get(position).getProduit().getImage()).centerCrop().fit().into(holder.image_view);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    if (groupeList.get(position).getProduit().getCategorie().equals("boisson")) {
                        holder.image_view.setImageResource(R.drawable.icon_drink);
                    } else {
                        holder.image_view.setImageResource(R.drawable.icon_product);
                    }
                }
            }

            String prix = String.format(" %.2f", groupeList.get(position).getProduit().getPrix_vente());
            prix = prix.replace(",", "€");
            prix = prix.replace(".", "€");
            holder.textView_info.setText(groupeList.get(position).getProduit().getNom());

            int placement_tmp = panier.produit_present(groupeList.get(position).getProduit().getNom());
            if (placement_tmp != -1) {
                holder.textView_prix.setText("Prix unitaire:" + prix + "\nQuantité achetée : " + panier.getList_article().get(placement_tmp).getQuantite());

            } else {
                holder.textView_prix.setText("Prix unitaire: " + prix);
            }


            return convertViewProduit;
        }


    }

}
