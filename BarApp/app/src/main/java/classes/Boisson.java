package classes;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;

import java.util.Comparator;

/**
 * Représente une boisson, elle sera composée de :
 *  - un identifiant unique pour pouvoir distinguer deux boissons du même nom
 *  - un nom
 *  - une description courte à afficher en premier
 *  - une description longue à afficher si l'utilisateur souuhaite en savoir plus
 *  - un taux d'alcoolémie
 *  - un type : bière, whisky, vodka, soft, etc.
 *  - un sous type : couleur de bière/rhum, bourbon/skotch, gazeux/plat, etc.
 *  - d'une image afin d'illustrer son contenu
 * Created by Lucas on 22/10/2017.
 */

public class Boisson implements Comparator<Boisson> {

    //region PROPRIETES
    /**
     * Identifiant unique de la boisson
     */
    private int id;

    /**
     * Le nom de la boisson : Heineken, Smirnoff, Jack Daniel's
     */
    private String nom;

    /**
     * Description courte donnant les informations essentielles
     */
    private String description_courte;

    /**
     * Description détaillée donnant plus d'informations
     */
    private String description_longue;

    /**
     * Degré d'alcoolémie
     * On pourra faire la distinction entre alcool (taux > 0) et soft (taux = 0)
     */
    private double taux_alcoolemie;

    /**
     * Donne un ordre d'idée de la boisson (whisky, vodka, etc.)
     */
    private String type;

    /**
     * Précise le type de boisson
     */
    private String sous_type;

    /**
     * Illustre le contenu de la boisson
     */
    private Drawable image;

    /**
     * Indique l'élément sur lequel on tri (nom, prix, puissance)
     */
    private String tri;

    /**
     * Prix de la boisson courante
     */
    private Double prix;

    /**
     * Le seuil de degré d'alcool permettant de séparer alcool fort et alcool doux
     */
    private final Double SEUIL_DEGRE_ALCOOL_FORT = 22.0;

    //endregion

    //region CONSTRUCTEUR

    public Boisson() {

    }

    /**
     * TODO COMMENTAIRE
     * @param id
     * @param nom
     * @param description_courte
     * @param description_longue
     * @param taux_alcoolemie
     * @param type
     * @param sous_type
     */
    public Boisson(int id, String nom, String description_courte, @Nullable String description_longue, @Nullable double taux_alcoolemie, String type, @Nullable String sous_type, Double prix) {
        // contrôles des paramètres
        if (id == 0) {
            //TODO ERREUR
        }

        if (nom.trim().isEmpty()) {
            //TODO ERREUR
        }

        if (description_courte.trim().isEmpty()) {
            //TODO ERREUR
        }

        if (description_longue == null) {
            description_longue = description_courte;
        }

        if (type == null) {
            //TODO ERREUR
        }

        if (sous_type == null) {
            //TODO ERREUR
        }

        // Tout est ok, affectation des attributs
        this.id = id;
        this.nom = nom;
        this.description_courte = description_courte;
        this.description_longue = description_longue;
        this.taux_alcoolemie = taux_alcoolemie;
        this.type = type;
        this.sous_type = sous_type;
        this.prix = prix;
    }
    //endregion

    //region FONCTIONS

    /**
     * Met en forme la description courte, appelée titre court pour éviter les confusion
     * @return les éléments nécessaires à afficher en premier concernant une boisson
     */
    public String getTitreCourt() {
        return this.getNom() + "\n" + this.getDescription_courte();
    }

    /**
     * Met en forme la description longue, appelée titre long pour éviter les confusion
     * @return les éléments détaillés à afficher concernant une boisson suite à un clic
     */
    public String getTitreLong() {
        return this.getNom() + "\n" + this.getDescription_longue();
    }

    /**
     * Évalue la force de la boisson courante
     * @return vrai si la boisson est forte (taux d'alcool > seuil), faux sinon
     */
    public boolean isAlcoolFort() {
        return this.getTaux_alcoolemie() >= SEUIL_DEGRE_ALCOOL_FORT;
    }

    /**
     * Évalue la force de la boisson courante
     * @return vrai si la boisson est douce (0 < taux d'alcool <= seuil), faux sinon
     */
    public boolean isAlcoolDoux() {
        return this.getTaux_alcoolemie() < SEUIL_DEGRE_ALCOOL_FORT && this.getTaux_alcoolemie() > 0 ;
    }

    /**
     * Évalue la force de la boisson courante
     * @return vrai si la boisson est un soft (taux d'alcool = 0), faux sinon
     */
    public boolean isSoft() {
        return this.getTaux_alcoolemie() == 0 ;
    }

    @Override
    public int compare(Boisson b1, Boisson b2) {
        switch(b1.tri) {
            case "nom":
                return b1.nom.compareTo(b2.nom);
            case "id" :
                return Integer.compare(b1.id, b2.id);
            case "puissance":
                return Double.compare(b1.taux_alcoolemie, b2.taux_alcoolemie);
        }

        return 0;
    }
    //endregion

    //region ACCESSEURS
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription_courte() {
        return description_courte;
    }

    public void setDescription_courte(String description_courte) {
        this.description_courte = description_courte;
    }

    public String getDescription_longue() {
        return description_longue;
    }

    public void setDescription_longue(String description_longue) {
        this.description_longue = description_longue;
    }

    public double getTaux_alcoolemie() {
        return taux_alcoolemie;
    }

    public void setTaux_alcoolemie(double taux_alcoolemie) {
        this.taux_alcoolemie = taux_alcoolemie;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSous_type() {
        return sous_type;
    }

    public void setSous_type(String sous_type) {
        this.sous_type = sous_type;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public String getTri() {
        return tri;
    }

    public void setTri(String tri) {
        this.tri = tri;
    }

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }
    //endregion
}
