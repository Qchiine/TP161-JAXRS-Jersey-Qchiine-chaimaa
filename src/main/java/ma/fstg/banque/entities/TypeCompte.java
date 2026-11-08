package ma.fstg.banque.entities;

/**
 * Nature du compte bancaire.
 * <p>
 * L'ordre des constantes compte : l'entite utilise
 * {@code EnumType.ORDINAL}, donc COURANT est stocke 0 et EPARGNE 1.
 */
public enum TypeCompte {

    /** Compte courant, operations courantes. */
    COURANT,

    /** Compte epargne, rémunéré. */
    EPARGNE
}
