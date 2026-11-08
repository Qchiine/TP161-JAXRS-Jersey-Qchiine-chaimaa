package ma.fstg.banque;

import ma.fstg.banque.entities.Compte;
import ma.fstg.banque.entities.TypeCompte;
import ma.fstg.banque.repositories.CompteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifie le service JAX-RS de bout en bout : serveur reel, base H2 en memoire,
 * et appels HTTP sur les deux formats.
 * <p>
 * Les tests confirment que le meme endpoint repond en JSON ou en XML selon
 * l'en-tete {@code Accept}, ce qui est le coeur du TP.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Service REST JAX-RS des comptes")
class CompteRestJaxRSAPITest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CompteRepository compteRepository;

    private static final String BASE = "/banque";

    @BeforeEach
    void preparer() {
        compteRepository.deleteAll();
        compteRepository.save(new Compte(null, 1500.0, aujourdHui(), TypeCompte.COURANT));
        compteRepository.save(new Compte(null, 3500.0, aujourdHui(), TypeCompte.EPARGNE));
        compteRepository.save(new Compte(null, 250.0, aujourdHui(), TypeCompte.EPARGNE));
    }

    private static Date aujourdHui() {
        return Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @Test
    @DisplayName("GET /comptes renvoie du JSON quand le client le demande")
    void listeEnJson() {
        HttpHeaders entetes = new HttpHeaders();
        entetes.setAccept(List.of(MediaType.APPLICATION_JSON));
        ResponseEntity<String> reponse = restTemplate.exchange(BASE + "/comptes",
                HttpMethod.GET, new HttpEntity<>(entetes), String.class);

        assertEquals(HttpStatus.OK, reponse.getStatusCode());
        assertNotNull(reponse.getHeaders().getContentType());
        assertTrue(reponse.getHeaders().getContentType().includes(MediaType.APPLICATION_JSON));
        assertTrue(reponse.getBody().startsWith("["), reponse.getBody());
        assertTrue(reponse.getBody().contains("solde"));
    }

    @Test
    @DisplayName("GET /comptes renvoie du XML quand le client le demande")
    void listeEnXml() {
        HttpHeaders entetes = new HttpHeaders();
        entetes.setAccept(List.of(MediaType.APPLICATION_XML));
        ResponseEntity<String> reponse = restTemplate.exchange(BASE + "/comptes",
                HttpMethod.GET, new HttpEntity<>(entetes), String.class);

        assertEquals(HttpStatus.OK, reponse.getStatusCode());
        assertNotNull(reponse.getHeaders().getContentType());
        assertTrue(reponse.getHeaders().getContentType().includes(MediaType.APPLICATION_XML));
        assertTrue(reponse.getBody().contains("<comptes>"), reponse.getBody());
        assertTrue(reponse.getBody().contains("<compte>"), reponse.getBody());
        assertTrue(reponse.getBody().contains("<solde>"), reponse.getBody());
    }

    @Test
    @DisplayName("GET /comptes/{id} expose un compte unique en JSON")
    void compteUniqueJson() {
        Long id = compteRepository.findAll().get(0).getId();

        HttpHeaders entetes = new HttpHeaders();
        entetes.setAccept(List.of(MediaType.APPLICATION_JSON));
        ResponseEntity<String> reponse = restTemplate.exchange(BASE + "/comptes/" + id,
                HttpMethod.GET, new HttpEntity<>(entetes), String.class);

        assertEquals(HttpStatus.OK, reponse.getStatusCode());
        assertTrue(reponse.getBody().contains("\"id\":" + id));
    }

    @Test
    @DisplayName("GET /comptes/{id} expose un compte unique en XML avec l'element racine")
    void compteUniqueXml() {
        Long id = compteRepository.findAll().get(0).getId();

        HttpHeaders entetes = new HttpHeaders();
        entetes.setAccept(List.of(MediaType.APPLICATION_XML));
        ResponseEntity<String> reponse = restTemplate.exchange(BASE + "/comptes/" + id,
                HttpMethod.GET, new HttpEntity<>(entetes), String.class);

        assertEquals(HttpStatus.OK, reponse.getStatusCode());
        assertTrue(reponse.getBody().contains("<compte>"), reponse.getBody());
        assertTrue(reponse.getBody().contains("<id>" + id + "</id>"), reponse.getBody());
    }

    @Test
    @DisplayName("un identifiant inconnu renvoie 404")
    void compteInconnu() {
        HttpHeaders entetes = new HttpHeaders();
        entetes.setAccept(List.of(MediaType.APPLICATION_JSON));
        ResponseEntity<String> reponse = restTemplate.exchange(BASE + "/comptes/9999",
                HttpMethod.GET, new HttpEntity<>(entetes), String.class);

        assertEquals(HttpStatus.NOT_FOUND, reponse.getStatusCode());
    }

    @Test
    @DisplayName("POST cree un compte et renvoie 201")
    void creationCompte() {
        HttpHeaders entetes = new HttpHeaders();
        entetes.setContentType(MediaType.APPLICATION_JSON);
        entetes.setAccept(List.of(MediaType.APPLICATION_JSON));
        String corps = "{\"solde\":999.99,\"dateCreation\":\"2026-01-15\",\"type\":\"COURANT\"}";

        ResponseEntity<String> reponse = restTemplate.exchange(BASE + "/comptes",
                HttpMethod.POST, new HttpEntity<>(corps, entetes), String.class);

        assertEquals(HttpStatus.CREATED, reponse.getStatusCode());
        assertTrue(reponse.getBody().contains("999.99"));
        assertEquals(4, compteRepository.count());
    }

    @Test
    @DisplayName("PUT met a jour un compte existant")
    void miseAJourCompte() {
        Long id = compteRepository.findAll().get(0).getId();
        HttpHeaders entetes = new HttpHeaders();
        entetes.setContentType(MediaType.APPLICATION_JSON);
        entetes.setAccept(List.of(MediaType.APPLICATION_JSON));
        String corps = "{\"solde\":4321.0,\"dateCreation\":\"2026-01-15\",\"type\":\"EPARGNE\"}";

        ResponseEntity<String> reponse = restTemplate.exchange(BASE + "/comptes/" + id,
                HttpMethod.PUT, new HttpEntity<>(corps, entetes), String.class);

        assertEquals(HttpStatus.OK, reponse.getStatusCode());
        assertEquals(4321.0, compteRepository.findById(id).orElseThrow().getSolde(), 0.001);
    }

    @Test
    @DisplayName("PUT sur un identifiant inconnu renvoie 404")
    void miseAJourInconnue() {
        HttpHeaders entetes = new HttpHeaders();
        entetes.setContentType(MediaType.APPLICATION_JSON);
        String corps = "{\"solde\":1.0,\"type\":\"COURANT\"}";

        ResponseEntity<String> reponse = restTemplate.exchange(BASE + "/comptes/9999",
                HttpMethod.PUT, new HttpEntity<>(corps, entetes), String.class);

        assertEquals(HttpStatus.NOT_FOUND, reponse.getStatusCode());
    }

    @Test
    @DisplayName("DELETE retire le compte et renvoie 204")
    void suppressionCompte() {
        Long id = compteRepository.findAll().get(0).getId();

        ResponseEntity<String> reponse = restTemplate.exchange(BASE + "/comptes/" + id,
                HttpMethod.DELETE, null, String.class);

        assertEquals(HttpStatus.NO_CONTENT, reponse.getStatusCode());
        assertEquals(2, compteRepository.count());
    }

    @Test
    @DisplayName("DELETE sur un identifiant inconnu renvoie 404")
    void suppressionInconnue() {
        ResponseEntity<String> reponse = restTemplate.exchange(BASE + "/comptes/9999",
                HttpMethod.DELETE, null, String.class);

        assertEquals(HttpStatus.NOT_FOUND, reponse.getStatusCode());
    }

    @Test
    @DisplayName("le filtre par type isole les comptes epargne")
    void filtreParType() {
        HttpHeaders entetes = new HttpHeaders();
        entetes.setAccept(List.of(MediaType.APPLICATION_JSON));
        ResponseEntity<String> reponse = restTemplate.exchange(BASE + "/comptes/type/EPARGNE",
                HttpMethod.GET, new HttpEntity<>(entetes), String.class);

        assertEquals(HttpStatus.OK, reponse.getStatusCode());
        assertTrue(reponse.getBody().contains("EPARGNE"));
        assertTrue(!reponse.getBody().contains("COURANT"), reponse.getBody());
    }

    @Test
    @DisplayName("le filtre par seuil de solde fonctionne")
    void filtreParSeuil() {
        HttpHeaders entetes = new HttpHeaders();
        entetes.setAccept(List.of(MediaType.APPLICATION_JSON));
        ResponseEntity<String> reponse = restTemplate.exchange(BASE + "/comptes/riches?seuil=1000",
                HttpMethod.GET, new HttpEntity<>(entetes), String.class);

        assertEquals(HttpStatus.OK, reponse.getStatusCode());
        assertTrue(!reponse.getBody().contains("250.0"), reponse.getBody());
    }

    @Test
    @DisplayName("les statistiques agregees sont exposees en JSON")
    void statistiquesJson() {
        HttpHeaders entetes = new HttpHeaders();
        entetes.setAccept(List.of(MediaType.APPLICATION_JSON));
        ResponseEntity<String> reponse = restTemplate.exchange(BASE + "/statistiques",
                HttpMethod.GET, new HttpEntity<>(entetes), String.class);

        assertEquals(HttpStatus.OK, reponse.getStatusCode());
        assertTrue(reponse.getBody().contains("\"nombreComptes\":3"), reponse.getBody());
        assertTrue(reponse.getBody().contains("\"comptesEpargne\":2"), reponse.getBody());
    }

    @Test
    @DisplayName("les statistiques sont aussi disponibles en XML")
    void statistiquesXml() {
        HttpHeaders entetes = new HttpHeaders();
        entetes.setAccept(List.of(MediaType.APPLICATION_XML));
        ResponseEntity<String> reponse = restTemplate.exchange(BASE + "/statistiques",
                HttpMethod.GET, new HttpEntity<>(entetes), String.class);

        assertEquals(HttpStatus.OK, reponse.getStatusCode());
        assertTrue(reponse.getBody().contains("<statistiques>"), reponse.getBody());
        assertTrue(reponse.getBody().contains("<nombreComptes>3</nombreComptes>"), reponse.getBody());
    }

    @Test
    @DisplayName("sans en-tete Accept, le service repond par defaut")
    void sansEnteteAccept() {
        ResponseEntity<String> reponse = restTemplate.getForEntity(BASE + "/comptes", String.class);

        assertEquals(HttpStatus.OK, reponse.getStatusCode());
        assertNotNull(reponse.getBody());
    }

    @Test
    @DisplayName("les deux formats exposent les memes donnees")
    void memesDonneesDansLesDeuxFormats() {
        HttpHeaders json = new HttpHeaders();
        json.setAccept(List.of(MediaType.APPLICATION_JSON));
        String corpsJson = restTemplate.exchange(BASE + "/comptes", HttpMethod.GET,
                new HttpEntity<>(json), String.class).getBody();

        HttpHeaders xml = new HttpHeaders();
        xml.setAccept(List.of(MediaType.APPLICATION_XML));
        String corpsXml = restTemplate.exchange(BASE + "/comptes", HttpMethod.GET,
                new HttpEntity<>(xml), String.class).getBody();

        assertTrue(corpsJson.contains("3500.0"), corpsJson);
        assertTrue(corpsXml.contains("<solde>3500.0</solde>"), corpsXml);
    }
}
