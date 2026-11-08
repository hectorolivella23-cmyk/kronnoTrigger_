package logica;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import modelo.Actividad;
import modelo.EstadoTarea;
import modelo.HorarioRecurrente;
import modelo.Tarea;
import modelo.Usuario;

public class Notificador {
    
    private static final int HORAS_DE_ANTICIPACION = 24;
 
    private ArrayList<String> avisados;
 
    public Notificador() {
        avisados = new ArrayList<>();
    }
 
    public ArrayList<String> generarAvisos(Usuario usuario) {
        return generarAvisos(usuario, LocalDateTime.now());
    }

    public ArrayList<String> generarAvisos(Usuario usuario, LocalDateTime ahora) {
 
        ArrayList<String> avisos = new ArrayList<>();
 
        for(Actividad actividad : usuario.getActividades()) {
 
            LocalDateTime momento = momentoProximo(actividad, ahora);
 
            if(momento == null) {
                continue;
            }
 
            String clave = actividad.getId() + "@" + momento;
 
            if(!avisados.contains(clave)) {
                avisados.add(clave);
                avisos.add(construirMensaje(actividad, momento, ahora));
            }
        }
        return avisos;
    }
 
    public ArrayList<Actividad> buscarActividadesProximas(Usuario usuario, LocalDateTime ahora) {
 
        ArrayList<Actividad> resultado = new ArrayList<>();
 
        for(Actividad actividad : usuario.getActividades()) {
            if(momentoProximo(actividad, ahora) != null) {
                resultado.add(actividad);
            }
        }
        return resultado;
    }
 
    //Pone al dia el estado de todas las tareas (por ejemplo, marca como VENCIDA la que ya paso).
    //El Timer puede llamarlo en cada revision para que los indicadores visuales no queden desactualizados.
    public void actualizarEstados(Usuario usuario) {
 
        for(Actividad actividad : usuario.getActividades()) {
            if(actividad instanceof Tarea) {
                ((Tarea) actividad).actualizarEstado();
            }
        }
    }
 
    //Devuelve la fecha y hora de la actividad si cae dentro de las proximas 24 horas, o null si no.
    private LocalDateTime momentoProximo(Actividad actividad, LocalDateTime ahora) {
 
        if(actividad instanceof Tarea) {
            Tarea tarea = (Tarea) actividad;
            //Una tarea ya completada no necesita recordatorio.
            if(tarea.getEstado() == EstadoTarea.COMPLETADA) {
                return null;
            }
            LocalDateTime limite = LocalDateTime.of(tarea.getFecha(), tarea.getHora());
            return estaEnLaVentana(limite, ahora) ? limite : null;
        }
 
        if(actividad instanceof HorarioRecurrente) {
            HorarioRecurrente horario = (HorarioRecurrente) actividad;
            //Un horario se repite cada semana, asi que se revisan todas sus repeticiones y no solo la primera fecha.
            //Se usa la hora de inicio, que es la hora a la que empieza cada clase o reunion.
            for(LocalDate fecha : horario.generarRepeticiones()) {
                LocalDateTime inicio = LocalDateTime.of(fecha, horario.getHoraInicio());
                if(estaEnLaVentana(inicio, ahora)) {
                    return inicio;
                }
            }
            return null;
        }
 
        LocalDateTime momento = LocalDateTime.of(actividad.getFecha(), actividad.getHora());
        return estaEnLaVentana(momento, ahora) ? momento : null;
    }
 
    //Esta en la ventana si todavia no ha pasado y faltan 24 horas o menos.
    private boolean estaEnLaVentana(LocalDateTime momento, LocalDateTime ahora) {
        return !momento.isBefore(ahora) && !momento.isAfter(ahora.plusHours(HORAS_DE_ANTICIPACION));
    }
 
    private String construirMensaje(Actividad actividad, LocalDateTime momento, LocalDateTime ahora) {
 
        //Como faltan 24 horas o menos, el momento cae entre el dia de hoy o el de mañana.
        String cuando = momento.toLocalDate().equals(ahora.toLocalDate()) ? "Hoy" : "Mañana";
 
        if(actividad instanceof Tarea) {
            return cuando + " a las " + momento.toLocalTime() + " vence la tarea \"" + actividad.getNombreActividad() + "\".";
        } else if(actividad instanceof HorarioRecurrente) {
            HorarioRecurrente horario = (HorarioRecurrente) actividad;
            return cuando + " tienes \"" + horario.getNombreActividad() + "\" de " + horario.getHoraInicio() + " a " + horario.getHoraFin() + ".";
        }
        return cuando + " a las " + momento.toLocalTime() + " tienes la actividad \"" + actividad.getNombreActividad() + "\".";
    }
    
}
