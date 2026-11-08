package ma.fstg.banque.web;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ma.fstg.banque.entities.Compte;
import ma.fstg.banque.entities.TypeCompte;
import ma.fstg.banque.repositories.CompteRepository;
import ma.fstg.banque.web.dto.ComptesXml;
import ma.fstg.banque.web.dto.StatistiquesBanque;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Service REST JAX-RS des comptes bancaires.
 * <p>
 * Chaque methode declare produire et consommer du JSON <em>et</em> du XML :
 * c'est l'en-tete {@code Accept} de la requete qui decide du format de reponse.
 * Aucune annotation Spring MVC ici, tout passe par Jersey.
 */
@Component
@Path("/banque")
public class CompteRestJaxRSAPI {

    private final CompteRepository compteRepository;

    /**
     * @param compteRepository repository injecte par Spring
     */
    public CompteRestJaxRSAPI(CompteRepository compteRepository) {
        this.compteRepository = compteRepository;
    }

    /**
     * Liste tous les comptes au format JSON.
     *
     * @return la liste complete
     */
    @Path("/comptes")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Compte> getComptesJson() {
        return compteRepository.findAll();
    }

    /**
     * Liste tous les comptes au format XML.
     * <p>
     * JAXB exige un element racine unique : la liste est donc enveloppee dans
     * {@link ComptesXml}, ce qui produit un document {@code <comptes>}.
     *
     * @return l'enveloppe contenant les comptes
     */
    @Path("/comptes")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public ComptesXml getComptesXml() {
        return new ComptesXml(compteRepository.findAll());
    }

    /**
     * Recherche un compte par identifiant.
     *
     * @param id identifiant recherche
     * @return le compte, ou une reponse 404
     */
    @Path("/comptes/{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCompte(@PathParam("id") Long id) {
        return compteRepository.findById(id)
                .map(compte -> Response.ok(compte).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    /**
     * Cree un compte.
     *
     * @param compte compte a enregistrer
     * @return le compte cree avec un statut 201
     */
    @Path("/comptes")
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response addCompte(Compte compte) {
        compte.setId(null);
        Compte enregistre = compteRepository.save(compte);
        return Response.status(Response.Status.CREATED).entity(enregistre).build();
    }

    /**
     * Met a jour un compte existant.
     *
     * @param id     identifiant du compte
     * @param compte nouvelles valeurs
     * @return le compte mis a jour, ou 404
     */
    @Path("/comptes/{id}")
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateCompte(@PathParam("id") Long id, Compte compte) {
        return compteRepository.findById(id)
                .map(existant -> {
                    existant.setSolde(compte.getSolde());
                    existant.setDateCreation(compte.getDateCreation());
                    existant.setType(compte.getType());
                    return Response.ok(compteRepository.save(existant)).build();
                })
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    /**
     * Supprime un compte.
     *
     * @param id identifiant du compte
     * @return 204 si supprime, 404 sinon
     */
    @Path("/comptes/{id}")
    @DELETE
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response deleteCompte(@PathParam("id") Long id) {
        if (!compteRepository.existsById(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        compteRepository.deleteById(id);
        return Response.noContent().build();
    }

    /**
     * Liste les comptes d'une nature donnee.
     *
     * @param type COURANT ou EPARGNE
     * @return les comptes correspondants
     */
    @Path("/comptes/type/{type}")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<Compte> parType(@PathParam("type") String type) {
        return compteRepository.findByType(TypeCompte.valueOf(type.toUpperCase()));
    }

    /**
     * Liste les comptes dont le solde depasse un seuil.
     *
     * @param seuil montant minimal
     * @return les comptes concernes
     */
    @Path("/comptes/riches")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<Compte> plusRichesQue(@QueryParam("seuil") double seuil) {
        return compteRepository.findPlusRichesQue(seuil);
    }

    /**
     * Apport personnel : statistiques agregees du portefeuille.
     * <p>
     * Le TP s'arrete au CRUD. Un responsable d'agence a pourtant besoin d'une
     * vue chiffree : nombre de comptes, masse totale, solde moyen, repartition
     * par type et compte le mieux provisionne.
     *
     * @return les statistiques du portefeuille
     */
    @Path("/statistiques")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public StatistiquesBanque statistiques() {
        List<Compte> comptes = compteRepository.findAll();
        if (comptes.isEmpty()) {
            return new StatistiquesBanque(0, 0.0, 0.0, 0L, 0L, 0.0);
        }
        double masse = comptes.stream().mapToDouble(Compte::getSolde).sum();
        double moyen = masse / comptes.size();
        long courants = comptes.stream()
                .filter(compte -> compte.getType() == TypeCompte.COURANT).count();
        long epargnes = comptes.stream()
                .filter(compte -> compte.getType() == TypeCompte.EPARGNE).count();
        double meilleur = comptes.stream().mapToDouble(Compte::getSolde).max().orElse(0.0);

        return new StatistiquesBanque(comptes.size(), arrondir(masse), arrondir(moyen),
                courants, epargnes, arrondir(meilleur));
    }

    private static double arrondir(double valeur) {
        return Math.round(valeur * 100.0) / 100.0;
    }
}
