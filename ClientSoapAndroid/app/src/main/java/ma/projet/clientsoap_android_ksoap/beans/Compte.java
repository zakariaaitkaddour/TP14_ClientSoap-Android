package ma.projet.clientsoap_android_ksoap.beans;

import java.util.Date;

public class Compte {
    private Long id;
    private double solde;
    private Date dateCreation;
    private TypeCompte type;

    // Constructeur
    public Compte(Long id, double solde, Date dateCreation, TypeCompte type) {
        this.id = id;
        this.solde = solde;
        this.dateCreation = dateCreation;
        this.type = type;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public double getSolde() {
        return solde;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public TypeCompte getType() {
        return type;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setSolde(double solde) {
        this.solde = solde;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public void setType(TypeCompte type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Compte{" +
                "id=" + id +
                ", solde=" + solde +
                ", dateCreation=" + dateCreation +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Compte compte = (Compte) o;
        return id != null && id.equals(compte.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}