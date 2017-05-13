package com.example.koro_.stalertv2;

import com.socrata.android.client.SodaEntity;
import com.socrata.android.client.SodaField;

/**
 * Creado por Juan David Hernández el 23/04/2017.
 */
@SodaEntity
public class Interrupciones {

    @SodaField("barrio")
    private String barrio;

    @SodaField("inicio")
    private String inicio;

    @SodaField("fin")
    private String fin;

    @SodaField("servicio")
    private String servicio;

    @SodaField("direccion")
    private String direccion;

    public Interrupciones() {
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public String getInicio() {
        return inicio;
    }

    public void setInicio(String inicio) {
        this.inicio = inicio;
    }

    public String getFin() {
        return fin;
    }

    public void setFin(String fin) {
        this.fin = fin;
    }

    public String getServicio() {
        return servicio;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @Override
    public String toString(){
        return String.format(
                        "Barrio: %s\n" +
                        "Inicio: %s\n" +
                        "Fin: %s\n"+
                        "Servicio: %s\n"+
                        "Direccion: %s", barrio, inicio, fin,servicio,direccion);
    }
}
