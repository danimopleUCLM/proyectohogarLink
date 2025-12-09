package es.proyecto.proyectohogarLink.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ListaDeseosId implements Serializable {

    private Integer inquilino_id;
    private Integer inmueble_id;

    public ListaDeseosId() {}

    public ListaDeseosId(Integer inquilinoId, Integer inmuebleId) {
        this.inquilino_id = inquilinoId;
        this.inmueble_id = inmuebleId;
    }

    public Integer getInquilino_id() { return inquilino_id; }
    public void setInquilino_id(Integer id) { this.inquilino_id = id; }

    public Integer getInmueble_id() { return inmueble_id; }
    public void setInmueble_id(Integer id) { this.inmueble_id = id; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ListaDeseosId)) return false;
        ListaDeseosId that = (ListaDeseosId) o;
        return Objects.equals(inquilino_id, that.inquilino_id) &&
               Objects.equals(inmueble_id, that.inmueble_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inquilino_id, inmueble_id);
    }
}
