package ensim.bdlc.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

import java.util.ArrayList;

import ensim.bdlc.R;
import ensim.bdlc.database.TableUser;
import ensim.bdlc.modele.Article;
import ensim.bdlc.modele.Panier;
import ensim.bdlc.modele.User;
import ensim.bdlc.utils.Methodes;
import ensim.bdlc.database.TableProduit;
import ensim.bdlc.modele.Produit;

public class ActivityCommande extends AppCompatActivity {

    /**
     * ATTRIBUTES
     */
    //INTERFACE
    private FloatingActionButton fab_panier;
    private ProgressDialog mProgressDialog;
    private ListView listView;
    private CollapsingToolbarLayout toolbar_layout;
    private LinearLayout linearLayout_scroll;
    private Toolbar toolbar;
    private BottomNavigationView navigation;
    private TextView textView_qte, textView_info, textView_prix;
    private Button button_close;
    //BDD
    //ARRAYLIST
    private ArrayList<Produit> list_produit_boisson = new ArrayList<>();
    private ArrayList<Produit> list_produit_snack = new ArrayList<>();
    private ArrayList<Produit> list_produit_formule = new ArrayList<>();
    //OTHER
    private Activity activity;
    protected Boolean type;
    private int qte_produit, qte_produit_old;
    private Panier panier;
    private User user,client,cadeau;
    private int position_vue;
    private MyCustomAdapterProduit dataAdapter = null;
    private boolean list_boisson_empty;
    private boolean list_snack_empty;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            listView.setAdapter(null);
            if (Methodes.internet_diponible(activity)) {
                switch (item.getItemId()) {
                    case R.id.navigation_boissons:
                        load_navigation_boissons();
                        position_vue = 0;
                        return true;
                    case R.id.navigation_snacks:
                        load_navigation_snacks();
                        position_vue = 1;
                        return true;
                    case R.id.navigation_formules:
                        load_navigation_formules();
                        position_vue = 2;
                        return true;
                }
            }

            return false;
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //GET INTERFACE
        setContentView(R.layout.activity_commande);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_layout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        linearLayout_scroll = (LinearLayout) findViewById(R.id.linearLayout_scroll);
        navigation = (BottomNavigationView) findViewById(R.id.navigation_produits);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        listView = (ListView) findViewById(R.id.listView_produit);
        fab_panier = (FloatingActionButton) findViewById(R.id.fab_panier);

