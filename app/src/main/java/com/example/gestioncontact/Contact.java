package com.example.gestioncontact;

public class Contact {

    private String Nom;
    private String Pseudo;
    private String Numero;



    public void setNom(String nom) {
        Nom = nom;
    }

    public void setPseudo(String pseudo) {
        Pseudo = pseudo;
    }

    public void setNumero(String numero) {
        Numero = numero;
    }

    public String getNom() {
        return Nom;
    }

    public String getPseudo() {
        return Pseudo;
    }

    public String getNumero() {
        return Numero;
    }
    public Contact(String pseudo, String nom, String numero) {
        Pseudo = pseudo;
        Nom = nom;
        Numero = numero;
    }
}
