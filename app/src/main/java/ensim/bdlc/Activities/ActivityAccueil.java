package ensim.bdlc.Activities;

import android.animation.Animator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.HashMap;

import ensim.bdlc.BuildConfig;
import ensim.bdlc.R;
import ensim.bdlc.database.TableNews;
import ensim.bdlc.database.TablePanier;
import ensim.bdlc.database.TableUser;
import ensim.bdlc.modele.News;
import ensim.bdlc.modele.Panier;
import ensim.bdlc.modele.User;
import ensim.bdlc.utils.Methodes;

public class ActivityAccueil extends AppCompatActivity
        implements AppBarLayout.OnOffsetChangedListener {

    /**
     * ATTRIBUTES
     */
    //INTERFACE
    private FloatingActionButton fab_menu, fab1, fab2, fab3, fab4, fab5,fab6, fab7;
    private LinearLayout fabLayout1, fabLayout2, fabLayout3, fabLayout4, fabLayout5,fabLayout6,fabLayout7;

    private ProgressDialog mProgressDialog;
    private ListView listView_menu;
    private View fabBGLayout;
    private TextView textView_info_dialog, textView_link,textTitre_title_news,textView_date_news;
    private ImageView imageView_news_dialog;

    //ARRAYLIST
    private HashMap<String, String> map;
    private ArrayList<News> list_news;
    private ArrayList<HashMap<String, String>> listItem = new ArrayList<>();
    private Activity activity;
    private User user;
    private News news;
    private MyCustomAdapterNews dataAdapterNews;
    //OTHER
    boolean isFABOpen = false;
    private boolean state_retour;

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS     = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION              = 200;

    private boolean mIsTheTitleVisible          = false;
    private boolean mIsTheTitleContainerVisible = true;

    private LinearLayout mTitleContainer;
    private TextView mTitle;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private boolean show_dialog_news = false;
    private AlertDialog alertDialog_main;

    private boolean hard_update = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        bindActivity();

        mAppBarLayout.addOnOffsetChangedListener(this);

        mToolbar.inflateMenu(R.menu.menu_a_propos);
        startAlphaAnimation(mTitle, 0, View.INVISIBLE);

        listView_menu = (ListView) findViewById(R.id.listView_menu);

        fabLayout1 = (LinearLayout) findViewById(R.id.fabLayout1);
        fabLayout2 = (LinearLayout) findViewById(R.id.fabLayout2);
        fabLayout3 = (LinearLayout) findViewById(R.id.fabLayout3);
        fabLayout4 = (LinearLayout) findViewById(R.id.fabLayout4);
        fabLayout5 = (LinearLayout) findViewById(R.id.fabLayout5);
        fabLayout6 = (LinearLayout) findViewById(R.id.fabLayout6);
        fabLayout7 = (LinearLayout) findViewById(R.id.fabLayout7);
        fab_menu = (FloatingActionButton) findViewById(R.id.fab_menu);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab4 = (FloatingActionButton) findViewById(R.id.fab4);
        fab5 = (FloatingActionButton) findViewById(R.id.fab5);
        fab6 = (FloatingActionButton) findViewById(R.id.fab6);
        fab7 = (FloatingActionButton) findViewById(R.id.fab7);
        fabBGLayout = findViewById(R.id.fabBGLayout);
        //INITIALIZE
        activity = this;
        state_retour= false;
        mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setTitle("Veuillez patienter");
        mProgressDialog.setMessage("Connexion en cours...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(false);
        //listView_menu.setNestedScrollingEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.getString("user") != null) {
                user = new Gson().fromJson(extras.getString("user"), User.class);
            }
        }
        list_news = new ArrayList<>();
        //Génération de la listview

        new Loading().execute();





        fabLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retour();
            }
        });
        fabLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.cancel();
                if (Methodes.internet_diponible(activity)) {

                    Intent intent = new Intent(activity, ActivityCommande.class);
                    intent.putExtra("user", new Gson().toJson(user));
                    startActivity(intent);
                    overridePendingTransition(R.anim.pull_in, R.anim.push_out);
                    finish();
                }

            }
        });
        fabLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Methodes.internet_diponible(activity)) {

                    Intent intent = new Intent(activity, ActivityCommandeOfferte.class);
                    intent.putExtra("user", new Gson().toJson(user));
                    startActivity(intent);
                    overridePendingTransition(R.anim.pull_in, R.anim.push_out);
                    finish();


                }


            }
        });

        fabLayout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Methodes.internet_diponible(activity)) {

                    Intent intent = new Intent(activity, ActivityProfil.class);
                    intent.putExtra("user", new Gson().toJson(user));
                    startActivity(intent);
                    overridePendingTransition(R.anim.pull_in, R.anim.push_out);
                    finish();


                }
            }
        });

        fabLayout5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Methodes.internet_diponible(activity)) {

                    Intent intent = new Intent(activity, ActivityCommandeBarista.class);
                    intent.putExtra("user", new Gson().toJson(user));
                    startActivity(intent);
                    overridePendingTransition(R.anim.pull_in, R.anim.push_out);
                    finish();


                }


            }
        });
        fabLayout6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Methodes.internet_diponible(activity)) {
                    IntentIntegrator integrator = new IntentIntegrator(activity);
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                    integrator.setPrompt("Veuillez scanner la commande");
                    integrator.setCameraId(0);
                    integrator.setBeepEnabled(false);
                    integrator.setBarcodeImageEnabled(false);
                    integrator.initiateScan();
                }

            }
        });

        fabLayout7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Methodes.internet_diponible(activity)) {
                    mProgressDialog.cancel();
                    Intent intent = new Intent(activity, ActivityChargeSolde.class);
                    intent.putExtra("user", new Gson().toJson(user));
                    startActivity(intent);
                    overridePendingTransition(R.anim.pull_in, R.anim.push_out);
                    finish();
                }

            }
        });

        fab_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Methodes.internet_diponible(activity)) {
                    if (!isFABOpen) {
                        showFABMenu();
                    } else {
                        closeFABMenu();
                    }
                }

            }
        });

        fabBGLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Methodes.internet_diponible(activity)) {
                    closeFABMenu();
                }

            }
        });

        //LISTENER sur le menu haut/droit
        mToolbar.setOnMenuItemClickListener(new android.support.v7.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_a_propos:

                        LayoutInflater factory = LayoutInflater.from(activity);
                        final View alertDialogView = factory.inflate(R.layout.dialog_a_propos, null);
                        AlertDialog.Builder adb = new AlertDialog.Builder(activity);

                        //GET INTERFACE
                        Button button_close = (Button) alertDialogView.findViewById(R.id.button_close);
                        TextView textView_ensim = (TextView)alertDialogView.findViewById(R.id.textView_ensim);
                        TextView textView_bdlc = (TextView)alertDialogView.findViewById(R.id.textView_bdlc);
                        TextView textView_site_bdlc = (TextView)alertDialogView.findViewById(R.id.textView_site_bdlc);



                        adb.setView(alertDialogView);
                        final AlertDialog alertDialog = adb.show();
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

                        button_close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });
                        textView_ensim.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                                Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://ensim.univ-lemans.fr/fr/index.html"));
                                startActivity(intent);
                            }
                        });
                        textView_bdlc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                                Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.facebook.com/groups/ensim.bdlc/"));
                                startActivity(intent);
                            }
                        });

                        textView_site_bdlc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                                Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://ensim-associations.univ-lemans.fr/fr/index.html"));
                                startActivity(intent);
                            }
                        });


                        return true;


                    default:
                        return false;
                }
            }
        });


    }

    private void bindActivity() {
        mToolbar        = (Toolbar) findViewById(R.id.main_toolbar);
        mTitle          = (TextView) findViewById(R.id.main_textview_title);
        mTitleContainer = (LinearLayout) findViewById(R.id.main_linearlayout_title);
        mAppBarLayout   = (AppBarLayout) findViewById(R.id.main_appbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_a_propos, menu);
        return true;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if(!mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if(mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation (View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    @Override
    public void onBackPressed() {

        if (isFABOpen) {
            closeFABMenu();
        } else {
            if(state_retour==true){
                state_retour=false;
            }else{
                retour();
            }



        }


    }

    public void retour() {
        if (Methodes.internet_diponible(activity)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setCancelable(false);
            builder.setMessage("Vous allez être déconnecté. Voulez-vous continuer ?")
                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            mProgressDialog.cancel();
                            mProgressDialog.dismiss();
                            startActivity(new Intent(activity, ActivityLogin.class));
                            overridePendingTransition(R.anim.pull_in_return, R.anim.push_out_return);
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

    private void showFABMenu() {
        isFABOpen = true;
        fabLayout1.setVisibility(View.VISIBLE);
        fabLayout2.setVisibility(View.VISIBLE);
        fabLayout3.setVisibility(View.VISIBLE);
        fabLayout4.setVisibility(View.VISIBLE);

        if (user.getCategorie().equals("barista")) {

            fabLayout5.setVisibility(View.VISIBLE);
            fabLayout6.setVisibility(View.VISIBLE);
        } else {
            if (user.getCategorie().equals("admin")) {
                fabLayout5.setVisibility(View.VISIBLE);
                fabLayout6.setVisibility(View.VISIBLE);
                fabLayout7.setVisibility(View.VISIBLE);

            } else {

            }
        }


        fabBGLayout.setVisibility(View.VISIBLE);

        fab_menu.animate().rotationBy(180);
        fabLayout1.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        fabLayout2.animate().translationY(-getResources().getDimension(R.dimen.standard_205));
        fabLayout3.animate().translationY(-getResources().getDimension(R.dimen.standard_305));
        fabLayout4.animate().translationY(-getResources().getDimension(R.dimen.standard_405));
        fabLayout5.animate().translationY(-getResources().getDimension(R.dimen.standard_505));
        fabLayout6.animate().translationY(-getResources().getDimension(R.dimen.standard_605));
        fabLayout7.animate().translationY(-getResources().getDimension(R.dimen.standard_705));
    }

    private void closeFABMenu() {
        isFABOpen = false;
        fabBGLayout.setVisibility(View.GONE);
        fab_menu.animate().rotationBy(-180);
        fabLayout1.animate().translationY(0);
        fabLayout2.animate().translationY(0);
        fabLayout3.animate().translationY(0);
        fabLayout4.animate().translationY(0);
        fabLayout5.animate().translationY(0);
        fabLayout6.animate().translationY(0);
        fabLayout7.animate().translationY(0);
        fab_menu.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFABOpen) {
                    fabLayout1.setVisibility(View.GONE);
                    fabLayout2.setVisibility(View.GONE);
                    fabLayout3.setVisibility(View.GONE);
                    fabLayout4.setVisibility(View.GONE);
                    fabLayout5.setVisibility(View.GONE);
                    fabLayout6.setVisibility(View.GONE);
                    fabLayout7.setVisibility(View.GONE);
                }

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        state_retour = true;
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (Methodes.internet_diponible(activity)) {
            if (result != null) {
                if (result.getContents() == null) {
                    Methodes.info_dialog("Aucun QRCode détecté", activity);
                    retour();
                } else {
                    Toast.makeText(activity, "result.getContents()=" + result.getContents(), Toast.LENGTH_SHORT).show();
                    TablePanier tablePanier = new TablePanier(activity.getBaseContext());
                    Panier panier_tmp = tablePanier.get_panier(result.getContents());
                    if (panier_tmp != null) {
                        if(panier_tmp.getId_owner().equals(user.getNum_etu())){
                            Methodes.info_dialog("Vous ne pouvez scanner votre propre commande", activity);
                        }else{
                            if (!panier_tmp.getState().equals("finie")) {
                                Intent intent = new Intent(activity, ActivityRecapCommande.class);
                                intent.putExtra("user", new Gson().toJson(user));
                                intent.putExtra("panier", new Gson().toJson(panier_tmp));
                                activity.startActivity(intent);
                                activity.overridePendingTransition(R.anim.pull_in, R.anim.push_out);
                                activity.finish();
                            } else {

                                Methodes.info_dialog("Cette commande a déjà été donné", activity);

                            }
                        }

                    } else {
                        Methodes.info_dialog("Aucune commande n'a été trouvée", activity);
                    }


                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
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

                TableNews tableNews = new TableNews(activity);

                list_news = tableNews.get_list_news();

                int versionCode = BuildConfig.VERSION_CODE;

                if(user.getVersion() < versionCode && user.getVersion()!=00){
                    TableUser tableUser = new TableUser(activity);
                    user.setVersion(versionCode);
                    tableUser.changer_version(user);
                }else{
                    if(user.getVersion() == 0){
                        hard_update = true;
                    }
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (Methodes.internet_diponible(activity)) {
                mProgressDialog.hide();
                mProgressDialog.dismiss();
                mProgressDialog.cancel();

                if(hard_update){
                    if (Methodes.internet_diponible(activity)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setCancelable(false);
                        builder.setMessage("Vous devez mettre à jour l'application pour continuer à l'utiliser")
                                .setPositiveButton("D'accord", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        mProgressDialog.cancel();
                                        mProgressDialog.dismiss();
                                        startActivity(new Intent(activity, ActivityLogin.class));
                                        overridePendingTransition(R.anim.pull_in_return, R.anim.push_out_return);
                                        finish();
                                    }

                                });
                        builder.create();
                        builder.show();
                    }
                }else{
                    dataAdapterNews = new MyCustomAdapterNews(activity, R.layout.layout_news, list_news);
                    listView_menu.setAdapter(dataAdapterNews);



                }
            }

        }

    }


    private class MyCustomAdapterNews extends ArrayAdapter<News> {

        private ArrayList<News> groupeList;


        public MyCustomAdapterNews(Context context, int textViewResourceId,
                                   ArrayList<News> groupeList) {
            super(context, textViewResourceId, groupeList);
            this.groupeList = new ArrayList<>();
            this.groupeList.addAll(groupeList);
        }

        private class ViewHolder {
            ImageView imageView_news;

        }

        @Override
        public View getView(final int position, View convertViewProduit, ViewGroup parent) {

            ViewHolder holder;

            if (convertViewProduit == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertViewProduit = vi.inflate(R.layout.layout_news, null);

                //GET INTERFACE
                holder = new ViewHolder();

                holder.imageView_news = (ImageView) convertViewProduit.findViewById(R.id.imageView_news);

                //INITIALIZE
                holder.imageView_news.setImageBitmap(groupeList.get(position).getPicture());
                convertViewProduit.setTag(holder);

            } else {
                holder = (ViewHolder) convertViewProduit.getTag();
            }

            //INITIALIZE
            holder.imageView_news.setImageBitmap(groupeList.get(position).getPicture());


            //LISTENER
            convertViewProduit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {


                    LayoutInflater factory = LayoutInflater.from(activity);

                    final View alertDialogView = factory.inflate(R.layout.dialog_visualiser_news, null);
                    AlertDialog.Builder adb = new AlertDialog.Builder(activity);

                    //adb.setCancelable(false);

                    //GET INTERFACE
                    imageView_news_dialog = (ImageView) alertDialogView.findViewById(R.id.imageView_news_dialog);
                    textView_info_dialog = (TextView) alertDialogView.findViewById(R.id.textView_info_dialog);
                    textView_link = (TextView) alertDialogView.findViewById(R.id.textView_link);
                    textTitre_title_news = (TextView) alertDialogView.findViewById(R.id.textTitre_title_news);
                    textView_date_news = (TextView) alertDialogView.findViewById(R.id.textView_date_news);

                    Button button_close = (Button) alertDialogView.findViewById(R.id.button_close);

                    //INITIALIZE
                    imageView_news_dialog.setImageBitmap(groupeList.get(position).getPicture());
                    textView_info_dialog.setText(groupeList.get(position).getDetails());
                    textTitre_title_news.setText(groupeList.get(position).getTitle());

                    if (groupeList.get(position).getLink().equals("")) {
                        textView_link.setVisibility(View.GONE);
                    }
                    if (groupeList.get(position).getDate().equals("aucune")) {
                        textView_date_news.setVisibility(View.GONE);
                    }else{
                        textView_date_news.setText(groupeList.get(position).getDate());
                    }


                    adb.setView(alertDialogView);
                    final AlertDialog alertDialog = adb.show();
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    alertDialog_main = alertDialog;
                    show_dialog_news = true;

                    button_close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                            show_dialog_news  = false;
                        }
                    });

                    textView_link.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(groupeList.get(position).getLink()));
                            startActivity(intent);
                        }
                    });


                }
            });


            return convertViewProduit;
        }


    }


}
