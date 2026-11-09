package modelo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class Tarea extends Actividad{

    private EstadoTarea estado;

    public Tarea(String nombreActividad, String descripcionActividad, int id, LocalDate fecha, LocalTime hora) {
        super(nombreActividad, descripcionActividad, id, fecha, hora);
        this.estado = EstadoTarea.PENDIENTE;
    }
    
    public Tarea(String nombreActividad, String descripsionActividad, LocalDate fecha, LocalTime hora){
        super(nombreActividad, descripsionActividad, fecha, hora);
        this.estado = EstadoTarea.PENDIENTE;
    }

    public EstadoTarea getEstado() {
        return estado;
    }

    public void setEstado(EstadoTarea estado) {
        this.estado = estado;
    }
    
    public void marcarCompletada(){
        this.estado = EstadoTarea.COMPLETADA;
    }
    
    public void marcarEnCurso(){
        this.estado = EstadoTarea.EN_CURSO;
    }
    
    public void marcarPendiente(){
        this.estado = EstadoTarea.PENDIENTE;
    }
    
    public void actualizarEstado(){
        
        LocalDateTime limite = LocalDateTime.of(getFecha(), getHora());
        
        if (limite.isBefore(LocalDateTime.now()) && estado != EstadoTarea.COMPLETADA){
            estado = EstadoTarea.VENCIDA;
        }
        
    }
    
}
