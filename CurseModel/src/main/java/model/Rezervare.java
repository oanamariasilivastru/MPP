package model;

import java.io.Serializable;
import java.util.Objects;

public class Rezervare extends Entity<Long> implements Serializable {

    private Cursa cursa;
    private String numeClient;
    private Integer nrLocuri;

    public Rezervare(Cursa cursa, String numeClient, Integer nrLocuri) {
        this.cursa = cursa;
        this.numeClient = numeClient;
        this.nrLocuri = nrLocuri;
    }

    public Cursa getCursa() {
        return cursa;
    }

    public void setIdCursa(Cursa cursa) {
        this.cursa = cursa;
    }

    public String getNumeClient() {
        return numeClient;
    }

    public void setNumeClient(String numeClient) {
        this.numeClient = numeClient;
    }

    public Integer getNrLocuri() {
        return nrLocuri;
    }

    public void setNrLocuri(Integer nrLocuri) {
        this.nrLocuri = nrLocuri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rezervare rezervare)) return false;
        return Objects.equals(getCursa(), rezervare.getCursa()) && Objects.equals(getNumeClient(), rezervare.getNumeClient()) && Objects.equals(getNrLocuri(), rezervare.getNrLocuri());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCursa(), getNumeClient(), getNrLocuri());
    }

    @Override
    public String toString() {
        return "Rezervare{" +
                "Cursa=" + cursa +
                ", numeClient='" + numeClient + '\'' +
                ", nrLocuri=" + nrLocuri +
                '}';
    }
}
