package com.example.lucas.barappmachine;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import classes.Boisson;
import classes.BoissonQuantite;
import classes.Cocktail;
import classes.Commande;
import classes.MifareUltralightTagTester;
import classes.Utilisateur;

import static android.graphics.Typeface.BOLD;

/**
 * Activité appelée dès le lancement de l'application
 * Elle sera utilisée côté machine (ou serveur) pour donner des boissons
 * à l'utilisateur qui souhaite se servir
 */
public class MainActivity extends AppCompatActivity {


    //region VARIABLES

    private static final String TAG = MifareUltralightTagTester.class.getSimpleName();

    boolean affichage_boisson = true;

    //region FIREBASE
    /**
     * La base de données où sont stockées les boissons, cocktails et l'utilisateur
     */
    FirebaseDatabase database;

    /**
     * La référence à la base de données
     */
    DatabaseReference myRef;

    /**
     * La référence au storage de Firebase où sont stockées les images de boissons/cocktails
     */
    private StorageReference mStorageRef;

    /**
     * Le storage où sont stockées les images
     */
    FirebaseStorage storage = FirebaseStorage.getInstance();
    //endregion FIREBASE

    //region BOISSON
    /**
     * Fichier lcoal permettant de stocker temporairement une image du storage à la fois
     */
    File localFile;

    /**
     * Permet l'accès aux images de boissons qui se trouvent dans le storage
     */
    StorageReference boissonRef;

    /**
     * Stocke toutes les boissons stockées dans la base de données
     * On stocke toute la base de données afin de fluidifier les traitements
     */
    ArrayList<Boisson> data_boissons = new ArrayList<Boisson>();

    /**
     * Stocke toutes les images récupérées du storage
     */
    Drawable[] images;

    /**
     * L'image à afficher dans la vignette (elle va subir des traitements au niveau de la taille)
     */
    Drawable imageVignette;

    /**
     * Permet d'afficher les boissons
     */
    Button btn_boissons;

    /**
     * Le filtre qui est appliqué sur les boissons ou cocktails
     */
    String leFiltre;

    /**
     * La taille de verre sélectionnée par le client en centilitre
     */
    int verre;

    /**
     * La taille du verre par défaut (25 cl)
     */
    final int VERRE_DEFAUT = 25;

    /**
     * Le bouton permettant d'ajouter toute la sélection de l'utilisateur
     */
    Button btn_ajouter;

    /**
     * Permet de mettre à zéro la sélection de l'utilisateur (cocktail et quantités)
     */
    Button btn_reset;

    /**
     * Permet de connaitre le nombre de boisson dans toute l'application
     */
    int nb_boissons;

    /**
     * Indique la quantité totale de boissons commandées
     */
    int quantite_boissons = 0;

    /**
     * Les boutons définissant la taille du verre (4cl, 15cl, etc.)
     */
    Button verre_4;
    Button verre_15;
    Button verre_25;
    Button verre_50;
    Button verre_100;

    Button btn_filtre_alcool_fort;
    Button btn_filtre_alcool_doux;
    Button btn_filtre_soft;

    /**
     * Afin de récupérer le style de bouton "normal" après modification
     */
    Drawable style_button_defaut;

    /**
     * La zone de texte disant quel verre est sélectionné
     */
    TextView verre_selectionne;

    int quantite_start;
    int quantite_stop;

    ArrayList<Integer> id_affiche;

    //endregion BOISSON

    //region COCKTAIL
    /**
     * L'objet cocktail qui est une liste de boissons et de quantités associées
     */
    Cocktail cocktail = new Cocktail();

    /**
     * Permet d'afficher les cocktails plutôt que les boissons
     */
    Button btn_cocktails;

    int cocktail_selectionne;

    ArrayList<Commande> data_commandes  = new ArrayList<Commande>();;

    Drawable[] images_commandes;

    String leFiltreCocktail;

    Button btn_filtre_commun;
    Button btn_filtre_perso;
    Button btn_filtre_autre;
    //endregion COCKTAIL

    //region COMMANDES

    /**
     * Permet de passer la commande des boissons sélectionnées
     */
    Button btn_commande;

    /**
     * Le nouvel identifiant que va prendre la prochaine commande
     * Il sera mis à jour après commande de l'utilisateur
     */
    int id;

    int id_commande;

    //endregion COMMANDES

    //region UTILISATEUR

    /**
     * Représente le client connecté depuis son appareil mobile sur le distributeur
     */
    Utilisateur utilisateur_connecte;

    //endregion UTILISATEUR

    //endregion VARIABLES

    //region DONNEES

    //region FIREBASE

    /**
     * Initialise la base de données Firebase
     */
    public void initFirebase(){
        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
    }

    //endregion FIREBASE

    //region BOISSON

    /**
     * Sélectionne toutes les boissons et les place dans le tableau data_boissons
     */
    public void selectBoissons(final String tri) {
        //sorting and searching
        DatabaseReference myRef = database.getReference("boissons");

        // Tri par identifiant
        Query query = myRef.orderByChild(tri);
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boisson b;
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    b = new Boisson(((Long) messageSnapshot.child("id").getValue()).intValue(), (String) messageSnapshot.child("nom").getValue(), (String) messageSnapshot.child("description_courte").getValue(), (String) messageSnapshot.child("description_longue").getValue(), (Double) Double.parseDouble(messageSnapshot.child("taux_alcoolemie").getValue().toString()), ((String) messageSnapshot.child("type").getValue()), ((String) messageSnapshot.child("sous_type").getValue()), (Integer) Integer.parseInt(messageSnapshot.child("prix").getValue().toString()));
                    data_boissons.add(b);
                }

