package com.example.lucas.barapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

import classes.Utilisateur;


public class MainActivity extends AppCompatActivity {

    // View des contenus particuliers selon l'onglet de menu sélectionné
    private View view_boisson;
    private View view_accueil;
    private View view_paiement;
    private View view_team;

    // Layout principal de l'application, elle y accueillera les vues
    private FrameLayout layout_main;

    // Pour se connecter à la base de données
    FirebaseDatabase database ;
    DatabaseReference myRef ;

    // Appelé en cas de clic sur le menu
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // Selon sur quel onglet on a cliqué, on fait apparaitre les contenus
            switch (item.getItemId()) {
                case R.id.navigation_accueil:
                    view_boisson.setVisibility(View.GONE);
                    view_paiement.setVisibility(View.GONE);
                    view_team.setVisibility(View.GONE);

                    // je me dis que c'est mieux de mettre le VISIBLE en dernier
                    // comme ça aucun risque de chevauchement
                    view_accueil.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_boisson:
                    view_paiement.setVisibility(View.GONE);
                    view_team.setVisibility(View.GONE);
                    view_accueil.setVisibility(View.GONE);
                    view_boisson.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_paiement:
                    view_boisson.setVisibility(View.GONE);
                    view_team.setVisibility(View.GONE);
                    view_accueil.setVisibility(View.GONE);
                    view_paiement.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_team:
                    view_boisson.setVisibility(View.GONE);
                    view_paiement.setVisibility(View.GONE);
                    view_accueil.setVisibility(View.GONE);
                    view_team.setVisibility(View.VISIBLE);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initFirebase();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Déclaration des vues et du layout
        layout_main = (FrameLayout) findViewById(R.id.layout_main);
        view_boisson = getLayoutInflater().inflate(R.layout.boisson, null);
        view_accueil = getLayoutInflater().inflate(R.layout.accueil, null);
        view_paiement = getLayoutInflater().inflate(R.layout.paiement, null);
        view_team = getLayoutInflater().inflate(R.layout.team, null);

        // Ajouts des view au layout
        addView(view_boisson);
        addView(view_accueil);
        addView(view_paiement);
        addView(view_team);


        // On affiche seulement l'accueil en premier
        view_boisson.setVisibility(View.GONE);
        view_accueil.setVisibility(View.VISIBLE);
        view_paiement.setVisibility(View.GONE);
        view_team.setVisibility(View.GONE);
    }

    public void InsertUtilisateur( Utilisateur u) throws Exception {
        myRef.child("users").child(String.valueOf(u.id)).setValue(u);
    }

    public void initFirebase(){
        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
    }

    /**
     * Ajoute la vue passée en paramètre sur le layout principal :
     * le FrameLayout layout_main
     * @param aAjouter : la vue à ajouter à l'activité
     */
    public void addView(View aAjouter) {
        aAjouter.setPadding(10, 140, 10, 90);
        layout_main.addView(aAjouter);
    }
}
