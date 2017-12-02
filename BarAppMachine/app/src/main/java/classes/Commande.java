package classes;

/**
 * Created by Lucas on 04/11/2017.
 */

public class Commande {


    //region PROPRIETES
    /**
     * L'identifiant unique de la commande
     */
    private int id;

    /**
     * La boisson qui a été commandée par le client
     */
    private Boisson boisson_commande;

    /**
     * La quantité de boisson commandée
     */
    private int quantite_commande;

    /**
     * Le client ayant passé la commande
     */
    private Utilisateur client;

    private String id_cocktail;

    private String nom;

    private int id_commande;

    /**
     * Coefficients d'importance des types de boisson afin de déterminer l'importance de chaque
     * boisson
     */
    private final double COEFF_ALCOOL_FORT = 5;
    private final double COEFF_ALCOOL_DOUX = 2.5;
    private final double COEFF_SOFT = 1;
    //endregion PROPRIETES

    //region CONSTRUCTEUR
    public Commande(Boisson b, int quantite, Utilisateur u, String id_cocktail, String nom, int id_commande) {
        this.boisson_commande = b;
        this.quantite_commande = quantite;
        this.client = u;
        this.id_cocktail = id_cocktail;
        this.nom = nom;
        this.id_commande = id_commande;
    }
    //endregion CONSTRUCTEUR

    //region FONCTIONS

    public boolean isCommun() {
        try {
            Integer.parseInt(this.id_cocktail);

            // les cocktails en commun ont un identifiant de type entier
            return true;
        } catch (Exception e) {
            // alors que ceux des utilisateurs possèdent l'id de l'utilisateur
            return false;
        }
    }

    //endregion FONCTIONS

    //region ACCESSEURS
    public Boisson getBoisson_commande() {
        return boisson_commande;
    }

    public void setBoisson_commande(Boisson boisson_commande) {
        this.boisson_commande = boisson_commande;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantiteCommande() {
        return quantite_commande;
    }

    public void setQuantiteCommande(int q) {
        this.quantite_commande = q;
    }

    public Utilisateur getClient() {
        return client;
    }

    public void setClient(Utilisateur client) {
        this.client = client;
    }

    public String getIdCocktail() {
        return id_cocktail;
    }

    public void setIdCocktail(String i) {
        this.id_cocktail = i;
    }

    public String getNom() {
        return this.nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getIdCommande() {
        return this.id_commande;
    }

    public void setIdCommande(int id_commande) {
        this.id_commande = id_commande;
    }

    //endregion ACCESSEURS

}
