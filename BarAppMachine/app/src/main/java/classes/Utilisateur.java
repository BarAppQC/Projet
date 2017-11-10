package classes;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by leofa on 30/09/2017.
 */

public class Utilisateur {

    public String id; // Id de l'utilisateur
    public String mail; // Email de l'utilisateur
    public String nom; // Nom de l'utilisateur
    public String prenom; // Prénom de l'utilisateur
    public int ptFidelite; // Nombre de points de fidélité de l'utilisateur
    public int ptConso; // Nombre de points de consommations présents dans le portefeuille électronique de l'utilisateur



    /**
     * Constructeur vide
     */
    public Utilisateur() {
        super();
        this.id = "";
        this.mail = "";
        this.prenom = "";
        this.ptFidelite = 0;
        this.ptConso = 0;
    }

    /**
     * Création d'un utilisateur
     *
     * @param id
     * @param mail
     * @param nom
     * @param prenom
     * @param ptFidelite
     * @param ptConso
     */
    public Utilisateur(String id, String mail, String nom, String prenom, int ptFidelite, int ptConso) {
        super();
        this.id = id;
        this.nom = nom;
        this.mail = mail;
        this.prenom = prenom;
        this.ptFidelite = ptFidelite;
        this.ptConso = ptConso;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Utilisateur){
            return this.id == ((Utilisateur) o).id && this.mail.equals(((Utilisateur) o).mail) && this.nom.equals(((Utilisateur) o).nom) && this.prenom.equals(((Utilisateur) o).prenom) && this.ptConso==((Utilisateur) o).ptConso && this.ptFidelite==((Utilisateur) o).ptFidelite;
        }else{
            return false;
        }
    }

}
