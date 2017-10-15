package com.example.lucas.barapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

import classes.Utilisateur;


public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private Button button2;

    FirebaseDatabase database ;
    DatabaseReference myRef ;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_accueil:
                    mTextMessage.setText(R.string.title_accueil);
                    return true;
                case R.id.navigation_boisson:
                    mTextMessage.setText(R.string.title_boisson);
                    return true;
                case R.id.navigation_paiement:
                    mTextMessage.setText(R.string.title_paiement);
                    return true;
                case R.id.navigation_team:
                    mTextMessage.setText(R.string.title_team);
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
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        button2 = (Button)findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                   // InsertUtilisateur();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void InsertUtilisateur( Utilisateur u) throws Exception {
        myRef.child("users").child(String.valueOf(u.id)).setValue(u);
    }

    public void initFirebase(){
        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
    }
}
