package com.example.lucas.barapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

/**
 * Activité principale de l'application
 */
public class MainActivity extends AppCompatActivity {

    /**   ------------ Composants ------------ */
    /* Texte du menu en bas */
    private TextView textMessage;

    /* Nom d'utilisateur */
    private TextView textNomUtilisateur;

    /** ------------ Fonctions ------------ */
    /* Affecte les textes du menu au bouton en bas */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            // Affichage des bonnes infos en fonction du menu sélectionné
            switch (item.getItemId()) {

                case R.id.navigation_accueil:
                    textMessage.setText(R.string.title_accueil);
                    return true;
                case R.id.navigation_paiement:
                    textMessage.setText(R.string.title_paiement);
                    return true;
                case R.id.navigation_boisson:
                    textMessage.setText(R.string.title_boisson);
                    return true;
                case R.id.navigation_team:
                    textMessage.setText(R.string.title_team);
                    return true;
            }
            return false;
        }

    };

    /**
     * Appelée à la création de l'application
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Texte affiché juste pour dire dans quel menu on est
        // TODO text à supprimer
        textMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Login utilisateur récupéré grâce à la connexin Facebook
        textNomUtilisateur = (TextView) findViewById(R.id.nomUtilisateur);
        textNomUtilisateur.setText("Lucas");
    }

}
