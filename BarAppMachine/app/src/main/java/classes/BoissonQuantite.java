package classes;

/**
 * Created by Lucas on 01/11/2017.
 */

public class BoissonQuantite extends Boisson {

    private Boisson boisson;

    private int quantite;

    public BoissonQuantite(Boisson b, int quantite) {
        this.boisson = b;
        this.quantite = quantite;
    }

    public Boisson getBoisson() {
        return boisson;
    }

    public void setBoisson(Boisson boisson) {
        this.boisson = boisson;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
}
