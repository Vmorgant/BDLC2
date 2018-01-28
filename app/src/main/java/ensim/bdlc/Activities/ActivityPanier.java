package ensim.bdlc.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import ensim.bdlc.R;
import ensim.bdlc.modele.Article;
import ensim.bdlc.modele.Panier;
import ensim.bdlc.modele.Produit;
import ensim.bdlc.modele.User;
import ensim.bdlc.utils.Methodes;

public class ActivityPanier extends AppCompatActivity {

    /**
     * ATTRIBUTES
     */
    //INTERFACE
    private Toolbar toolbar;
    private ListView listView_produit;
    private FloatingActionButton fab_confirm;
    private LinearLayout linearLayout_scroll;
    private CollapsingToolbarLayout toolbar_layout;
    //BDD
    //ARRAYLIST
    //OTHER
    private Activity activity;
    private Panier panier,panier_old;
    private MyCustomAdapterProduit dataAdapter = null;
    private User user,client,cadeau;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //GET INTERFACE
        setContentView(R.layout.activity_panier);
        listView_produit = (ListView) findViewById(R.id.listView_produit);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_layout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        fab_confirm = (FloatingActionButton) findViewById(R.id.fab_confirm);
        linearLayout_scroll = (LinearLayout) findViewById(R.id.linearLayout_scroll);


        //INITIALIZE
        activity = this;
     //   linearLayout_scroll.setNestedScrollingEnabled(true);
       // listView_produit.setNestedScrollingEnabled(true);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.getString("mon_panier") != null) {
                panier = new Gson().fromJson(extras.getString("mon_panier"), Panier.class);
                panier_old = panier;
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


        String prix_panier = String.format(" %.2f", +panier.getPrix_total());
        prix_panier = prix_panier.replace(",", "€");
        prix_panier = prix_panier.replace(".", "€");
        toolbar_layout.setTitle("Total panier :" + prix_panier);

        ArrayList<Article> list_tmp = new ArrayList<>();
        list_tmp.addAll(panier.getList_article());
        for (int i = 0; i < panier.getList_formule().size(); i++) {
            Article article = new Article(
                    new Produit(panier.getList_formule().get(i).getNom(),
                            panier.getList_formule().get(i).getPrix_vente(), "icon_formule"),
                    panier.getList_formule().get(i).getQuantite());
            list_tmp.add(article);

        }


        dataAdapter = new MyCustomAdapterProduit(this, R.layout.layout_produit, list_tmp);
        listView_produit.setAdapter(dataAdapter);


        //LISTENER
        fab_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Methodes.internet_diponible(activity)) {
                    Intent intent = new Intent(activity, ActivityQrcodeCommande.class);
                    intent.putExtra("mon_panier", new Gson().toJson(panier_old));
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
            Intent intent = new Intent(activity, ActivityCommande.class);
            intent.putExtra("mon_panier", new Gson().toJson(panier));
            intent.putExtra("user", new Gson().toJson(user));
            if(client!=null){
                intent.putExtra("client", new Gson().toJson(client));
            }
            if(cadeau!=null){
                intent.putExtra("cadeau", new Gson().toJson(cadeau));
            }
            startActivity(intent);
            overridePendingTransition(R.anim.pull_in_return, R.anim.push_out_return);
            finish();
        }

    }


    private class MyCustomAdapterProduit extends ArrayAdapter<Article> {

        private ArrayList<Article> groupeList;

        public MyCustomAdapterProduit(Context context, int textViewResourceId,
                                      ArrayList<Article> groupeList) {
            super(context, textViewResourceId, groupeList);
            this.groupeList = new ArrayList<>();
            this.groupeList.addAll(groupeList);
        }

        private class ViewHolder {
            ImageView image_view;
            TextView textView_info,textView_prix;
            FrameLayout frameLayout;
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

                String prix = String.format(" %.2f", groupeList.get(position).getProduit().getPrix_vente());
                prix = prix.replace(",", "€");
                prix = prix.replace(".", "€");
                holder.textView_info.setText(groupeList.get(position).getProduit().getNom());
                holder.textView_prix.setText("Prix unitaire : " + prix + "\nNombre de produit dans le panier : " + panier.getList_article().get(position).getQuantite());
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
            } else {
                holder = (ViewHolder) convertViewProduit.getTag();
            }

            //INITIALIZE
            String prix = String.format(" %.2f", groupeList.get(position).getProduit().getPrix_vente());
            prix = prix.replace(",", "€");
            prix = prix.replace(".", "€");
            holder.textView_info.setText(groupeList.get(position).getProduit().getNom());
            holder.textView_prix.setText("Prix unitaire : " + prix + "\nNombre de produit dans le panier : " + panier.getList_article().get(position).getQuantite());
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


            //LISTENER
            convertViewProduit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //TODO DELETE PRODUCT ???
                }
            });

            return convertViewProduit;
        }
    }
}
