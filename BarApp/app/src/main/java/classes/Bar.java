package classes;

/**
 * Created by leofa on 15/10/2017.
 */

public class Bar {

    public long id;
    public String nom;
    public String adresse;
    public Double latitude;
    public Double longitude;

    public Bar(long p_id, String p_nom, String p_adresse, Double p_lat, Double p_long){
        this.id=p_id;
        this.nom=p_nom;
        this.adresse=p_adresse;
        this.latitude=p_lat;
        this.longitude=p_long;
    }

}
