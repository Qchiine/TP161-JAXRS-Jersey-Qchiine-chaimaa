package ma.fstg.banque.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.Date;
import java.util.Objects;

/**
 * Compte bancaire persiste dans la base H2.
 * <p>
 * {@code @XmlRootElement} autorise la serialisation JAXB : le service peut donc
 * renvoyer du JSON ou du XML selon l'en-tete {@code Accept} du client. Les deux
 * representations portent les memes donnees.
 */
@Entity
@Table(name = "compte")
@XmlRootElement(name = "compte")
public class Compte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double solde;

    @Temporal(TemporalType.DATE)
    private Date dateCreation;

    @Enumerated(EnumType.ORDINAL)
    private TypeCompte type;

    /** Constructeur requis par JPA et JAXB. */
    public Compte() {
    }

    /**
     * @param id           identifiant, null pour une creation
     * @param solde        montant disponible
     * @param dateCreation date d'ouverture
     * @param type         nature du compte
     */
    public Compte(Long id, double solde, Date dateCreation, TypeCompte type) {
        this.id = id;
        this.solde = solde;
        this.dateCreation = dateCreation;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getSolde() {
        return solde;
    }

    public void setSolde(double solde) {
        this.solde = solde;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public TypeCompte getType() {
        return type;
    }

    public void setType(TypeCompte type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object autre) {
        if (this == autre) {
            return true;
        }
        if (!(autre instanceof Compte)) {
            return false;
        }
        Compte compte = (Compte) autre;
        return id != null && id.equals(compte.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Compte#" + id + " solde=" + solde + " type=" + type;
    }
}
