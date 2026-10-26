package modelo;

import java.time.LocalDate;
import java.time.LocalTime;

public class Actividad {

    private static int contadorId = 1;
    private String nombreActividad;
    private String descripcionActividad;
    private int id;
    private LocalDate fecha;
    private LocalTime hora;

    //Este es para generar IDs automaticamente, es el primero que se usa.
    public Actividad(String nombreActividad, String descripcionActividad, LocalDate fecha, LocalTime hora) {
        this.nombreActividad = nombreActividad;
        this.descripcionActividad = descripcionActividad;
        this.id = contadorId++;
        this.fecha = fecha;
        this.hora = hora;
    }

    //Este es para cuando se cargan datos de los archivos, asi conserva los IDs ya existentes.
    public Actividad(String nombreActividad, String descripcionActividad, int id, LocalDate fecha, LocalTime hora) {
        this.nombreActividad = nombreActividad;
        this.descripcionActividad = descripcionActividad;
        this.id = id;
        this.fecha = fecha;
        this.hora = hora;
    }
    
    public String getNombreActividad() {
        return nombreActividad;
    }

    public String getDescripcionActividad() {
        return descripcionActividad;
    }

    public int getId() {
        return id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setNombreActividad(String nombreActividad) {
        this.nombreActividad = nombreActividad;
    }

    public void setDescripcionActividad(String descripcionActividad) {
        this.descripcionActividad = descripcionActividad;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }
    
    @Override
    public String toString(){
        return "ID: " + this.id + 
               "\nTitulo: " + this.nombreActividad +
               "\nDescripcion: " + this.descripcionActividad +
               "\nFecha: " + this.fecha + 
               "\nHora: " + this.hora;    
    }
    
}