                images = new Drawable[data_boissons.size()];
                selectImage(images, 0, data_boissons);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "select cancelled", Toast.LENGTH_LONG);
            }
        });
    }

    /**
     * Sélectionne toutes les images stockées en base de données pour les faire apparaitre dans
     * les vignettes
     * Fonction récursive qui prend en compte le tableau à remplir à chaque appel puis l'index
     * de l'élément à insérer.
     * Le tableau a la taille du nombre d'images, donc nous arrêtons les appels dès que l'index de
     * l'élément à insérer est égal à la taille du tableau.
     * Une fois que toutes les images sont chargées, on appelle affichage_boissons qui affichera
     * toutes les images avec la boisson associée.
     * @param images : le tableau d'images de boisson à remplir à partir du storage de firebase
     *               L'image d'une boisson porte le même nom et est au format jpg
     * @param index : l'index de l'élément courant à insérer. S'il est égal à la taille du tableau,
     *              le chargement est terminé et on lance l'affichage
     */
    public void selectImage(final Drawable[] images, final int index, final ArrayList<Boisson> ref) {

        // Une fois que toutes les images sont chargées, on procède à leur affichage
        if (images.length == index) {
            affichage_boissons(data_boissons);
        } else {
            // sinon, on continue de les charger à partir de la base de données (mStorageRef)
            localFile = null;
            mStorageRef = storage.getReference();
            boissonRef = mStorageRef.child("photosboissons/" + ref.get(index).getNom() +".jpg");

            // Fichier local qui va prendre l'image
            try {
                localFile = File.createTempFile("images", "jpeg");
            } catch (IOException e) {
                e.printStackTrace();
            }

            boissonRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                            // On attend que l'image est complètement chargée avant de l'affecter
                            try {
                                imageVignette = null;
                                do {
                                    imageVignette = Drawable.createFromPath(localFile.getPath());
                                } while(imageVignette == null);

                                data_boissons.get(index).setImage(imageVignette);
                                images[index] = imageVignette;
                                // Appel de la même fonction avec le tableau alimenté et l'index + 1
                                selectImage(images, index+1, ref);

                            } catch (Exception e) {
                                //TODO ERREUR
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    //TODO ERREUR
                }
            });
        }
    }

    /**
     * Permet d'insérer la boisson passée en paramètre en base de données
     * @param b : la boisson à insérer
     * @throws Exception : en cas d'erreur de création de boisson ou d'accès en base
     */
    public void insertBoisson(Boisson b) throws Exception {
        myRef.child("boissons").child(String.valueOf(b.getId())).setValue(b);
    }

    //endregion BOISSON

    //region COMMANDE

    /**
     * Permet d'insérer la commande passée en paramètre en base de données
     * @param c : la commande à insérer
     * @throws Exception : en cas d'erreur de création de commande ou d'accès en base
     */
    public void insertCommande(Commande c) throws Exception {
        myRef.child("commandes").child(String.valueOf(c.getId())).setValue(c);
    }

    /**
     * Stocke la valeur de l'identifiant de la commande
     * Elle sera égale à la dernière commande + 1
     */
    public void getId() {
        //sorting and searching
        DatabaseReference myRef = database.getReference("commandes");

        // Tri par identifiant
        Query query = myRef.orderByChild("id");
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                id_commande = 0;
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    id = ((Long) messageSnapshot.child("id").getValue()).intValue() + 1;
                    id_commande = ((Long) messageSnapshot.child("idCommande").getValue()).intValue() + 1;
                }

                insertionCommandes();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "select cancelled", Toast.LENGTH_LONG);
            }
        });
    }

    /**
     * Sélectionne toutes les commandes et les place dans le tableau data_commandes
     */
    public void selectCommandes() {
        //sorting and searching
        DatabaseReference myRef = database.getReference("commandes");

        // Tri par identifiant
        Query query = myRef.orderByChild("idCommande");
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Commande c;
                data_commandes.clear();
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    c = new Commande(new Boisson(((Long) messageSnapshot.child("boisson_commande/id").getValue()).intValue(), (String) messageSnapshot.child("boisson_commande/nom").getValue(), (String) messageSnapshot.child("boisson_commande/description_courte").getValue(), (String) messageSnapshot.child("boisson_commande/description_longue").getValue(), (Double) Double.parseDouble(messageSnapshot.child("boisson_commande/taux_alcoolemie").getValue().toString()), ((String) messageSnapshot.child("boisson_commande/type").getValue()), ((String) messageSnapshot.child("boisson_commande/sous_type").getValue()), (Integer) Integer.parseInt(messageSnapshot.child("boisson_commande/prix").getValue().toString())), (Integer) Integer.parseInt(messageSnapshot.child("quantiteCommande").getValue().toString()), null, ((String) messageSnapshot.child("idCocktail").getValue()), ((String) messageSnapshot.child("nom").getValue()), (Integer) Integer.parseInt(messageSnapshot.child("idCommande").getValue().toString()));
                    data_commandes.add(c);
                }

                affichage_cocktails(data_commandes, -1);
                /*images_commandes = new Drawable[data_commandes.size()];
                selectImageCommandes(images_commandes, 0, data_commandes);*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "select cancelled", Toast.LENGTH_LONG);
            }
        });
    }

    /**
     * Sélectionne toutes les images stockées en base de données pour les faire apparaitre dans
     * les vignettes
     * Fonction récursive qui prend en compte le tableau à remplir à chaque appel puis l'index
     * de l'élément à insérer.
     * Le tableau a la taille du nombre d'images, donc nous arrêtons les appels dès que l'index de
     * l'élément à insérer est égal à la taille du tableau.
     * Une fois que toutes les images sont chargées, on appelle affichage_boissons qui affichera
     * toutes les images avec la boisson associée.
     * @param images : le tableau d'images de boisson à remplir à partir du storage de firebase
     *               L'image d'une boisson porte le même nom et est au format jpg
     * @param index : l'index de l'élément courant à insérer. S'il est égal à la taille du tableau,
     *              le chargement est terminé et on lance l'affichage
     */
    public void selectImageCommandes(final Drawable[] images, final int index, final ArrayList<Commande> ref) {

        // Une fois que toutes les images sont chargées, on procède à leur affichage
        if (images.length == index) {
            affichage_cocktails(data_commandes, -1);
        } else {
            // sinon, on continue de les charger à partir de la base de données (mStorageRef)
            localFile = null;
            mStorageRef = storage.getReference();
            boissonRef = mStorageRef.child("photosboissons/" + ref.get(index).getNom() +".jpg");

            // Fichier local qui va prendre l'image
            try {
                localFile = File.createTempFile("images", "jpeg");
            } catch (IOException e) {
                e.printStackTrace();
            }

            boissonRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                            // On attend que l'image est complètement chargée avant de l'affecter
                            try {
                                imageVignette = null;
                                do {
                                    imageVignette = Drawable.createFromPath(localFile.getPath());
                                } while(imageVignette == null);

                                data_boissons.get(index).setImage(imageVignette);
                                images[index] = imageVignette;
                                // Appel de la même fonction avec le tableau alimenté et l'index + 1
                                selectImageCommandes(images, index+1, ref);

                            } catch (Exception e) {
                                //TODO ERREUR
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    //TODO ERREUR
                }
            });
        }
    }

    public void insertionCommandes() {
        ArrayList<Commande> commandes = new ArrayList<Commande>();
        for (BoissonQuantite bq : cocktail.getCocktail()) {
            bq.getBoisson().setImage(null);
            Commande commande = new Commande(bq.getBoisson(), bq.getQuantite(), utilisateur_connecte, utilisateur_connecte.id, "", id_commande);
            commande.setId(id);
            commandes.add(commande);
            id++;
        }

        // on simule la distribution de la boisson
        Toast commande_en_cours = Toast.makeText(getApplicationContext(), "Commande en cours...", Toast.LENGTH_LONG);
        commande_en_cours.show();

        // on débite le compte utilisateur
        utilisateur_connecte.ptConso -= cocktail.getPrix();

        // utilisateur_connecte existe en base, ses points consos seront juste mis à jour
        try {
            insertUtilisateur(utilisateur_connecte);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // on insert l'objet Commande en base de données
        try {
            for (Commande aInserer : commandes) {
                insertCommande(aInserer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // reset de la liste des boissons sélectionnées
        cocktail.getCocktail().clear();
        TableLayout liste_boissons = (TableLayout) findViewById(R.id.liste_boissons_cocktails_choisis);
        liste_boissons.removeAllViews();

        // reset des sélections
        if (affichage_boisson) {
            for (int i = 0; i < nb_boissons; i++) {
                SeekBar aChanger = (SeekBar) findViewById(i+1000);
                aChanger.setProgress(0);
                aChanger.setMax(verre);
            }

            quantite_boissons = 0;
        }

        selectCommandes();
    }

    //endregion COMMANDE

    //region UTILISATEUR

    /**
     * Insert l'utilisateur passé en paramètre
     * S'il existe, le met à jour en base de données
     * @param u : l'utilisateur à insérer
     * @throws Exception : en cas d'erreur d'insertion ou d'accès en base
     */
    public void insertUtilisateur(Utilisateur u) throws Exception {
        myRef.child("users").child(String.valueOf(u.id)).setValue(u);
    }

    /**
     * Sélectionne l'utilisateur qui vient de se connecter grâce à l'identifant passé en paramètre
     */
    public void selectUtilisateur(String id) {
        //sorting and searching
        DatabaseReference myRef = database.getReference("users");

        // Tri par identifiant
        Query query = myRef.orderByChild("id").equalTo(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    utilisateur_connecte = new Utilisateur((String) messageSnapshot.child("id").getValue(), (String) messageSnapshot.child("mail").getValue(), (String) messageSnapshot.child("nom").getValue(), (String) messageSnapshot.child("prenom").getValue(), ((Long) messageSnapshot.child("ptFidelite").getValue()).intValue(), ((Long) messageSnapshot.child("ptConso").getValue()).intValue());
                    break; // on prend que le premier qui correspond -> normalement il y en a qu'un
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "select cancelled", Toast.LENGTH_LONG);
            }
        });
    }

    //endregion UTILISATEUR

    //endregion DONNEES

    //region FONCTIONS

    //region BOISSON
    /**
     * Affiche toutes les boissons avec :
     *  - une image représentative
     *  - un titre et une description
     *  - leur degré d'alcool
     * @param boissons : les boissons à afficher
     */
    public void affichage_boissons(ArrayList<Boisson> boissons) {
        TableLayout layout_boissons = (TableLayout) findViewById(R.id.liste_boissons);

        int i = 0;
        nb_boissons = boissons.size();
        id_affiche = new ArrayList<Integer>();
        id_affiche.clear();

        // Pour chaque boisson, on crée une nouvelle vignette à insérer
        for (Boisson boisson : boissons) {
            if (leFiltre.equals("tous")) {
                id_affiche.add(i);

                // regroupe image et description
                TableRow vignette = new TableRow(this);
                vignette.setId(i);

                // image
                ImageView image = new ImageView(this);

                Bitmap bitmap = ((BitmapDrawable) boisson.getImage()).getBitmap();
                Drawable image_reduite = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 75, 75, true));
                image.setId(10000 + i);
                image.setImageDrawable(image_reduite);

                // nom de la boisson en gras
                TextView nom = new TextView(this);
                nom.setId(100 + i);
                nom.setText(boisson.getNom());
                nom.setTypeface(null, BOLD);
                nom.setTextSize(16);

                // prix centré à droite
                TextView prix = new TextView(this);
                prix.setId(500 + i);
                prix.setText(String.valueOf(boisson.getPrix()));
                prix.setTextSize(16);
                prix.setGravity(Gravity.RIGHT);
                prix.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_piece, 0);

                vignette.addView(image);
                vignette.addView(nom);
                vignette.addView(prix);

                // choix de la quantité
                TableRow quantite_boisson = new TableRow(this);
                TextView vide = new TextView(this);
                TextView quantite_texte = new TextView(this);

                quantite_texte.setId(2000 + i);
                SeekBar quantite = new SeekBar(this);
                quantite.setId(1000 + i);
                quantite.setMax(verre);
                ShapeDrawable thumb = new ShapeDrawable(new OvalShape());
                thumb.setIntrinsicHeight(30);
                thumb.setIntrinsicWidth(30);
                quantite.setThumb(thumb);
                quantite.setProgress(0);
                quantite_texte.setText(quantite.getProgress() + "cl");

                /**
                 * On met à jour la valeur du texte à côté du seekBar pour informer l'utilisateur
                 * de la quantité de la boisson qu'il est en train de se servir
                 */
                quantite.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        TextView aChanger = (TextView) findViewById(seekBar.getId()+1000);
                        aChanger.setText(i + "cl");

                        if (i == 0) {
                            btn_ajouter.setClickable(false);
                            if (isBoissonSelectionnee()) {
                                btn_ajouter.setClickable(true);
                            }
                        } else {
                            btn_ajouter.setClickable(true);
                        }
                    }

                    /**
                     * On récupère la valeur de départ afin d'évaluer la variation du progress
                     * dans le onStopTrackingTouch
                     * @param seekBar : le seekbar qui a été touché
                     */
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        quantite_start = seekBar.getProgress();
                    }

                    /**
                     * Met à jour la valeur maximale des seek bar qui ne sont pas encore renseignées
                     * pour éviter à l'utilisateur de rentrer des valeurs plus grandes que ce que
                     * permet le verre
                     * @param seekBar : la seekBar qui a été modifiée à l'instant : celle-ci on y
                     *                touche pas, par contre on récupère sa progression pour savoir
                     *                où en est le total de quantité
                     */
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        SeekBar aChanger;
                        quantite_stop = seekBar.getProgress();

                        if (quantite_stop > quantite_start) {
                            quantite_boissons += quantite_stop - quantite_start;
                        } else {
                            quantite_boissons -= quantite_start - quantite_stop;
                        }

                        for (int i  : id_affiche) {
                            aChanger = (SeekBar) findViewById(1000 + i);
                            if ((i+1000) != seekBar.getId() && aChanger.getProgress() == 0) {
                                aChanger.setMax(verre - quantite_boissons);
                            }
                        }
                    }
                });

                quantite_boisson.addView(vide);
                quantite_boisson.addView(quantite);
                quantite_boisson.addView(quantite_texte);

                TableRow vignette_vide = new TableRow(this);
                TextView texte_vide = new TextView(this);
                texte_vide.setText("lala");
                texte_vide.setVisibility(View.INVISIBLE);
                vignette_vide.addView(texte_vide);

                layout_boissons.addView(vignette);
                layout_boissons.addView(quantite_boisson);
                layout_boissons.addView(vignette_vide);
            } else if (leFiltre.equals("fort") && boisson.isAlcoolFort()) {
                id_affiche.add(i);

                // regroupe image et description
                TableRow vignette = new TableRow(this);
                vignette.setId(i);

                // image
                ImageView image = new ImageView(this);

                Bitmap bitmap = ((BitmapDrawable) boisson.getImage()).getBitmap();
                Drawable image_reduite = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 75, 75, true));
                image.setId(10000 + i);
                image.setImageDrawable(image_reduite);

                // nom de la boisson en gras
                TextView nom = new TextView(this);
                nom.setId(100 + i);
                nom.setText(boisson.getNom());
                nom.setTypeface(null, BOLD);
                nom.setTextSize(16);

                // prix centré à droite
                TextView prix = new TextView(this);
                prix.setId(500 + i);
                prix.setText(String.valueOf(boisson.getPrix()));
                prix.setTextSize(16);
                prix.setGravity(Gravity.RIGHT);
                prix.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_piece, 0);

                vignette.addView(image);
                vignette.addView(nom);
                vignette.addView(prix);

                // choix de la quantité
                TableRow quantite_boisson = new TableRow(this);
                TextView vide = new TextView(this);
                TextView quantite_texte = new TextView(this);

                quantite_texte.setId(2000 + i);
                SeekBar quantite = new SeekBar(this);
                quantite.setId(1000 + i);
                quantite.setMax(verre);
                ShapeDrawable thumb = new ShapeDrawable(new OvalShape());
                thumb.setIntrinsicHeight(30);
                thumb.setIntrinsicWidth(30);
                quantite.setThumb(thumb);
                quantite.setProgress(0);
                quantite_texte.setText(quantite.getProgress() + "cl");

                /**
                 * On met à jour la valeur du texte à côté du seekBar pour informer l'utilisateur
                 * de la quantité de la boisson qu'il est en train de se servir
                 */
                quantite.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        TextView aChanger = (TextView) findViewById(seekBar.getId()+1000);
                        aChanger.setText(i + "cl");

                        if (i == 0) {
                            btn_ajouter.setClickable(false);
                            if (isBoissonSelectionnee()) {
                                btn_ajouter.setClickable(true);
                            }
                        } else {
                            btn_ajouter.setClickable(true);
                        }
                    }

                    /**
                     * On récupère la valeur de départ afin d'évaluer la variation du progress
                     * dans le onStopTrackingTouch
                     * @param seekBar : le seekbar qui a été touché
                     */
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        quantite_start = seekBar.getProgress();
                    }

                    /**
                     * Met à jour la valeur maximale des seek bar qui ne sont pas encore renseignées
                     * pour éviter à l'utilisateur de rentrer des valeurs plus grandes que ce que
                     * permet le verre
                     * @param seekBar : la seekBar qui a été modifiée à l'instant : celle-ci on y
                     *                touche pas, par contre on récupère sa progression pour savoir
                     *                où en est le total de quantité
                     */
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        SeekBar aChanger;
                        quantite_stop = seekBar.getProgress();

                        if (quantite_stop > quantite_start) {
                            quantite_boissons += quantite_stop - quantite_start;
                        } else {
                            quantite_boissons -= quantite_start - quantite_stop;
                        }

                        for (int i  : id_affiche) {
                            aChanger = (SeekBar) findViewById(1000 + i);
                            if ((i+1000) != seekBar.getId() && aChanger.getProgress() == 0) {
                                aChanger.setMax(verre - quantite_boissons);
                            }
                        }
                    }
                });

                quantite_boisson.addView(vide);
                quantite_boisson.addView(quantite);
                quantite_boisson.addView(quantite_texte);

                TableRow vignette_vide = new TableRow(this);
                TextView texte_vide = new TextView(this);
                texte_vide.setText("lala");
                texte_vide.setVisibility(View.INVISIBLE);
                vignette_vide.addView(texte_vide);

                layout_boissons.addView(vignette);
                layout_boissons.addView(quantite_boisson);
                layout_boissons.addView(vignette_vide);
            } else if (leFiltre.equals("doux") && boisson.isAlcoolDoux()) {
                id_affiche.add(i);


                // regroupe image et description
                TableRow vignette = new TableRow(this);
                vignette.setId(i);

                // image
                ImageView image = new ImageView(this);

                Bitmap bitmap = ((BitmapDrawable) boisson.getImage()).getBitmap();
                Drawable image_reduite = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 75, 75, true));
                image.setId(10000 + i);
                image.setImageDrawable(image_reduite);

                // nom de la boisson en gras
                TextView nom = new TextView(this);
                nom.setId(100 + i);
                nom.setText(boisson.getNom());
                nom.setTypeface(null, BOLD);
                nom.setTextSize(16);

                // prix centré à droite
                TextView prix = new TextView(this);
                prix.setId(500 + i);
                prix.setText(String.valueOf(boisson.getPrix()));
                prix.setTextSize(16);
                prix.setGravity(Gravity.RIGHT);
                prix.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_piece, 0);

                vignette.addView(image);
                vignette.addView(nom);
                vignette.addView(prix);

                // choix de la quantité
                TableRow quantite_boisson = new TableRow(this);
                TextView vide = new TextView(this);
                TextView quantite_texte = new TextView(this);

                quantite_texte.setId(2000 + i);
                SeekBar quantite = new SeekBar(this);
                quantite.setId(1000 + i);
                quantite.setMax(verre);
                ShapeDrawable thumb = new ShapeDrawable(new OvalShape());
                thumb.setIntrinsicHeight(30);
                thumb.setIntrinsicWidth(30);
                quantite.setThumb(thumb);
                quantite.setProgress(0);
                quantite_texte.setText(quantite.getProgress() + "cl");

                /**
                 * On met à jour la valeur du texte à côté du seekBar pour informer l'utilisateur
                 * de la quantité de la boisson qu'il est en train de se servir
                 */
                quantite.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        TextView aChanger = (TextView) findViewById(seekBar.getId()+1000);
                        aChanger.setText(i + "cl");

                        if (i == 0) {
                            btn_ajouter.setClickable(false);
                            if (isBoissonSelectionnee()) {
                                btn_ajouter.setClickable(true);
                            }
                        } else {
                            btn_ajouter.setClickable(true);
                        }
                    }

                    /**
                     * On récupère la valeur de départ afin d'évaluer la variation du progress
                     * dans le onStopTrackingTouch
                     * @param seekBar : le seekbar qui a été touché
                     */
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        quantite_start = seekBar.getProgress();
                    }

                    /**
                     * Met à jour la valeur maximale des seek bar qui ne sont pas encore renseignées
                     * pour éviter à l'utilisateur de rentrer des valeurs plus grandes que ce que
                     * permet le verre
                     * @param seekBar : la seekBar qui a été modifiée à l'instant : celle-ci on y
                     *                touche pas, par contre on récupère sa progression pour savoir
                     *                où en est le total de quantité
                     */
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        SeekBar aChanger;
                        quantite_stop = seekBar.getProgress();

                        if (quantite_stop > quantite_start) {
                            quantite_boissons += quantite_stop - quantite_start;
                        } else {
                            quantite_boissons -= quantite_start - quantite_stop;
                        }

                        for (int i  : id_affiche) {
                            aChanger = (SeekBar) findViewById(1000 + i);
                            if ((i+1000) != seekBar.getId() && aChanger.getProgress() == 0) {
                                aChanger.setMax(verre - quantite_boissons);
                            }
                        }
                    }
                });

                quantite_boisson.addView(vide);
                quantite_boisson.addView(quantite);
                quantite_boisson.addView(quantite_texte);

                TableRow vignette_vide = new TableRow(this);
                TextView texte_vide = new TextView(this);
                texte_vide.setText("lala");
                texte_vide.setVisibility(View.INVISIBLE);
                vignette_vide.addView(texte_vide);

                layout_boissons.addView(vignette);
                layout_boissons.addView(quantite_boisson);
                layout_boissons.addView(vignette_vide);
            } else if (leFiltre.equals("soft") && boisson.isSoft()) {
                id_affiche.add(i);


                // regroupe image et description
                TableRow vignette = new TableRow(this);
                vignette.setId(i);

                // image
                ImageView image = new ImageView(this);

                Bitmap bitmap = ((BitmapDrawable) boisson.getImage()).getBitmap();
                Drawable image_reduite = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 75, 75, true));
                image.setId(10000 + i);
                image.setImageDrawable(image_reduite);

                // nom de la boisson en gras
                TextView nom = new TextView(this);
                nom.setId(100 + i);
                nom.setText(boisson.getNom());
                nom.setTypeface(null, BOLD);
                nom.setTextSize(16);

                // prix centré à droite
                TextView prix = new TextView(this);
                prix.setId(500 + i);
                prix.setText(String.valueOf(boisson.getPrix()));
                prix.setTextSize(16);
                prix.setGravity(Gravity.RIGHT);
                prix.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_piece, 0);

                vignette.addView(image);
                vignette.addView(nom);
                vignette.addView(prix);

                // choix de la quantité
                TableRow quantite_boisson = new TableRow(this);
                TextView vide = new TextView(this);
                TextView quantite_texte = new TextView(this);

                quantite_texte.setId(2000 + i);
                SeekBar quantite = new SeekBar(this);
                quantite.setId(1000 + i);
                quantite.setMax(verre);
                ShapeDrawable thumb = new ShapeDrawable(new OvalShape());
                thumb.setIntrinsicHeight(30);
                thumb.setIntrinsicWidth(30);
                quantite.setThumb(thumb);
                quantite.setProgress(0);
                quantite_texte.setText(quantite.getProgress() + "cl");

                /**
                 * On met à jour la valeur du texte à côté du seekBar pour informer l'utilisateur
                 * de la quantité de la boisson qu'il est en train de se servir
                 */
                quantite.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        TextView aChanger = (TextView) findViewById(seekBar.getId()+1000);
                        aChanger.setText(i + "cl");

                        if (i == 0) {
                            btn_ajouter.setClickable(false);
                            if (isBoissonSelectionnee()) {
                                btn_ajouter.setClickable(true);
                            }
                        } else {
                            btn_ajouter.setClickable(true);
                        }
                    }

                    /**
                     * On récupère la valeur de départ afin d'évaluer la variation du progress
                     * dans le onStopTrackingTouch
                     * @param seekBar : le seekbar qui a été touché
                     */
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        quantite_start = seekBar.getProgress();
                    }

                    /**
                     * Met à jour la valeur maximale des seek bar qui ne sont pas encore renseignées
                     * pour éviter à l'utilisateur de rentrer des valeurs plus grandes que ce que
                     * permet le verre
                     * @param seekBar : la seekBar qui a été modifiée à l'instant : celle-ci on y
                     *                touche pas, par contre on récupère sa progression pour savoir
                     *                où en est le total de quantité
                     */
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        SeekBar aChanger;
                        quantite_stop = seekBar.getProgress();

                        if (quantite_stop > quantite_start) {
                            quantite_boissons += quantite_stop - quantite_start;
                        } else {
                            quantite_boissons -= quantite_start - quantite_stop;
                        }

                        for (int i  : id_affiche) {
                            aChanger = (SeekBar) findViewById(1000 + i);
                            if ((i+1000) != seekBar.getId() && aChanger.getProgress() == 0) {
                                aChanger.setMax(verre - quantite_boissons);
                            }
                        }
                    }
                });

                quantite_boisson.addView(vide);
                quantite_boisson.addView(quantite);
                quantite_boisson.addView(quantite_texte);

                TableRow vignette_vide = new TableRow(this);
                TextView texte_vide = new TextView(this);
                texte_vide.setText("lala");
                texte_vide.setVisibility(View.INVISIBLE);
                vignette_vide.addView(texte_vide);

                layout_boissons.addView(vignette);
                layout_boissons.addView(quantite_boisson);
                layout_boissons.addView(vignette_vide);
            }

            i++;
        }
    }

    /**
     * Vérifie s'il y a encore une boisson de sélectionnée afin d'activer le bouton "Ajouter"
     * ou non
     * @return vrai si une boisson a été sélectionnée, faux sinon
     */
    public boolean isBoissonSelectionnee() {
        SeekBar temp;
        for (int j : id_affiche) {
            temp = (SeekBar) findViewById(j + 1000);
            if (temp.getProgress() > 0) {
                return true;
            }
        }

        return false;
    }

    public Boisson getById(int id) {
        for (Boisson aRetourner : data_boissons) {
            if (aRetourner.getId() == id) {
                return aRetourner;
            }
        }

        return null;
    }

    //endregion BOISSON

    //region COMMANDES
    public void affichage_cocktails(ArrayList<Commande> commandes, int aDevelopper) {
        TableLayout layout_cocktails = (TableLayout) findViewById(R.id.liste_cocktails);
        layout_cocktails.removeAllViews();

        int i = 0;
        int ancien_id_commande = -1;
        int nouveau_id_commande;
        int num_cocktail = 1;
        id_affiche = new ArrayList<Integer>();
        id_affiche.clear();

        // Pour chaque boisson, on crée une nouvelle vignette à insérer
        for (Commande cocktail : commandes) {
            nouveau_id_commande = cocktail.getIdCommande();
            if (ancien_id_commande != nouveau_id_commande) {
                if (cocktail.isCommun() && leFiltreCocktail == "commun") {
                    TableRow nom_cocktail = new TableRow(this);
                    nom_cocktail.setId(1000 + cocktail.getIdCommande());
                    // on a changé de cocktail, on affiche un espace puis le nom
                    TextView espace = new TextView(this);
                    espace.setText("invivi");
                    espace.setVisibility(View.INVISIBLE);
                    TextView nom = new TextView(this);
                    if (!cocktail.getNom().equals(null)) {
                        nom.setText(cocktail.getNom());
                    } else {
                        nom.setText("Cocktail " + num_cocktail);
                        num_cocktail++;
                    }

                    nom.setTypeface(null, Typeface.BOLD);
                    nom.setTextSize(20);

                    nom_cocktail.setOnClickListener(click_vignette);
                    nom_cocktail.addView(espace);
                    nom_cocktail.addView(nom);

                    layout_cocktails.addView(nom_cocktail);
                    i++;

                } else if (cocktail.getIdCocktail().equals(utilisateur_connecte.id) && leFiltreCocktail.equals("perso")) {
                    TableRow nom_cocktail = new TableRow(this);
                    nom_cocktail.setId(1000 + cocktail.getIdCommande());
                    // on a changé de cocktail, on affiche un espace puis le nom
                    TextView espace = new TextView(this);
                    espace.setText("invivi");
                    espace.setVisibility(View.INVISIBLE);
                    TextView nom = new TextView(this);
                    nom.setText("Cocktail " + num_cocktail);
                    num_cocktail++;

                    nom.setTypeface(null, Typeface.BOLD);
                    nom.setTextSize(20);

                    nom_cocktail.setOnClickListener(click_vignette);
                    nom_cocktail.addView(espace);
                    nom_cocktail.addView(nom);

                    layout_cocktails.addView(nom_cocktail);
                    i++;
                } else if (!cocktail.isCommun() && !cocktail.getIdCocktail().equals(utilisateur_connecte.id) && leFiltreCocktail.equals("autres")) {
                    TableRow nom_cocktail = new TableRow(this);
                    nom_cocktail.setId(1000 + cocktail.getIdCommande());
                    // on a changé de cocktail, on affiche un espace puis le nom
                    TextView espace = new TextView(this);
                    espace.setText("invivi");
                    espace.setVisibility(View.INVISIBLE);
                    TextView nom = new TextView(this);
                    nom.setText("Cocktail " + num_cocktail);
                    num_cocktail++;

                    nom.setTypeface(null, Typeface.BOLD);
                    nom.setTextSize(20);

                    nom_cocktail.setOnClickListener(click_vignette);
                    nom_cocktail.addView(espace);
                    nom_cocktail.addView(nom);

                    layout_cocktails.addView(nom_cocktail);
                    i++;
                }

                ancien_id_commande = cocktail.getIdCommande();
            }

            if (cocktail.getIdCommande() == aDevelopper) {

                id_affiche.add(cocktail.getBoisson_commande().getId());
                // on affiche ses boissons
                TableRow ligne_boisson = new TableRow(this);
                // nom de la boisson en gras
                TextView nom_boisson = new TextView(this);
                nom_boisson.setId(100 + cocktail.getBoisson_commande().getId());
                nom_boisson.setText(cocktail.getBoisson_commande().getNom());
                nom_boisson.setTypeface(null, Typeface.BOLD);
                nom_boisson.setTextSize(16);

                // prix centré à droite
                TextView prix = new TextView(this);
                prix.setId(500 + cocktail.getBoisson_commande().getId());
                prix.setText(String.valueOf(cocktail.getBoisson_commande().getPrix()));
                prix.setTextSize(16);
                prix.setGravity(Gravity.RIGHT);
                prix.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_piece, 0);

                // choix de la quantité
                TableRow quantite_boisson = new TableRow(this);
                TextView vide = new TextView(this);
                TextView quantite_texte = new TextView(this);

                quantite_texte.setId(2000 + cocktail.getBoisson_commande().getId());
                SeekBar quantite = new SeekBar(this);
                quantite.setId(3000 + cocktail.getBoisson_commande().getId());
                quantite.setMax(verre);
                ShapeDrawable thumb = new ShapeDrawable(new OvalShape());
                thumb.setIntrinsicHeight(30);
                thumb.setIntrinsicWidth(30);
                quantite.setThumb(thumb);
                quantite.setProgress(cocktail.getQuantiteCommande());
                quantite_texte.setText(quantite.getProgress() + "cl");

                /**
                 * On met à jour la valeur du texte à côté du seekBar pour informer l'utilisateur
                 * de la quantité de la boisson qu'il est en train de se servir
                 */
                quantite.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        TextView aChanger = (TextView) findViewById(seekBar.getId()-1000);
                        aChanger.setText(i + "cl");
                        /*
                        if (i == 0) {
                            btn_ajouter.setClickable(false);
                            if (isBoissonSelectionnee()) {
                                btn_ajouter.setClickable(true);
                            }
                        } else {
                            btn_ajouter.setClickable(true);
                        }*/
                    }

                    /**
                     * On récupère la valeur de départ afin d'évaluer la variation du progress
                     * dans le onStopTrackingTouch
                     * @param seekBar : le seekbar qui a été touché
                     */
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        //quantite_start = seekBar.getProgress();
                    }

                    /**
                     * Met à jour la valeur maximale des seek bar qui ne sont pas encore renseignées
                     * pour éviter à l'utilisateur de rentrer des valeurs plus grandes que ce que
                     * permet le verre
                     * @param seekBar : la seekBar qui a été modifiée à l'instant : celle-ci on y
                     *                touche pas, par contre on récupère sa progression pour savoir
                     *                où en est le total de quantité
                     */
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        /*SeekBar aChanger;
                        quantite_stop = seekBar.getProgress();

                        if (quantite_stop > quantite_start) {
                            quantite_boissons += quantite_stop - quantite_start;
                        } else {
                            quantite_boissons -= quantite_start - quantite_stop;
                        }

                        for (int i  : id_affiche) {
                            aChanger = (SeekBar) findViewById(1000 + i);
                            if ((i+1000) != seekBar.getId() && aChanger.getProgress() == 0) {
                                aChanger.setMax(verre - quantite_boissons);
                            }
                        }*/
                    }
                });

                quantite_boisson.addView(vide);
                quantite_boisson.addView(quantite);
                quantite_boisson.addView(quantite_texte);

                ligne_boisson.addView(nom_boisson);
                ligne_boisson.addView(prix);
                layout_cocktails.addView(ligne_boisson);
                layout_cocktails.addView(quantite_boisson);
            }
        }
    }

    //endregion COMMANDES

    //endregion FONCTIONS

    //region GESTION EVENEMENT

    //region BOISSON
    View.OnClickListener btn_ajouter_click = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            SeekBar temp;

            if (affichage_boisson) {
                for (int j  : id_affiche) {
                    temp = (SeekBar) findViewById(j + 1000);
                    if (temp.getProgress() > 0) {
                        BoissonQuantite bq = new BoissonQuantite(data_boissons.get(j), temp.getProgress());

                        if (cocktail.contains(bq)) {
                            // on ajoute seulement de la quantité à la boisson sélectionnée
                            cocktail.get(cocktail.indexOf(bq)).setQuantite(cocktail.get(cocktail.indexOf(bq)).getQuantite() + temp.getProgress());
                        } else {
                            // on ajoute le cocktail izi
                            cocktail.add(bq);
                        }
                    }
                }

            /*for (Boisson b : data_boissons) {
                temp = (SeekBar) findViewById(i + 1000);
                if (temp.getProgress() > 0) {
                    BoissonQuantite bq = new BoissonQuantite(b, temp.getProgress());

                    if (cocktail.contains(bq)) {
                        // on ajoute seulement de la quantité à la boisson sélectionnée
                        cocktail.get(cocktail.indexOf(bq)).setQuantite(cocktail.get(cocktail.indexOf(bq)).getQuantite() + temp.getProgress());
                    } else {
                        // on ajoute le cocktail izi
                        cocktail.add(bq);
                    }
                }

                i++;
            }*/

                //on remet à 0 les valeurs des boissons sélectionnées
                for (int j  : id_affiche) {
                    temp = (SeekBar) findViewById(j + 1000);
                    temp.setProgress(0);
                    temp.setMax(verre - quantite_boissons);
                }
            } else {
                // ajout des boissons juste
                BoissonQuantite bq = null;
                for (int j  : id_affiche) {
                    temp = (SeekBar) findViewById(j + 3000);
                    if (temp.getProgress() > 0) {
                        bq = new BoissonQuantite(getById(j), temp.getProgress());
                    }

                    if (cocktail.contains(bq)) {
                        // on ajoute seulement de la quantité à la boisson sélectionnée
                        cocktail.get(cocktail.indexOf(bq)).setQuantite(cocktail.get(cocktail.indexOf(bq)).getQuantite() + temp.getProgress());
                    } else {
                        // on ajoute le cocktail izi
                        cocktail.add(bq);
                    }
                }


            }

            // affichage des boissons en dessous
            TableLayout liste_boissons_choisies = (TableLayout) findViewById(R.id.liste_boissons_cocktails_choisis);
            liste_boissons_choisies.removeAllViews();
            for (BoissonQuantite bq : cocktail.getCocktail()) {
                TableRow ligne_boisson_choisie = new TableRow(getApplicationContext());
                TextView nom_boisson_choisie = new TextView(getApplicationContext());
                TextView quantite_boisson_choisie = new TextView(getApplicationContext());

                nom_boisson_choisie.setText(bq.getBoisson().getNom());
                quantite_boisson_choisie.setText(bq.getQuantite() + "cl");

                ligne_boisson_choisie.addView(nom_boisson_choisie);
                ligne_boisson_choisie.addView(quantite_boisson_choisie);

                liste_boissons_choisies.addView(ligne_boisson_choisie);
            }

            TableRow ligne_prix_total = new TableRow(getApplicationContext());
            TextView texte_total = new TextView(getApplicationContext());
            TextView prix_total = new TextView(getApplicationContext());

            texte_total.setText("TOTAL : ");
            prix_total.setText(String.valueOf(cocktail.getPrix()));
            prix_total.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_piece, 0);

            ligne_prix_total.addView(texte_total);
            ligne_prix_total.addView(prix_total);
            liste_boissons_choisies.addView(ligne_prix_total);
        }
    };

    View.OnClickListener click_btn_verre = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_verre_4:
                    verre = 4;
                    break;
                case R.id.btn_verre_15:
                    verre = 15;
                    break;
                case R.id.btn_verre_25:
                    verre = 25;
                    break;
                case R.id.btn_verre_50:
                    verre = 50;
                    break;
                case R.id.btn_verre_100:
                    verre = 100;
                    break;
            }

            verre_selectionne.setText( verre+ "cl");

            if (affichage_boisson) {
                // on met à jour la valeur maximale des boissons
                for (int i  : id_affiche) {
                    SeekBar aChanger = (SeekBar) findViewById(i+1000);
                    aChanger.setMax(verre);
                }
            } else {
                // met à jour les quantités
                for (int i  : id_affiche) {
                    SeekBar aChanger = (SeekBar) findViewById(i+3000);
                    aChanger.setMax(verre);
                }
            }
        }
    };

    View.OnClickListener click_btn_reset = new View.OnClickListener() {

        /**
         * Mise à zéro de :
         *  - la liste des boissons sélectionnées (avec màj de l'affichage)
         *  - les sélections des quantités de chaque boisson
         * @param v : le bouton sur lequel l'utilisateur a appuyé (là y en a qu'un)
         */
        @Override
        public void onClick(View v) {

            cocktail.getCocktail().clear();
            TableLayout liste_boissons = (TableLayout) findViewById(R.id.liste_boissons_cocktails_choisis);
            liste_boissons.removeAllViews();

            if (affichage_boisson) {

                // reset des sélections
                for (int i  : id_affiche) {
                    SeekBar aChanger = (SeekBar) findViewById(i+1000);
                    aChanger.setProgress(0);
                    aChanger.setMax(verre);
                }

                quantite_boissons = 0;
            }
        }
    };

    View.OnClickListener click_btn_filtre = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // On enlève tous les éléments du layout vu qu'on va en afficher de nouveaux
            TableLayout layout_boissons = (TableLayout) findViewById(R.id.liste_boissons);
            layout_boissons.removeAllViews();

            switch (v.getId()) {
                case R.id.btn_filtre_alcool_fort:
                    if (leFiltre.equals("fort")) {
                        leFiltre = "tous";
                        btn_filtre_soft.setBackground(style_button_defaut);
                        btn_filtre_alcool_fort.setBackground(style_button_defaut);
                        btn_filtre_alcool_doux.setBackground(style_button_defaut);
                    } else {
                        leFiltre = "fort";
                        ShapeDrawable shapedrawable = new ShapeDrawable();
                        shapedrawable.setShape(new RectShape());
                        shapedrawable.getPaint().setColor(Color.RED);
                        shapedrawable.getPaint().setStrokeWidth(10f);
                        shapedrawable.getPaint().setStyle(Paint.Style.STROKE);
                        btn_filtre_alcool_fort.setBackground(shapedrawable);
                        btn_filtre_soft.setBackground(style_button_defaut);
                        btn_filtre_alcool_doux.setBackground(style_button_defaut);
                    }

                    affichage_boissons(data_boissons);
                    break;

                case R.id.btn_filtre_alcool_doux:
                    if (leFiltre.equals("doux")) {
                        leFiltre = "tous";
                        btn_filtre_soft.setBackground(style_button_defaut);
                        btn_filtre_alcool_fort.setBackground(style_button_defaut);
                        btn_filtre_alcool_doux.setBackground(style_button_defaut);
                    } else {
                        leFiltre = "doux";
                        ShapeDrawable shapedrawable = new ShapeDrawable();
                        shapedrawable.setShape(new RectShape());
                        shapedrawable.getPaint().setColor(Color.RED);
                        shapedrawable.getPaint().setStrokeWidth(10f);
                        shapedrawable.getPaint().setStyle(Paint.Style.STROKE);
                        btn_filtre_alcool_doux.setBackground(shapedrawable);
                        btn_filtre_soft.setBackground(style_button_defaut);
                        btn_filtre_alcool_fort.setBackground(style_button_defaut);
                    }

                    affichage_boissons(data_boissons);
                    break;
                case R.id.btn_filtre_soft:
                    if (leFiltre.equals("soft")) {
                        leFiltre = "tous";
                        btn_filtre_soft.setBackground(style_button_defaut);
                        btn_filtre_alcool_fort.setBackground(style_button_defaut);
                        btn_filtre_alcool_doux.setBackground(style_button_defaut);
                    } else {
                        leFiltre = "soft";
                        ShapeDrawable shapedrawable = new ShapeDrawable();
                        shapedrawable.setShape(new RectShape());
                        shapedrawable.getPaint().setColor(Color.RED);
                        shapedrawable.getPaint().setStrokeWidth(10f);
                        shapedrawable.getPaint().setStyle(Paint.Style.STROKE);
                        btn_filtre_soft.setBackground(shapedrawable);
                        btn_filtre_alcool_doux.setBackground(style_button_defaut);
                        btn_filtre_alcool_fort.setBackground(style_button_defaut);
                    }

                    affichage_boissons(data_boissons);

                    break;
            }
        }
    };

    View.OnClickListener click_cocktail_boisson = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            TableLayout layout_cocktails = (TableLayout) findViewById(R.id.cocktails);
            TableLayout layout_boissons = (TableLayout) findViewById(R.id.boissons);

            if (v.getId() == R.id.btn_boissons) {
                layout_boissons.setVisibility(View.VISIBLE);
                layout_cocktails.setVisibility(View.GONE);
                affichage_boisson = true;
            } else if (v.getId() == R.id.btn_cocktails) {
                layout_cocktails.setVisibility(View.VISIBLE);
                layout_boissons.setVisibility(View.GONE);
                affichage_boisson = false;
            }
        }
    };

    //endregion BOISSON

    //region COCKTAIL

    View.OnClickListener click_vignette = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast tst_vignette = Toast.makeText(getApplicationContext(), "Click sur le cocktail " + v.getId(), Toast.LENGTH_SHORT);
            tst_vignette.show();

            // réduire les vignettes déjà ouvertes
            TableLayout layout_cocktails = (TableLayout) findViewById(R.id.liste_cocktails);
            layout_cocktails.removeAllViews();
            affichage_cocktails(data_commandes, v.getId() - 1000);
        }
    };

    View.OnClickListener click_btn_commande = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // on crée l'objet Commande
            if (utilisateur_connecte != null && cocktail.getCocktail().size() > 0) {

                if (utilisateur_connecte.ptConso >= cocktail.getPrix()) {
                    getId();
                } else {
                    Toast pas_assez_dargent = Toast.makeText(getApplicationContext(), "Vous n'avez pas assez d'argent", Toast.LENGTH_LONG);
                    pas_assez_dargent.show();
                }
            } else {
                Toast rien_commande = Toast.makeText(getApplicationContext(), "Vous n'avez rien commandé / Utilisateur non connecté", Toast.LENGTH_LONG);
                rien_commande.show();
            }
        }
    };

    View.OnClickListener click_btn_filtre_cocktail = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_filtre_cocktails_autres_utilisateurs :
                    leFiltreCocktail = "autres";
                    break;

                case R.id.btn_filtre_cocktails_personnalises :
                    leFiltreCocktail = "perso";
                    break;

                case R.id.btn_filtre_cocktails_precomposes :
                    leFiltreCocktail = "commun";
                    break;
            }

            affichage_cocktails(data_commandes, -1);
        }
    };

    //endregion COCKTAIL

    //endregion GESTION EVENEMENT

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFirebase();

        leFiltre = "tous";
        leFiltreCocktail = "commun";
        verre = VERRE_DEFAUT;
        selectBoissons("nom");
        selectCommandes();
        btn_ajouter = (Button) findViewById(R.id.btn_ajouter);
        btn_ajouter.setClickable(false);
        btn_ajouter.setOnClickListener(btn_ajouter_click);

        Button verre_4 = (Button) findViewById(R.id.btn_verre_4);
        Button verre_15 = (Button) findViewById(R.id.btn_verre_15);
        Button verre_25 = (Button) findViewById(R.id.btn_verre_25);
        Button verre_50 = (Button) findViewById(R.id.btn_verre_50);
        Button verre_100 = (Button) findViewById(R.id.btn_verre_100);

        verre_4.setOnClickListener(click_btn_verre);
        verre_15.setOnClickListener(click_btn_verre);
        verre_25.setOnClickListener(click_btn_verre);
        verre_50.setOnClickListener(click_btn_verre);
        verre_100.setOnClickListener(click_btn_verre);

        verre_selectionne = (TextView) findViewById(R.id.texte_verre);
        verre_selectionne.setText(verre + "cl");

        btn_reset = (Button) findViewById(R.id.btn_reset);
        btn_reset.setOnClickListener(click_btn_reset);

        btn_boissons = (Button) findViewById(R.id.btn_boissons);
        btn_cocktails = (Button) findViewById(R.id.btn_cocktails);
        btn_boissons.setOnClickListener(click_cocktail_boisson);
        btn_cocktails.setOnClickListener(click_cocktail_boisson);

        TableLayout layout_cocktails = (TableLayout) findViewById(R.id.cocktails);
        layout_cocktails.setVisibility(View.GONE);

        btn_commande = (Button) findViewById(R.id.btn_commande);
        btn_commande.setOnClickListener(click_btn_commande);

        btn_filtre_alcool_fort = (Button) findViewById(R.id.btn_filtre_alcool_fort);
        btn_filtre_alcool_doux = (Button) findViewById(R.id.btn_filtre_alcool_doux);
        btn_filtre_soft = (Button) findViewById(R.id.btn_filtre_soft);

        btn_filtre_alcool_fort.setOnClickListener(click_btn_filtre);
        btn_filtre_alcool_doux.setOnClickListener(click_btn_filtre);
        btn_filtre_soft.setOnClickListener(click_btn_filtre);

        btn_filtre_commun = (Button) findViewById(R.id.btn_filtre_cocktails_precomposes);
        btn_filtre_perso = (Button) findViewById(R.id.btn_filtre_cocktails_personnalises);
        btn_filtre_autre = (Button) findViewById(R.id.btn_filtre_cocktails_autres_utilisateurs);

        btn_filtre_commun.setOnClickListener(click_btn_filtre_cocktail);
        btn_filtre_perso.setOnClickListener(click_btn_filtre_cocktail);
        btn_filtre_autre.setOnClickListener(click_btn_filtre_cocktail);

        //TODO récupérer l'identifiant à partir de la connexion NFC

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");
        }
        catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        //IntentFilter[] intentFiltersArray = new IntentFilter[]{ndef,};

        selectUtilisateur("06bca15d-3c10-4e12-8565-2757461b16cd");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        MifareUltralight mifare = MifareUltralight.get(tagFromIntent);
        try {
            mifare.connect();
            byte[] payload = mifare.readPages(1);
            Toast lala = Toast.makeText(getApplicationContext(), "NFC connecté... récupération de l'utilisateur", Toast.LENGTH_LONG);
            lala.show();
            selectUtilisateur(new String(payload, Charset.forName("US-ASCII")));
        } catch (IOException e) {
            Log.e(TAG, "IOException while writing MifareUltralight message...", e);
        } finally {
            if (mifare != null) {
                try {
                    mifare.close();
                }
                catch (IOException e) {
                    Log.e(TAG, "Error closing tag...", e);
                }
            }
        }

        // erreur de connexion
        utilisateur_connecte = null;
    }
}
