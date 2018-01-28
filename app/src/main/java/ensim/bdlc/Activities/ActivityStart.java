package ensim.bdlc.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ensim.bdlc.utils.Methodes;
import ensim.bdlc.R;

public class ActivityStart extends AppCompatActivity {

    /**
     * ATTRIBUTES
     */
    //OTHER
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //GET INTERFACE
        setContentView(R.layout.activity_start);

        //INITIALIZE
        activity = this;


        //LOADING
        if (Methodes.internet_diponible_activity_start(this)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(activity, ActivityLogin.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.pull_in, R.anim.push_out);
                    finish();
                }
            }, 1000);
        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setCancelable(false);
            builder.setMessage("L'application a besoin d'internet pour fonctionner\nVeuillez l'activer.")
                    .setPositiveButton("Fermer", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                                Intent intent = new Intent(activity, ActivityStart.class);
                                startActivity(intent);
                                finish();

                        }
                    });
            builder.create();
            builder.show();
        }
    }
}
