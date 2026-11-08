package ma.fstg.banque.web.dto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import ma.fstg.banque.entities.Compte;

import java.util.List;

/**
 * Enveloppe XML d'une collection de comptes.
 * <p>
 * JAXB ne sait pas produire un document XML a partir d'une {@code List} nue :
 * il lui faut un element racine. Ce wrapper porte cet element et sert aussi de
 * reponse JSON, ou il expose simplement la liste sous la cle {@code comptes}.
 */
@XmlRootElement(name = "comptes")
public class ComptesXml {

    private List<Compte> comptes;

    /** Constructeur requis par JAXB. */
    public ComptesXml() {
    }

    /**
     * @param comptes liste a envelopper
     */
    public ComptesXml(List<Compte> comptes) {
        this.comptes = comptes;
    }

    /**
     * @return la liste des comptes
     */
    @XmlElement(name = "compte")
    public List<Compte> getComptes() {
        return comptes;
    }

    public void setComptes(List<Compte> comptes) {
        this.comptes = comptes;
    }
}
