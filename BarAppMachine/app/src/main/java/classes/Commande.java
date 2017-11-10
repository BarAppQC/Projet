package classes;

/**
 * Created by Lucas on 04/11/2017.
 */

public class Commande {

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

    /**
     * Coefficients d'importance des types de boisson afin de déterminer l'importance de chaque
     * boisson
     */
    private final double COEFF_ALCOOL_FORT = 5;
    private final double COEFF_ALCOOL_DOUX = 2.5;
    private final double COEFF_SOFT = 1;

    public Commande(Boisson b, int quantite, Utilisateur u) {
        this.boisson_commande = b;
        this.quantite_commande = quantite;
        this.client = u;
    }

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

}
