package ma.fstg.banque.web.dto;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Statistiques agregees du portefeuille de comptes.
 * <p>
 * Le DTO est annote {@code @XmlRootElement} pour pouvoir etre renvoye aussi
 * bien en JSON qu'en XML, comme les entites.
 */
@XmlRootElement(name = "statistiques")
public class StatistiquesBanque {

    private int nombreComptes;
    private double masseTotale;
    private double soldeMoyen;
    private long comptesCourants;
    private long comptesEpargne;
    private double soldeMaximal;

    /** Constructeur requis par JAXB. */
    public StatistiquesBanque() {
    }

    /**
     * @param nombreComptes   nombre de comptes
     * @param masseTotale     somme des soldes
     * @param soldeMoyen      moyenne des soldes
     * @param comptesCourants nombre de comptes courants
     * @param comptesEpargne  nombre de comptes epargne
     * @param soldeMaximal    solde le plus eleve
     */
    public StatistiquesBanque(int nombreComptes, double masseTotale, double soldeMoyen,
                              long comptesCourants, long comptesEpargne, double soldeMaximal) {
        this.nombreComptes = nombreComptes;
        this.masseTotale = masseTotale;
        this.soldeMoyen = soldeMoyen;
        this.comptesCourants = comptesCourants;
        this.comptesEpargne = comptesEpargne;
        this.soldeMaximal = soldeMaximal;
    }

    public int getNombreComptes() {
        return nombreComptes;
    }

    public void setNombreComptes(int nombreComptes) {
        this.nombreComptes = nombreComptes;
    }

    public double getMasseTotale() {
        return masseTotale;
    }

    public void setMasseTotale(double masseTotale) {
        this.masseTotale = masseTotale;
    }

    public double getSoldeMoyen() {
        return soldeMoyen;
    }

    public void setSoldeMoyen(double soldeMoyen) {
        this.soldeMoyen = soldeMoyen;
    }

    public long getComptesCourants() {
        return comptesCourants;
    }

    public void setComptesCourants(long comptesCourants) {
        this.comptesCourants = comptesCourants;
    }

    public long getComptesEpargne() {
        return comptesEpargne;
    }

    public void setComptesEpargne(long comptesEpargne) {
        this.comptesEpargne = comptesEpargne;
    }

    public double getSoldeMaximal() {
        return soldeMaximal;
    }

    public void setSoldeMaximal(double soldeMaximal) {
        this.soldeMaximal = soldeMaximal;
    }
}
