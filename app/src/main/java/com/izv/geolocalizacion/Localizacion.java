package com.izv.geolocalizacion;

import android.location.Location;

import java.io.Serializable;
import java.util.Date;

public class Localizacion implements Serializable{

    private Date fecha;
    private Location localizacion;
    private String localidad;
    private String calle;

    public Localizacion(){}

    public Localizacion(Date fecha, Location localizacion, String localidad, String calle) {
        this.fecha = fecha;
        this.localizacion = localizacion;
        this.localidad = localidad;
        this.calle = calle;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Location getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(Location localizacion) {
        this.localizacion = localizacion;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    @Override
    public String toString() {
        return "Localizacion{" +
                "fecha=" + fecha +
                ", localizacion=" + localizacion +
                ", localidad='" + localidad + '\'' +
                ", calle='" + calle + '\'' +
                '}';
    }
}
