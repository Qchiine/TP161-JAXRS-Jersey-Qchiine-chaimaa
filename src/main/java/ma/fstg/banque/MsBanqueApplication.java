package ma.fstg.banque;

import ma.fstg.banque.entities.Compte;
import ma.fstg.banque.entities.TypeCompte;
import ma.fstg.banque.repositories.CompteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Point d'entree du microservice bancaire.
 * <p>
 * Le jeu de donnees est insere une seule fois, via un {@code CommandLineRunner}
 * comme demande a l'etape 6. Les soldes restent deterministes pour que les
 * captures d'ecran soient reproductibles.
 */
@SpringBootApplication
public class MsBanqueApplication {

    /** Graine fixe : le jeu de donnees est identique a chaque demarrage. */
    private static final long GRAINE = 20240517L;

    /**
     * @param args arguments de la ligne de commande
     */
    public static void main(String[] args) {
        SpringApplication.run(MsBanqueApplication.class, args);
    }

    /**
     * Initialise trois comptes de demonstration.
     *
     * @param compteRepository repository des comptes
     * @return le lanceur execute au demarrage
     */
    @Bean
    CommandLineRunner initialiserComptes(CompteRepository compteRepository) {
        return args -> {
            if (compteRepository.count() > 0) {
                return;
            }
            Random aleatoire = new Random(GRAINE);
            List<Compte> comptes = List.of(
                    new Compte(null, arrondir(aleatoire.nextDouble() * 9000), aujourdHui(),
                            TypeCompte.EPARGNE),
                    new Compte(null, arrondir(aleatoire.nextDouble() * 9000), aujourdHui(),
                            TypeCompte.COURANT),
                    new Compte(null, arrondir(aleatoire.nextDouble() * 9000), aujourdHui(),
                            TypeCompte.EPARGNE));
            compteRepository.saveAll(comptes);
            compteRepository.findAll().forEach(compte -> System.out.println("  " + compte));
        };
    }

    private static double arrondir(double valeur) {
        return Math.round(valeur * 100.0) / 100.0;
    }

    private static Date aujourdHui() {
        return Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
