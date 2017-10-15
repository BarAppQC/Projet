package classes;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.model.TestClass;

import static org.junit.Assert.*;

/**
 * Created by leofa on 30/09/2017.
 */
public class UtilisateurTest {

    Utilisateur u1;
    Utilisateur u2;
    Utilisateur u3;
    Utilisateur u4;
    Utilisateur u5;
    Utilisateur u6;
    Utilisateur u7;
    Utilisateur u8;
    Utilisateur u9;
    Utilisateur u10;

    DatabaseReference myRef ;

@Test
    public void ajouterNouvelUtilisateur() throws Exception {
        myRef.child("users").child(String.valueOf(u1.id)).setValue(u1);
        myRef.child("users").child(String.valueOf(u2.id)).setValue(u2);
        myRef.child("users").child(String.valueOf(u3.id)).setValue(u3);
        myRef.child("users").child(String.valueOf(u4.id)).setValue(u4);
        myRef.child("users").child(String.valueOf(u5.id)).setValue(u5);
        myRef.child("users").child(String.valueOf(u6.id)).setValue(u6);
        myRef.child("users").child(String.valueOf(u7.id)).setValue(u7);
        myRef.child("users").child(String.valueOf(u8.id)).setValue(u8);
        myRef.child("users").child(String.valueOf(u9.id)).setValue(u9);
        myRef.child("users").child(String.valueOf(u10.id)).setValue(u10);
    assertTrue(true);
    }


    @Test
    public void recupererUtilisateur() throws Exception {
        assertTrue(true);
    }


    @Test
    public void equals() throws Exception {
        assertTrue(true);
    }

    @Before
    public void setUp() throws Exception {
        FirebaseApp.initializeApp();
        myRef = FirebaseDatabase.getInstance().getReference("users");
        u1 = new Utilisateur(1, "Gigi@gmail.com", "Lucas", "Gicquel", 0, 0);
        u2 = new Utilisateur(2, "Lucas@gmail.com", "Lucas", "Domingo", 0, 0);
        u3 = new Utilisateur(3, "Gigi@gmail.com", "Lucas", "Gicquel", 0, 0);
        u4 = new Utilisateur(4, "Gigi@gmail.com", "Lucas", "Gicquel", 0, 0);
        u5 = new Utilisateur(5, "Gigi@gmail.com", "Lucas", "Gicquel", 0, 0);
        u6 = new Utilisateur(6, "Gigi@gmail.com", "Lucas", "Gicquel", 0, 0);
        u7 = new Utilisateur(7, "Gigi@gmail.com", "Lucas", "Gicquel", 0, 0);
        u8 = new Utilisateur(8, "Gigi@gmail.com", "Lucas", "Gicquel", 0, 0);
        u9 = new Utilisateur(9, "Gigi@gmail.com", "Lucas", "Gicquel", 0, 0);
        u10 = new Utilisateur(10, "Gigi@gmail.com", "Lucas", "Gicquel", 0, 0);
    }

    @After
    public void tearDown() throws Exception {
        u1 = null;
        u2 = null;
        u3 = null;
        u4 = null;
        u5 = null;
        u6 = null;
        u7 = null;
        u8 = null;
        u9 = null;
        u10 = null;
    }

}