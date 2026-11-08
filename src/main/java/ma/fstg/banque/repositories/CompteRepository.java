package ma.fstg.banque.repositories;

import ma.fstg.banque.entities.Compte;
import ma.fstg.banque.entities.TypeCompte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Acces aux donnees des comptes.
 * <p>
 * Spring Data JPA fournit l'implementation : seules les requetes metier sont
 * declarees explicitement.
 */
@Repository
public interface CompteRepository extends JpaRepository<Compte, Long> {

    /**
     * Liste les comptes d'une nature donnee.
     *
     * @param type nature recherchee
     * @return les comptes correspondants
     */
    List<Compte> findByType(TypeCompte type);

    /**
     * Liste les comptes dont le solde depasse un seuil.
     *
     * @param seuil montant minimal exclu
     * @return les comptes concernes, tries par solde decroissant
     */
    @Query("from Compte c where c.solde > :seuil order by c.solde desc")
    List<Compte> findPlusRichesQue(double seuil);
}
