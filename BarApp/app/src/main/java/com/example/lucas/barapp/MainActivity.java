package com.example.lucas.barapp;

import android.*;
import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import classes.Bar;
import classes.Boisson;
import classes.Evenement;
import classes.MifareUltralightTagTester;
import classes.Utilisateur;

import static com.facebook.Profile.getCurrentProfile;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{
    //region VARIABLES

    private static final String TAG = MifareUltralightTagTester.class.getSimpleName();

    //region LAYOUT

    /**
     * Vue permettant la connexion à facebook
     */
    View view_connexion;

    /**
     * Vue affichant les boissons
     */
    View view_boisson;

    /**
     * Vue contenant les évènements de l'accueil et le bouton permettant d'interagir avec
     * la deuxième application (celle de commande sur la machine)
     */
    View view_accueil;

    /**
     * Contenant les éléments nécessaires à l'ajout d'argent virtuel sur le compte Utilisateur
     */
    View view_paiement;

    /**
     * Contient le chat de la team et les défis personnalisés
     */
    View view_team;

    /**
     * Vue principale accueillant les 4 vues citées ci-dessus ainsi que le menu en bas et le
     * bandeau supérieur donnant des informations sur l'utilisateur
     */
    FrameLayout layout_main;
    //endregion LAYOUT

    //region FIREBASE

    /**
     * La base de données de BarApp
     */
    FirebaseDatabase database;

    /**
     * La référence à la base de données BarApp
     */
    DatabaseReference myRef;

    /**
     * La référence à la partie Storage de la base de données où sont stockées les images
     */
    StorageReference mStorageRef;

    /**
     * Le storage de la base de données où sont stockées les images
     */
    FirebaseStorage storage;

    //endregion FIREBASE

    //region UTILISATEUR
    Utilisateur utilisateur;
    //endregion UTILISATEUR

    //region BOISSON
    /**
     * La référence de la boisson dans firebase
     */
    StorageReference boissonRef;

    /**
     * Contiendra toutes les boissons stockées en base
     * Triées par nom de base
     * Fontionne en duo avec images
     */
    ArrayList<Boisson> data_boissons = new ArrayList<Boisson>();

    /**
     * Le tableau qui contient toutes les images des boissons
     * Fonctionne en duo avec data_boissons
     */
    Drawable[] images;

    /**
     * Le filtre appliqué aux boissons (fort pour les alcools fort, même chose pour doux ou soft)
     */
    String leFiltre = "";

    /**
     * Indique si la vignette d'indice i est étendue (true) ou non (false)
     */
    boolean[] etendue;

    /**
     * Le fichier local où est stocké l'image après réussite de l'extraction de la base
     */
    File localFile;

    /**
     * L'image de la vignette juste après extraction
     */
    Drawable imageVignette;

    /**
     * Afin de récupérer le style de bouton "normal" après modification
     */
    Drawable style_button_defaut;
    //endregion BOISSON

    //region CONNEXION

    /**
     *
     */
    private CallbackManager callbackManager;

    private TextView connexion_info;
    private LoginButton connexion_loginButton;
    private ProfilePictureView connexion_photo;
    private ArrayList<Utilisateur> connexion_listeUtilisateurs;
    private Button connexion_bt_connexion;
    private TextView connexion_tvw_Nom;
    private TextView connexion_tvw_Prenom;
    private TextView connexion_tvw_Mail;
    private View connexion_layout_donnees_fb;
    private TextView connexion_txt_erreur;
    //endregion CONNEXION

    //region PAIEMENT
    private TextView pointArgent;
    private TextView nom_prenom;
    private TextView point_fidelite;
    private int moneyInt;
    private ImageView lienPlus;

    private TextView valeur5;
    private TextView valeur10;
    private TextView valeur20;
    private TextView valeur50;
    private Button cinqDollar;
    private Button dixDollar;
    private Button vingtDollar;
    private Button cinquanteDollar;
    private Button validerPaiement;
    private EditText mEdit;
    Toast toast_trop_argent;

    //endregion PAIEMENT

    //region EVENEMENT

    //endregion EVENEMENT

    //region TEAM
    private GoogleMap map;
    private double longitude = 0D;
    private double latitude = 0D;
    private LatLng p0 = new LatLng(0D, 0D);

    /**
     * Permet la récupération des bars ajoutés en base de données
     */
    Bar bar;

    //endregion TEAM

    //region ACCUEIL
    private Button accueil_bt_deconnexion;
    private TextView accueil_txt_nomprenom;
    //endregion ACCEUIL

    //endregion VARIABLES

    //region DONNEES

    //region BOISSON

    /**
     * Permet d'insérer la boisson passée en paramètre en base de données
     *
     * @param b : la boisson à insérer
     * @throws Exception : en cas d'erreur de création de boisson ou d'accès en base
     */
    public void insertBoisson(Boisson b) throws Exception {
        myRef.child("boissons").child(String.valueOf(b.getId())).setValue(b);
    }

    /**
     * Supprime la boisson passée en paramètre dans la base de données
     *
     * @param b : la boisson à supprimer
     */
    public void deleteBoisson(Boisson b) {
        myRef.child("boissons").child(String.valueOf(b.getId())).setValue(null);
    }

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
                    b = new Boisson(((Long) messageSnapshot.child("id").getValue()).intValue(), (String) messageSnapshot.child("nom").getValue(), (String) messageSnapshot.child("description_courte").getValue(), (String) messageSnapshot.child("description_longue").getValue(), (Double) Double.parseDouble(messageSnapshot.child("taux_alcoolemie").getValue().toString()), ((String) messageSnapshot.child("type").getValue()), ((String) messageSnapshot.child("sous_type").getValue()), (Double) Double.parseDouble(messageSnapshot.child("prix").getValue().toString()));
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
     *
     * @param images : le tableau d'images de boisson à remplir à partir du storage de firebase
     *               L'image d'une boisson porte le même nom et est au format jpg
     * @param index  : l'index de l'élément courant à insérer. S'il est égal à la taille du tableau,
     *               le chargement est terminé et on lance l'affichage
     */
    public void selectImage(final Drawable[] images, final int index, final ArrayList<Boisson> ref) {

        // Une fois que toutes les images sont chargées, on procède à leur affichage
        if (images.length == index) {
            affichage_boissons(data_boissons);
        } else {
            // sinon, on continue de les charger à partir de la base de données (mStorageRef)
            localFile = null;
            mStorageRef = storage.getReference();
            boissonRef = mStorageRef.child("photosboissons/" + ref.get(index).getNom() + ".jpg");

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
                                } while (imageVignette == null);

                                data_boissons.get(index).setImage(imageVignette);
                                images[index] = imageVignette;
                                // Appel de la même fonction avec le tableau alimenté et l'index + 1
                                selectImage(images, index + 1, ref);

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

    //endregion BOISSON

    //region UTILISATEUR


    /**
     * Insert l'utilisateur passé en paramètre
     *
     * @param u : l'utilisateur à insérer
     * @throws Exception : en cas d'erreur d'insertion ou d'accès en base
     */
    public void insertUtilisateur(Utilisateur u) throws Exception {
        myRef.child("users").child(String.valueOf(u.id)).setValue(u);
    }

    /**
     * Supprime l'utilisateur passé en paramètre
     *
     * @param u : l'utilisateur à supprimer
     */
    public void deleteUtilisateur(Utilisateur u) {
        myRef.child("users").child(String.valueOf(u.id)).setValue(null);
    }

    /**
     * Sélectionne un utilisateur grâce à l'identifiant unique passé en paramètre
     *
     * @param id : l'identifiant unique de l'utilisateur à sélectionner
     */
    public void selectUtilisateur(String id) {

        //sorting and searching
        final DatabaseReference myRef = database.getReference("users");
        Query query = myRef.orderByChild("id").equalTo(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    utilisateur = new Utilisateur((String) messageSnapshot.child("id").getValue(), (String) messageSnapshot.child("mail").getValue(), (String) messageSnapshot.child("nom").getValue(), (String) messageSnapshot.child("prenom").getValue(), ((Long) messageSnapshot.child("ptFidelite").getValue()).intValue(), ((Long) messageSnapshot.child("ptConso").getValue()).intValue());
                }

                pointArgent = (TextView) findViewById(R.id.valeurPointArgent);
                pointArgent.setText(String.valueOf(utilisateur.ptConso));

                nom_prenom = (TextView) findViewById(R.id.nomUtilisateur);
                nom_prenom.setText(utilisateur.prenom + " " + utilisateur.nom);

                point_fidelite = (TextView) findViewById(R.id.valeurPointFidelite);
                point_fidelite.setText(String.valueOf(utilisateur.ptFidelite));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "select cancelled", Toast.LENGTH_LONG);
            }
        });
    }

    /**
     * Récupération des informations d'un profile Facebook dans un bundle
     *
     * @param object le résultat de la requête Facebook sous forme d'objet JSON
     * @return un Bundle contenant les données contenues dans object
     */
    private Bundle getFacebookData(JSONObject object) {

        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));

            utilisateur = new Utilisateur(bundle.get("idFacebook").toString(), bundle.get("email").toString(), bundle.get("last_name").toString(), bundle.get("first_name").toString(), 0, 0);
            insertUtilisateur(utilisateur);
            return bundle;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //endregion UTILISATEUR

    //region PAIEMENT

    //endregion PAIEMENT

    //region EVENEMENT

    //endregion EVENEMENT

    //region TEAM

    //endregion TEAM

    //endregion DONNEES

    //region FONCTIONS

    /**
     * Ajoute la vue passée en paramètre sur le layout principal :
     * le FrameLayout layout_main
     *
     * @param aAjouter : la vue à ajouter à l'activité
     */
    public void addView(View aAjouter) {
        aAjouter.setPadding(10, 140, 10, 100);
        layout_main.addView(aAjouter);
    }

    /**
     * Cache toutes les vues sauf celle à afficher
     * @param aMontrer la vue à afficher
     */
    public void showView(View aMontrer) {
        {
            view_connexion.setVisibility(View.GONE);
            view_accueil.setVisibility(View.GONE);
            view_boisson.setVisibility(View.GONE);
            view_paiement.setVisibility(View.GONE);
            view_team.setVisibility(View.GONE);

         //   if (utilisateur == null || aMontrer == view_connexion) {
            if ( aMontrer == view_connexion) {
                view_connexion.setVisibility(View.VISIBLE);
            } else if (aMontrer == view_accueil) {
                view_accueil.setVisibility(View.VISIBLE);
            } else if (aMontrer == view_boisson) {
                view_boisson.setVisibility(View.VISIBLE);
            } else if (aMontrer == view_paiement) {
                view_paiement.setVisibility(View.VISIBLE);
            } else if (aMontrer == view_team) {
                view_team.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Initialise la base de données Firebase
     */
    public void initFirebase() {

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        storage = FirebaseStorage.getInstance();
    }

    /**
     * Initialisation des composants présents dans la vue d'accueil
     * Ajout des listeners
     */
    private void initializeViewAccueil() {
        accueil_bt_deconnexion = (Button) view_accueil.findViewById(R.id.accueil_bt_connexion);
        accueil_txt_nomprenom = (TextView) view_accueil.findViewById(R.id.accueil_txt_nomprenom);
        accueil_bt_deconnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                connexion_layout_donnees_fb.setVisibility(View.GONE);
                showView(view_connexion);
            }
        });

        final ArrayList<Evenement> listeEvenements = new ArrayList<Evenement>();
        DatabaseReference myRef = database.getReference("evenements");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("DD-MM-YYYY");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    listeEvenements.clear();
                    //   Evenement ev= new Evenement(ds.child("id").getValue().toString(),ds.child("titre").getValue().toString(),ds.child("descriptionCourte").getValue().toString(),ds.child("descriptionLongue").getValue().toString(),ds.child("image").getValue().toString(), dateFormat.parse(ds.child("date").getValue().toString()), selectBar( ds.child("lieu").getValue().toString()));
                    //   listeEvenements.add(ev);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Initialisation des composants présents dans la vue de connexion
     * Ajout des listeners
     */
    private void initializeViewConnexion() {
        // Récupération des composants
        connexion_layout_donnees_fb = view_connexion.findViewById(R.id.layout_donnees_facebook);
        connexion_info = (TextView) view_connexion.findViewById(R.id.tvw_Nom);
        connexion_loginButton = (LoginButton) view_connexion.findViewById(R.id.connexion_login_button);
        connexion_photo = (ProfilePictureView) view_connexion.findViewById(R.id.connexion_photo);
        connexion_tvw_Nom = (TextView) view_connexion.findViewById(R.id.tvw_Nom);
        connexion_tvw_Prenom = (TextView) view_connexion.findViewById(R.id.tvw_Prenom);
        connexion_tvw_Mail = (TextView) view_connexion.findViewById(R.id.tvw_Mail);
        connexion_bt_connexion = (Button) view_connexion.findViewById(R.id.connexion_bt_connexion);
        connexion_txt_erreur = (TextView) view_connexion.findViewById(R.id.connexion_txt_erreur);

        // Remise à vide des champs d'information sur le profil connecté
        connexion_tvw_Nom.setText("");
        connexion_tvw_Mail.setText("");
        connexion_tvw_Prenom.setText("");
        connexion_photo.setProfileId("");

        // Evènements
        connexion_bt_connexion.setOnClickListener(new View.OnClickListener() {

            /**
             * Click sur le bouton de connexion qui permet de passer de la Connexion à la vue Accueil une fois que la connexion Facebook est effectuée
             * Si la connexion est effectuée, affichage de la vue Accueil, sinon affichage d'un message d'erreur
             * @param v
             */
            @Override
            public void onClick(View v) {
                Profile user = Profile.getCurrentProfile();
                selectUtilisateur("06bca15d-3c10-4e12-8565-2757461b16cd");
                if (user != null) {
                    selectUtilisateur(user.getId());
                    connexion_txt_erreur.setVisibility(View.GONE);
                    accueil_txt_nomprenom.setText(utilisateur.nom + " " + utilisateur.prenom);
                    connexion_layout_donnees_fb.setVisibility(View.GONE);
                    showView(view_accueil);
                } else {
                    connexion_txt_erreur.setVisibility(View.VISIBLE);
                }
            }
        });

        // Paramétrage du bouton de login Facebook
        connexion_loginButton.setReadPermissions(Arrays.asList(  // On demande les autorisations de base pou obtenir nom prénom et photo de profil,
                "public_profile", "email"));                // plus une autorisation pour avoir accès à l'adresse mail de l'utilisateur

        // Ajout d'un évènement sur le retour fait par la connexion Facebook
        connexion_loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            /**
             * Cas de succés :  Envoi d'une requête à Facebook pour la récupération des données du profil
             *                  Affichage du nom, prénom, adresse mail et photo de profil de l'utilisateur connecté
             * @param loginResult
             */
            @Override
            public void onSuccess(LoginResult loginResult) {
                // Récupération du profil courant
                Profile profile = getCurrentProfile();

                // Affichage de la photo de profil et du nom de l'utilisateur connecté
                connexion_photo.setProfileId(loginResult.getAccessToken().getUserId());
                connexion_info.setText(profile.getName());

                // Création d'un requête Facebook pour obtenir plus d'informations sur le profil : le mail, le nom et le prénom
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    /**
                     * Evènement appelé après la réussite de la communication avec Facebook et le retour du résultat de la requête
                     * @param object objet JSON contenant le résultat de la requête
                     * @param response la requête
                     */
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        getFacebookData(object); // assignation du résultat de la requête dans l'objet utilisateur
                    }
                });
                // Création d'un Bundle accueillant les paramètres de la requête Facebook
                Bundle parameters = new Bundle();
                parameters.putString("fields", " email,gender, birthday, location"); // Paramètres demandés à Facebook
                request.setParameters(parameters);
                request.executeAsync();

                // Affichage des données du profil
                connexion_tvw_Mail.setText(utilisateur.mail);
                connexion_tvw_Nom.setText(utilisateur.nom);
                connexion_tvw_Prenom.setText(utilisateur.prenom);

                connexion_layout_donnees_fb.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {
                e.printStackTrace();
            }

        });
    }

    private void initializeViewPaiement(){
        pointArgent = (TextView) findViewById(R.id.valeurPointArgent);
        valeur5 = (TextView) view_paiement.findViewById(R.id.valeur5);
        valeur10 = (TextView) view_paiement.findViewById(R.id.valeur10);
        valeur20= (TextView) view_paiement.findViewById(R.id.valeur20);
        valeur50 = (TextView) view_paiement.findViewById(R.id.valeur50);
        cinqDollar = (Button) view_paiement.findViewById(R.id.paiement5);
        dixDollar =  (Button) view_paiement.findViewById(R.id.paiement10);
        vingtDollar =  (Button) view_paiement.findViewById(R.id.paiement20);
        cinquanteDollar =  (Button) view_paiement.findViewById(R.id.paiement50);
        validerPaiement= (Button) view_paiement.findViewById(R.id.paiement_valider);
        mEdit = (EditText) view_paiement.findViewById(R.id.paiement_perso);
        lienPlus = (ImageView) findViewById(R.id.plusPointArgent);

        selectUtilisateur("06bca15d-3c10-4e12-8565-2757461b16cd");

        cinqDollar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                moneyInt = utilisateur.ptConso+ 5*137;
                if (moneyInt < 0) {
                    toast_trop_argent.show();
                } else {
                    pointArgent.setText(String.valueOf(moneyInt));
                    try {
                        utilisateur.ptConso=moneyInt;
                        insertUtilisateur(utilisateur);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        dixDollar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                moneyInt = utilisateur.ptConso + (int) (10*137*1.05);
                if (moneyInt < 0) {
                    toast_trop_argent.show();
                } else {
                    pointArgent.setText(String.valueOf(moneyInt));
                    try {
                        utilisateur.ptConso = moneyInt;
                        insertUtilisateur(utilisateur);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        vingtDollar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // bonus de 10% sur la monnaie ajouté
                moneyInt = utilisateur.ptConso + (int) (20*137*1.1);
                if (moneyInt < 0) {
                    toast_trop_argent.show();
                } else {
                    pointArgent.setText(String.valueOf(moneyInt));
                    utilisateur.ptConso = moneyInt;
                    try {
                        insertUtilisateur(utilisateur);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        cinquanteDollar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // bonus de 15% sur la monnaie ajouté
                moneyInt = utilisateur.ptConso + (int)(50*137*1.15);
                if (moneyInt < 0) {
                    toast_trop_argent.show();
                } else {
                    pointArgent.setText(String.valueOf(moneyInt));
                    utilisateur.ptConso = moneyInt;
                    try {
                        insertUtilisateur(utilisateur);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // Bouton de validation d'ajout personnalisé
        validerPaiement.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                String somme = mEdit.getText().toString();
                if (!somme.equals("")){
                    int mEditInt = Integer.parseInt(somme);
                    // bonus de 15% sur la monnaie ajouté si la somme est supérieur à 50$
                    if (mEditInt >= 50){
                        moneyInt = utilisateur.ptConso + (int)( mEditInt*137*1.15);
                        if (moneyInt < 0) {
                            toast_trop_argent.show();
                        } else {
                            try {
                                utilisateur.ptConso = moneyInt;
                                insertUtilisateur(utilisateur);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (mEditInt >= 0) {
                        moneyInt = utilisateur.ptConso + mEditInt*137;
                        if (moneyInt < 0) {
                            toast_trop_argent.show();
                        } else {
                            try {
                                utilisateur.ptConso = moneyInt;
                                insertUtilisateur(utilisateur);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Toast lala = Toast.makeText(getApplicationContext(), "Veuillez entrer une valeur positive", Toast.LENGTH_LONG);
                        lala.show();
                    }
                    pointArgent.setText(String.valueOf(moneyInt));
                    mEdit.setText(null);
                }
            }
        });

        // lien de l'image PLUS vers la page paiement
        lienPlus.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
              showView(view_paiement);
            }

        });

        // Affichage des points d'argents gagnés
        valeur5.setText(String.valueOf(5*137)+" pt d'argent");
        valeur10.setText(String.valueOf((int) (10*137*1.05))+" pt d'argent");
        valeur20.setText(String.valueOf((int) (20*137*1.1))+" pt d'argent");
        valeur50.setText(String.valueOf((int) (50*137*1.15))+" pt d'argent");
    }

    //region BOISSON

    /**
     * Affiche toutes les boissons avec :
     * - une image représentative
     * - un titre et une description
     * - leur degré d'alcool
     *
     * @param boissons : les boissons à afficher
     */
    public void affichage_boissons(ArrayList<Boisson> boissons) {
        TableLayout layout_boissons = (TableLayout) findViewById(R.id.liste_boissons);

        etendue = new boolean[boissons.size()];
        int i = 0;

        // Pour chaque boisson, on crée une nouvelle vignette à insérer
        for (Boisson boisson : boissons) {
            if (leFiltre.equals("tous")) {
                // regroupe image et description
                TableRow vignette = new TableRow(this);
                vignette.setId(i);
                etendue[i] = false;
                //vignette.setBackgroundDrawable(getResources().getDrawable(R.drawable.vignette));
                vignette.setOnClickListener(vignette_listener);

                // image
                ImageView image = new ImageView(this);
                image.setId(10000 + i);

                Bitmap bitmap = ((BitmapDrawable) boisson.getImage()).getBitmap();
                Drawable image_reduite = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));

                image.setBackgroundDrawable(image_reduite);

                // description
                TextView description = new TextView(this);
                description.setId(100 + i);
                description.setText(boisson.getTitreCourt());
                description.setTextSize(16);

                // taux d'alcolémie centré à droite
                TextView taux_alcool = new TextView(this);
                taux_alcool.setId(500 + i);
                taux_alcool.setText(String.valueOf(boisson.getTaux_alcoolemie()) + "°\n" + String.valueOf(boisson.getPrix()));
                taux_alcool.setTextSize(16);
                taux_alcool.setGravity(Gravity.RIGHT);
                taux_alcool.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_piece, 0);

                vignette.addView(image);
                vignette.addView(description);
                vignette.addView(taux_alcool);

                layout_boissons.addView(vignette);
            } else if (leFiltre.equals("fort") && boisson.isAlcoolFort()) {
                // regroupe image et description
                TableRow vignette = new TableRow(this);
                vignette.setId(i);
                etendue[i] = false;
                //vignette.setBackgroundDrawable(getResources().getDrawable(R.drawable.vignette));
                vignette.setOnClickListener(vignette_listener);

                // image
                ImageView image = new ImageView(this);
                image.setId(10000 + i);
                Bitmap bitmap = ((BitmapDrawable) boisson.getImage()).getBitmap();
                Drawable image_reduite = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));

                image.setBackgroundDrawable(image_reduite);

                // description
                TextView description = new TextView(this);
                description.setId(100 + i);
                description.setText(boisson.getTitreCourt());
                description.setTextSize(16);

                // taux d'alcolémie centré à droite
                TextView taux_alcool = new TextView(this);
                taux_alcool.setId(500 + i);
                taux_alcool.setText(String.valueOf(boisson.getTaux_alcoolemie()) + "°\n" + String.valueOf(boisson.getPrix()));
                taux_alcool.setTextSize(16);
                taux_alcool.setGravity(Gravity.RIGHT);
                taux_alcool.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_piece, 0);

                vignette.addView(image);
                vignette.addView(description);
                vignette.addView(taux_alcool);

                layout_boissons.addView(vignette);
            } else if (leFiltre.equals("doux") && boisson.isAlcoolDoux()) {
                // regroupe image et description
                TableRow vignette = new TableRow(this);
                vignette.setId(i);
                etendue[i] = false;
                //vignette.setBackgroundDrawable(getResources().getDrawable(R.drawable.vignette));
                vignette.setOnClickListener(vignette_listener);

                // image
                ImageView image = new ImageView(this);
                image.setId(10000 + i);
                Bitmap bitmap = ((BitmapDrawable) boisson.getImage()).getBitmap();
                Drawable image_reduite = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));

                image.setBackgroundDrawable(image_reduite);

                // description
                TextView description = new TextView(this);
                description.setId(100 + i);
                description.setText(boisson.getTitreCourt());
                description.setTextSize(16);

                // taux d'alcolémie centré à droite
                TextView taux_alcool = new TextView(this);
                taux_alcool.setId(500 + i);
                taux_alcool.setText(String.valueOf(boisson.getTaux_alcoolemie()) + "°\n" + String.valueOf(boisson.getPrix()));
                taux_alcool.setTextSize(16);
                taux_alcool.setGravity(Gravity.RIGHT);
                taux_alcool.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_piece, 0);

                vignette.addView(image);
                vignette.addView(description);
                vignette.addView(taux_alcool);

                layout_boissons.addView(vignette);
            } else if (leFiltre.equals("soft") && boisson.isSoft()) {
                // regroupe image et description
                TableRow vignette = new TableRow(this);
                vignette.setId(i);
                etendue[i] = false;
                //vignette.setBackgroundDrawable(getResources().getDrawable(R.drawable.vignette));
                vignette.setOnClickListener(vignette_listener);

                // image
                ImageView image = new ImageView(this);
                image.setId(10000 + i);
                Bitmap bitmap = ((BitmapDrawable) boisson.getImage()).getBitmap();
                Drawable image_reduite = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));

                image.setBackgroundDrawable(image_reduite);

                // description
                TextView description = new TextView(this);
                description.setId(100 + i);
                description.setText(boisson.getTitreCourt());
                description.setTextSize(16);

                // taux d'alcolémie centré à droite
                TextView taux_alcool = new TextView(this);
                taux_alcool.setId(500 + i);
                taux_alcool.setText(String.valueOf(boisson.getTaux_alcoolemie()) + "°\n" + String.valueOf(boisson.getPrix()));
                taux_alcool.setTextSize(16);
                taux_alcool.setGravity(Gravity.RIGHT);
                taux_alcool.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_piece, 0);

                vignette.addView(image);
                vignette.addView(description);
                vignette.addView(taux_alcool);

                layout_boissons.addView(vignette);
            }
            i++;

        }
    }

    //endregion BOISSON

    //region UTILISATEUR

    //endregion UTILISATEUR

    //region PAIEMENT

    //endregion PAIEMENT

    //region EVENEMENT

    //endregion EVENEMENT

    //region TEAM

    //endregion TEAM

    //endregion FONCTIONS

    //region GESTION EVENEMENTS

    //region MENU

    /**
     * Appelé en cas de clic sur le menu
     * Elle servira pour afficher ou masquer des éléments, aller chercher des informations en bdd
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // Selon sur quel onglet on a cliqué, on fait apparaitre les contenus
            switch (item.getItemId()) {
                case R.id.navigation_accueil:
                    showView(view_accueil);
                    return true;
                case R.id.navigation_boisson:
                    showView(view_boisson);
                    return true;
                case R.id.navigation_paiement:
                    showView(view_paiement);
                    return true;
                case R.id.navigation_team:
                    showView(view_team);
                    return true;
            }
            return false;
        }

    };

    //endregion MENU

    //region BOISSON

    /**
     * En cas de clic sur une vignette, on réduit la vignette qui est étendue s'il y en a une
     * puis on étend celle où l'utilisateur a cliqué :
     * - On affiche la description longue de l'objet
     * - On agrandit l'image de présentation
     * La réduction consiste à mettre l'image sous sa taille initiale et d'afficher la description
     * courte
     */
    View.OnClickListener vignette_listener = new View.OnClickListener() {
        public void onClick(View v) {

            // on stocke la ligne qu'on a réduit, il doit y en avoir qu'une
            int reduite = -1;

            // s'il y a une zone étendue, on la réduit
            for (int i = 0; i < etendue.length; i++) {
                if (etendue[i]) {
                    TextView texteAReduire = (TextView) findViewById(100 + i);
                    ImageView imageAReduire = (ImageView) findViewById(10000 + i);
                    Bitmap bitmap = ((BitmapDrawable) data_boissons.get(i).getImage()).getBitmap();
                    Drawable image_reduite = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));

                    imageAReduire.setBackgroundDrawable(image_reduite);
                    //imageAReduire.setImageDrawable(data_boissons.get(i).getImage()); // images
                    texteAReduire.setText(data_boissons.get(i).getTitreCourt()); // data_boissons
                    etendue[i] = false;
                    reduite = i;
                }
            }

            if (reduite != v.getId()) {
                TextView texteAEtendre = (TextView) findViewById(100 + v.getId());
                texteAEtendre.setWidth(430);
                texteAEtendre.setText(data_boissons.get(v.getId()).getTitreLong()); // data_boissons
                ImageView imageAEtendre = (ImageView) findViewById(10000 + v.getId());
                imageAEtendre.setBackgroundDrawable(data_boissons.get(v.getId()).getImage());
                //ImageView imageAEtendre = (ImageView) findViewById(10000 + v.getId());

                /*Drawable dr = images[v.getId()];
                Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
                Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 200, 200, true));
                imageAEtendre.setImageDrawable(images[v.getId()]);*/
                etendue[v.getId()] = true;
            }
        }

    };

    /**
     * Gère le click sur un des filtre
     */
    View.OnClickListener filtre = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Pour gérer l'affichage des boutons (sélectionné ou non)
            Button btn_filtre_alcool_fort = (Button) findViewById(R.id.btn_filtre_alcool_fort);
            Button btn_filtre_alcool_doux = (Button) findViewById(R.id.btn_filtre_alcool_doux);
            Button btn_filtre_soft = (Button) findViewById(R.id.btn_filtre_soft);

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

    /**
     * Gère le click sur un tri
     */
    View.OnClickListener tri = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button tri = (Button) findViewById(v.getId());

            // Boucle Prix -> Nom -> Popularité -> Prix
            TableLayout layout_boissons = (TableLayout) findViewById(R.id.liste_boissons);
            layout_boissons.removeAllViews();

            if (tri.getText().equals("Prix")) {
                // après prix, on fait le nom
                tri.setText("Nom");
                for (Boisson b : data_boissons) {
                    b.setTri("nom");
                }

                Collections.sort(data_boissons, new Boisson());
                affichage_boissons(data_boissons);
            } else if ((tri.getText().equals("Nom"))) {
                // après nom on fait popularité
                tri.setText("Puissance");
                for (Boisson b : data_boissons) {
                    b.setTri("puissance");
                }
                Collections.sort(data_boissons, new Boisson());
                affichage_boissons(data_boissons);
            } else if ((tri.getText().equals("Puissance"))) {
                // après popularité, on fait prix
                tri.setText("Prix");

                for (Boisson b : data_boissons) {
                    b.setTri("prix");
                }
                Collections.sort(data_boissons, new Boisson());
                affichage_boissons(data_boissons);
            }
        }
    };

    //endregion BOISSON

    //region UTILISATEUR

    //endregion UTILISATEUR

    //region PAIEMENT

    //endregion PAIEMENT

    //region EVENEMENT

    //endregion EVENEMENT

    //region TEAM

    //endregion TEAM

    //endregion GESTION EVENEMENTS

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //region LE NEXUS
        super.onCreate(savedInstanceState);
       // FirebaseApp.initializeApp(getApplicationContext());
       // FacebookSdk.setApplicationId("400722533677087");
       // FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_main);
        initFirebase();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //endregion LE NEXUS

        //region AJOUT DES VUES AU LAYOUT PRINCIPAL
        // Déclaration des vues et du layout
        layout_main = (FrameLayout) findViewById(R.id.layout_main);
        view_boisson = getLayoutInflater().inflate(R.layout.boisson, null);
        view_accueil = getLayoutInflater().inflate(R.layout.accueil, null);
        view_paiement = getLayoutInflater().inflate(R.layout.paiement, null);
        view_team = getLayoutInflater().inflate(R.layout.team, null);
        view_connexion = getLayoutInflater().inflate(R.layout.connexion, null);

        // Ajouts des view au layout
        addView(view_boisson);
        addView(view_accueil);
        addView(view_paiement);
        addView(view_team);
        addView(view_connexion);

        // On affiche seulement l'accueil en premier
        view_boisson.setVisibility(View.GONE);
        view_accueil.setVisibility(View.VISIBLE);
        view_paiement.setVisibility(View.GONE);
        view_team.setVisibility(View.GONE);
        view_connexion.setVisibility(View.GONE);
        //endregion AJOUT DES VUES AU LAYOUT PRINCIPAL

        //region BOISSON
        // Boutons de filtre
        Button btn_filtre_alcool_fort = (Button) findViewById(R.id.btn_filtre_alcool_fort);
        Button btn_filtre_alcool_doux = (Button) findViewById(R.id.btn_filtre_alcool_doux);
        Button btn_filtre_soft = (Button) findViewById(R.id.btn_filtre_soft);
        Button btn_tri = (Button) findViewById(R.id.btn_tri);
        style_button_defaut = btn_filtre_alcool_fort.getBackground();

        btn_filtre_alcool_fort.setOnClickListener(filtre);
        btn_filtre_alcool_doux.setOnClickListener(filtre);
        btn_filtre_soft.setOnClickListener(filtre);
        btn_tri.setOnClickListener(tri);

        // on fait appel à cette fonction ici afin de minimiser les temps de chargement au sein
        // de l'application
        leFiltre = "tous";
        selectBoissons("prix");
        //endregion BOISSON

        // initialisation des vues
        //initializeViewConnexion();
        initializeViewAccueil();
        initializeViewPaiement();

        /*
        //region CONNEXION
        // Si aucun utilisateur n'est connecté, on affiche la fenêtre de connexion
        Profile user = Profile.getCurrentProfile();
        if (user == null) {
            showView(view_connexion);
        } else {
            selectUtilisateur(user.getId());
            //   accueil_txt_nomprenom.setText(utilisateur.nom+""+utilisateur.prenom);
        }
        //endregion CONNEXION*/

        toast_trop_argent = Toast.makeText(getApplicationContext(), "Vous ne pouvez pas mettre plus d'argent...", Toast.LENGTH_SHORT);

        // Affichage de la map sur la page d'accueil
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Acceptation pour utiliser le gps
        try {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long) 2000, (float) 10, locationListener);
        } catch (SecurityException e) {

        }
    }

        //Calcul de la position de l'utilisateur
        final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                p0 = new LatLng(latitude, longitude);
                map.addMarker(new MarkerOptions().position(p0).title("Vous êtes ici"));
                map.moveCamera(CameraUpdateFactory.newLatLng(p0));
                // Zoom sur la position au lancement de la carte
                float zoomLevel = 16.0f;
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(p0, zoomLevel));
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };


    public void onNewIntent(Intent intent) {
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        // écriture... normalement
        MifareUltralight ultralight = MifareUltralight.get(tagFromIntent);
        try {
            ultralight.connect();
            // l'identifiant est entré en dur
            ultralight.writePage(1, "06bca15d-3c10-4e12-8565-2757461b16cd".getBytes(Charset.forName("US-ASCII")));
            Toast lala = Toast.makeText(getApplicationContext(), "NFC connecté... envoi", Toast.LENGTH_LONG);
            lala.show();
        } catch (IOException e) {
            Log.e(TAG, "IOException while closing MifareUltralight...", e);
        } finally {
            try {
                ultralight.close();
            } catch (IOException e) {
                Log.e(TAG, "IOException while closing MifareUltralight...", e);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    // Permet l'affichage de la positon du téléphone et des bars
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //sorting and searching
        final DatabaseReference myRef = database.getReference("bars");
        Query query = myRef.orderByChild("id");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    bar = new Bar((Long) messageSnapshot.child("id").getValue(), (String) messageSnapshot.child("nom").getValue(), (String) messageSnapshot.child("adresse").getValue(), (Double) messageSnapshot.child("latitude").getValue(), (Double) messageSnapshot.child("longitude").getValue());
                    LatLng bar1 = new LatLng(bar.latitude,bar.longitude );
                    //affichage d'un marqueur avec nom et adresse du bar
                    map.addMarker(new MarkerOptions().position(bar1).title(bar.nom).snippet(bar.adresse));
                    map.moveCamera(CameraUpdateFactory.newLatLng(bar1));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "select cancelled", Toast.LENGTH_LONG);
            }
        });
        map = googleMap;
    }

}
