package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Cursa extends Entity<Long> implements Serializable {
    private String destinatie;
    private LocalDateTime data;

    public Cursa(String destinatie, LocalDateTime data) {
        this.destinatie = destinatie;
        this.data = data;
    }

    public Cursa(){};
    public String getDestinatie() {
        return destinatie;
    }

    public void setDestinatie(String destinatie) {
        this.destinatie = destinatie;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cursa cursa)) return false;
        return Objects.equals(getDestinatie(), cursa.getDestinatie()) && Objects.equals(getData(), cursa.getData());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDestinatie(), getData());
    }

    @Override
    public String toString() {
        return "Cursa{" + this.getId() + " " +
                "destinatie='" + destinatie + '\'' +
                ", data=" + data +
                '}';
    }
}

