package classes;

import java.util.Date;

/**
 * Created by leofa on 15/10/2017.
 * Classe décrivant le comportement d'un évènement
 */

public class Evenement {

    public String id; //id de l'évènement
    public String titre; // titre de l'évènement
    public String descriptionCourte; // courte description pour affichage dans une liste
    public String descriptionLongue; // plus longue description de l'évènement pour l'affichage des détails
    public String image; //image de présentation de l'évènement
    public Date date; //date de l'évènement
    public Bar lieu; //bar dans lequel se déroule l'évènement;

    public Evenement(String p_id,String p_titre, String p_descriptionC,String p_descriptionL,String p_image,Date p_date,Bar p_bar){
        this.id=p_id;
        this.titre = p_titre;
        this.descriptionCourte=p_descriptionC;
        this.descriptionLongue=p_descriptionL;
        this.image=p_image;
        this.date=p_date;
        this.lieu=p_bar;
    }

}
