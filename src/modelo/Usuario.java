package modelo;

import java.util.ArrayList;
import java.util.Iterator;

public class Usuario {
    
    private String user;
    private String password;
    private ArrayList<Actividad> actividades;
 
    public Usuario(String user, String password) {
        this.user = user;
        this.password = password;
        this.actividades = new ArrayList<>();
    }
 
    public String getUser() {
        return user;
    }
 
    public String getPassword() {
        return password;
    }
    
    public void setUser(String username) {
        this.user = username;
    }
    
    public void setPassword(String pass) {
        this.password = pass;
    }
 
    public ArrayList<Actividad> getActividades() {
        return actividades;
    }
 
    public void agregarActividad(Actividad activity) {
        this.actividades.add(activity);
    }
 
    public boolean eliminarActividad(int id) {
 
        Iterator<Actividad> it = actividades.iterator();
 
        while (it.hasNext()) {
 
            Actividad actividad = it.next();
 
            if (actividad.getId() == id) {
                it.remove();
                return true;
            }
 
        }
 
        return false;
    }
 
    // Busca por ID de la actividad (no por posicion en la lista).
    public Actividad buscarActividad(int id) {
 
        for (Actividad actividad : actividades) {
            if (actividad.getId() == id) {
                return actividad;
            }
        }
        return null;
    }
 
    public boolean editarActividad(int id, Actividad nueva) {
 
        Actividad existente = buscarActividad(id);
 
        if (existente == null) {
            return false;
        }
        
        if (existente instanceof HorarioRecurrente && nueva instanceof HorarioRecurrente){
            ((HorarioRecurrente) existente).setDia(((HorarioRecurrente) nueva).getDia());
        }
 
        existente.setNombreActividad(nueva.getNombreActividad());
        existente.setDescripcionActividad(nueva.getDescripcionActividad());
        existente.setFecha(nueva.getFecha());
        existente.setHora(nueva.getHora());
        
        if(existente instanceof HorarioRecurrente && nueva instanceof HorarioRecurrente){
            HorarioRecurrente horario = (HorarioRecurrente) existente;
            HorarioRecurrente otro = (HorarioRecurrente) nueva;
            horario.setHoraInicio(otro.getHoraInicio());
            horario.setHoraFin(otro.getHoraFin());
            horario.setFechaFinRecurrencia(otro.getFechaFinRecurrencia());
        }
        
        if(existente instanceof Tarea){
            Tarea tarea = (Tarea) existente;
            if(tarea.getEstado() == EstadoTarea.VENCIDA){
                tarea.marcarPendiente();
            }
            tarea.actualizarEstado();
        }
        
        return true;
    
    }
    
    @Override
    public String toString(){
        return "Usuario: " + this.user +
               "\nNumero de actividades registradas: " + this.actividades.size();
    }
    
}