        //INITIALIZE
        activity = this;
        position_vue = 0;
        setSupportActionBar(toolbar);
        //linearLayout_scroll.setNestedScrollingEnabled(true);
        //listView.setNestedScrollingEnabled(true);
        list_boisson_empty = false;
        list_snack_empty = false;


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.getString("user") != null) {
                user = new Gson().fromJson(extras.getString("user"), User.class);
            }
            if (extras.getString("client") != null) {
                client = new Gson().fromJson(extras.getString("client"), User.class);
            }
            if (extras.getString("cadeau") != null) {
                cadeau = new Gson().fromJson(extras.getString("cadeau"), User.class);
            }
            if (extras.getString("mon_panier") != null) {
                panier = new Gson().fromJson(extras.getString("mon_panier"), Panier.class);
            } else {
                if(client!=null){
                    panier = new Panier(client.getNum_etu());
                }else{
                    if(cadeau!=null){
                        panier = new Panier(cadeau.getNum_etu());
                    }else{
                        panier = new Panier(user.getNum_etu());
                    }

                }

            }
        }

        type = true;

        String prix = String.format(" %.2f", +panier.getPrix_total());
        prix = prix.replace(",", "€");
        prix = prix.replace(".", "€");
        toolbar_layout.setTitle("Total panier :" + prix);
        mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setTitle("Veuillez patienter");
        mProgressDialog.setMessage("Connexion en cours...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(false);

        //LISTENER
        fab_panier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (panier.getList_article().isEmpty() && panier.getList_formule().isEmpty()) {
                    Methodes.info_dialog("Vous n'avez aucun article dans votre panier", activity);
                } else {
                    if (Methodes.internet_diponible(activity)) {
                        new Loading_ticket().execute();
                    }

                }
            }
        });

        //ASYNCTASK
        new Loading().execute();


    }


    @Override
    public void onBackPressed() {

        retour();
    }

    public void retour() {
        if (Methodes.internet_diponible(activity)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setCancelable(false);
            builder.setMessage("Etes-vous sur de vouloir abandonner la commande ?")
                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mProgressDialog.dismiss();
                            dialog.cancel();
                            if(client!=null){
                                Intent intent = new Intent(activity, ActivityCommandeBarista.class);
                                intent.putExtra("user", new Gson().toJson(user));
                                if(client!=null){
                                    intent.putExtra("client", new Gson().toJson(client));
                                }
                                if(cadeau!=null){
                                    intent.putExtra("cadeau", new Gson().toJson(cadeau));
                                }
                                startActivity(intent);
                                overridePendingTransition(R.anim.pull_in, R.anim.push_out);
                                finish();
                            }else{
                                Intent intent =null;
                                if(cadeau!=null){
                                    intent = new Intent(activity, ActivityCommandeOfferte.class);
                                }else{
                                    if(client!=null){
                                        intent = new Intent(activity, ActivityCommandeBarista.class);
                                    }else{
                                        intent = new Intent(activity, ActivityAccueil.class);
                                    }
                                }
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

    private class Loading_ticket extends AsyncTask<Void, Void, Void> {

        Intent intent;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (Methodes.internet_diponible(activity)) {
                mProgressDialog.show();
                intent = new Intent(activity, ActivityPanier.class);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (Methodes.internet_diponible(activity)) {
                intent.putExtra("user", new Gson().toJson(user));
                intent.putExtra("mon_panier", new Gson().toJson(panier));
                if(client!=null){
                    intent.putExtra("client", new Gson().toJson(client));
                }
                if(cadeau!=null) {
                    intent.putExtra("cadeau", new Gson().toJson(cadeau));
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (Methodes.internet_diponible(activity)) {
                mProgressDialog.dismiss();
                startActivity(intent);
                overridePendingTransition(R.anim.pull_in, R.anim.push_out);
                finish();
            }

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
                TableProduit tableProduit = new TableProduit(activity.getBaseContext());
                list_produit_boisson = tableProduit.get_produit("boisson");
                list_produit_snack = tableProduit.get_produit("snack");
                list_produit_formule = tableProduit.get_formule();
                TableUser tableUser = new TableUser(activity.getBaseContext());
                user = tableUser.get_user(user.getNum_etu());


            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (Methodes.internet_diponible(activity)) {
                mProgressDialog.hide();
                load_navigation_boissons();
            }

        }

    }

    public void load_navigation_boissons() {

        if (list_produit_boisson.size() == 0) {
            list_boisson_empty = true;
            list_produit_boisson.add(new Produit("Aucun article"));
        }
        dataAdapter = new MyCustomAdapterProduit(this, R.layout.layout_produit, list_produit_boisson);
        listView.setAdapter(dataAdapter);
    }

    public void load_navigation_snacks() {
        if (list_produit_snack.size() == 0) {
            list_snack_empty = true;
            list_produit_snack.add(new Produit("Aucun article"));
        }
        dataAdapter = new MyCustomAdapterProduit(this, R.layout.layout_produit, list_produit_snack);
        listView.setAdapter(dataAdapter);
    }

    public void load_navigation_formules() {
        if (list_produit_formule.size() == 0) {
            list_produit_formule.add(new Produit("Aucune Formule"));
        }
        dataAdapter = new MyCustomAdapterProduit(this, R.layout.layout_produit, list_produit_formule);
        listView.setAdapter(dataAdapter);
    }


    private class MyCustomAdapterProduit extends ArrayAdapter<Produit> {

        private ArrayList<Produit> groupeList;


        public MyCustomAdapterProduit(Context context, int textViewResourceId,
                                      ArrayList<Produit> groupeList) {
            super(context, textViewResourceId, groupeList);
            this.groupeList = new ArrayList<>();
            this.groupeList.addAll(groupeList);
        }

        private class ViewHolder {
            ImageView image_view;
            TextView textView_info;
            FrameLayout frameLayout;
            TextView textView_prix;

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
                if (list_boisson_empty || list_snack_empty) {
                    holder.frameLayout.setVisibility(View.GONE);
                    holder.textView_info.setText(groupeList.get(position).getNom());
                    holder.textView_info.setGravity(Gravity.CENTER_HORIZONTAL);
                } else {
                    String prix = String.format(" %.2f", groupeList.get(position).getPrix_vente());
                    prix = prix.replace(",", "€");
                    prix = prix.replace(".", "€");
                    holder.textView_info.setText(groupeList.get(position).getNom());
                    holder.textView_prix.setText("" + prix);

                    int placement_tmp = panier.produit_present(groupeList.get(position).getNom());
                    if (placement_tmp != -1) {
                        holder.textView_prix.setText("Prix unitaire : " + prix + "\nNombre de produit dans le panier : " + panier.getList_article().get(placement_tmp).getQuantite());

                    } else {
                        holder.textView_prix.setText("Prix unitaire : " + prix);
                    }

                    try {
                        Picasso.with(activity).cancelRequest(holder.image_view);
                        Picasso.with(activity).load(groupeList.get(position).getImage()).centerCrop().fit().into(holder.image_view);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                        if (position_vue == 0) {
                            holder.image_view.setImageResource(R.drawable.icon_drink);
                        } else {
                            holder.image_view.setImageResource(R.drawable.icon_product);
                        }
                    }
                }

                convertViewProduit.setTag(holder);

            } else {
                holder = (ViewHolder) convertViewProduit.getTag();
            }


            //INITIALIZE
            if (list_boisson_empty || list_snack_empty) {
                holder.frameLayout.setVisibility(View.GONE);
                holder.textView_info.setText(groupeList.get(position).getNom());
                holder.textView_info.setGravity(Gravity.CENTER_HORIZONTAL);
            } else {
                try {
                    Picasso.with(activity).cancelRequest(holder.image_view);
                    Picasso.with(activity).load(groupeList.get(position).getImage()).centerCrop().fit().into(holder.image_view);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    if (position_vue == 0) {
                        holder.image_view.setImageResource(R.drawable.icon_drink);
                    } else {
                        holder.image_view.setImageResource(R.drawable.icon_product);
                    }
                }

                String prix = String.format(" %.2f", groupeList.get(position).getPrix_vente());
                prix = prix.replace(",", "€");
                prix = prix.replace(".", "€");
                holder.textView_info.setText(groupeList.get(position).getNom());

                int placement_tmp = panier.produit_present(groupeList.get(position).getNom());
                if (placement_tmp != -1) {
                    holder.textView_prix.setText("Prix unitaire : " + prix + "\nNombre de produit dans le panier : " + panier.getList_article().get(placement_tmp).getQuantite());
                } else {
                    holder.textView_prix.setText("Prix unitaire : " + prix);
                }


            }


            //LISTENER
            final ViewHolder final_holder = holder;
            convertViewProduit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(0 == groupeList.get(position).getStock()){
                        Methodes.info_dialog("Produit indisponible pour le moment",activity);
                    }else{
                        LayoutInflater factory = LayoutInflater.from(activity);

                        final View alertDialogView = factory.inflate(R.layout.dialog_commander_article, null);
                        AlertDialog.Builder adb = new AlertDialog.Builder(activity);

                        //GET INTERFACE
                        Button button_pos = (Button) alertDialogView.findViewById(R.id.button_pos);
                        Button button_neg = (Button) alertDialogView.findViewById(R.id.button_neg);
                        Button button_confirm = (Button) alertDialogView.findViewById(R.id.button_confirm);


                        textView_qte = (TextView) alertDialogView.findViewById(R.id.textView_qte);
                        textView_info = (TextView) alertDialogView.findViewById(R.id.textView_info);
                        textView_prix = (TextView) alertDialogView.findViewById(R.id.textView_prix);
                        button_close = (Button) alertDialogView.findViewById(R.id.button_close);

                        ImageView image_view_produit = (ImageView) alertDialogView.findViewById(R.id.image_view_produit);

                        //INITIALIZE
                        final int placement = panier.produit_present(groupeList.get(position).getNom());
                        if (placement != -1) {
                            qte_produit = panier.getList_article().get(placement).getQuantite();
                        } else {
                            qte_produit = 1;
                        }
                        qte_produit_old = qte_produit;

                        String prix;
                        textView_info.setText("" + groupeList.get(position).getNom());
                        prix = String.format(" %.2f", +groupeList.get(position).getPrix_vente());
                        prix = prix.replace(",", "€");
                        prix = prix.replace(".", "€");
                        final String prix_produit = prix;
                        textView_prix.setText("Prix unitaire : " + prix_produit + "\nQuantité : " + qte_produit);
                        textView_qte.setText("" + qte_produit);

                        try {
                            Picasso.with(activity).cancelRequest(image_view_produit);
                            Picasso.with(activity).load(groupeList.get(position).getImage()).centerCrop().fit().into(image_view_produit);
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                            if (position_vue == 0) {
                                image_view_produit.setImageResource(R.drawable.icon_drink);
                            } else {
                                image_view_produit.setImageResource(R.drawable.icon_product);
                            }
                        }
                        adb.setView(alertDialogView);
                        final AlertDialog alertDialog = adb.show();
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

                        //LISTENER
                        button_pos.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                //TODO test quantite dispo
                                //TODO MAJ QUANTITE PANIER
                                if(qte_produit == groupeList.get(position).getStock()){
                                    Methodes.info_dialog("Vous êtes arrivé à la limite des stocks du produit",activity);
                                }else{
                                    qte_produit++;
                                    textView_prix.setText("Prix unitaire : " + prix_produit + "\nQuantité : " + qte_produit);
                                    textView_qte.setText("" + qte_produit);
                                }


                            }
                        });
                        button_neg.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                if (qte_produit > 1) {
                                    qte_produit--;
                                    textView_prix.setText("Prix unitaire : " + prix_produit + "\nQuantité : " + qte_produit);
                                    textView_qte.setText("" + qte_produit);
                                } else {
                                    if (placement != -1) {//maj de l'article
                                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                        builder.setCancelable(false);
                                        builder.setMessage("Voulez-vous retirer le produit du panier ?")
                                                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                        alertDialog.dismiss();
                                                        qte_produit--;
                                                        if (placement != -1) {//maj de l'article
                                                            panier.set_quantite_article(placement, qte_produit, qte_produit_old);
                                                        }


                                                        String prix_total = String.format(" %.2f", +panier.getPrix_total());
                                                        prix_total = prix_total.replace(",", "€");
                                                        prix_total = prix_total.replace(".", "€");
                                                        toolbar_layout.setTitle("Total panier :" + prix_total);
                                                        final_holder.textView_prix.setText("Prix unitaire : " + prix_produit);

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
                            }
                        });

                        button_confirm.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                //TODO SI DEJA PRESENT
                                if (placement != -1) {//maj de l'article
                                    panier.set_quantite_article(placement, qte_produit, qte_produit_old);
                                } else {
                                    panier.add_article(new Article(groupeList.get(position), qte_produit));
                                }

                                alertDialog.dismiss();
                                String prix_total = String.format(" %.2f", +panier.getPrix_total());
                                prix_total = prix_total.replace(",", "€");
                                prix_total = prix_total.replace(".", "€");
                                toolbar_layout.setTitle("Total panier :" + prix_total);

                                String prix = String.format(" %.2f", +groupeList.get(position).getPrix_vente());
                                prix = prix.replace(",", "€");
                                prix = prix.replace(".", "€");

                                int placement_tmp = panier.produit_present(groupeList.get(position).getNom());
                                if (placement_tmp != -1) {
                                    final_holder.textView_prix.setText("Prix unitaire : " + prix + "\nNombre de produit dans le panier : " + panier.getList_article().get(placement_tmp).getQuantite());
                                } else {
                                    final_holder.textView_prix.setText("Prix unitaire : " + prix);
                                }



                            }
                        });

                        button_close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });
                    }



                }
            });



            return convertViewProduit ;
        }
    }
}
