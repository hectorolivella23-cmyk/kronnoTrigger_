package controlador;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import modelo.Actividad;
import modelo.EstadoTarea;
import modelo.HorarioRecurrente;
import modelo.Tarea;
import modelo.Usuario;

public class AdministrarTareas {
    
    private Usuario usuario;
    private GestorActividad gestor;
    
    public AdministrarTareas(Usuario usuario){
        this.usuario = usuario; //Antes el parametro se llamaba "usuraio" y usuario quedaba en null
        this.gestor = new GestorActividad();
    }
    
    public String validarDatos(String titulo, LocalDate fecha, LocalTime hora){
        
        if(titulo == null || titulo.isBlank()){
            return "falta el titulo";
        }
        if(fecha == null){
            return "fecha no valida";
        }
        if (hora == null){
            return "hora no valida";
        }
        return null;
    }
    
    public boolean fechaCaducada(LocalDate fecha, LocalTime hora){
        return LocalDateTime.of(fecha, hora).isBefore(LocalDateTime.now()); //esto no lo conocia
    }
    
    public Tarea crearTarea(String titulo, String descripcion, LocalDate fecha, LocalTime hora){
        
        if(validarDatos(titulo, fecha, hora) != null){
            return null;
        }
        
        Tarea tarea = new Tarea(titulo, descripcion, fecha, hora);
        gestor.agregarActividad(usuario, tarea);
        return tarea;
    }
    
    public Actividad crearEvento(String titulo, String descripcion, LocalDate fecha, LocalTime hora){
        
        if (validarDatos(titulo, fecha, hora) != null){
            return null;
        }
        
        Actividad evento = new Actividad(titulo, descripcion, fecha, hora);
        gestor.agregarActividad(usuario, evento);
        return evento;
    }
    
    //Lista las tareas y eventos del usuario (los horarios recurrentes se manejan con GestorActividad)
    public ArrayList<Actividad> listar(){
        
        ArrayList<Actividad> resultado = new ArrayList<>();
        ArrayList<Actividad> todas = gestor.listarActividades(usuario);
        
        for(int i = 0; i < todas.size(); i++){
            
            Actividad actividad = todas.get(i);
            boolean Horario = actividad instanceof HorarioRecurrente;
            
            if(Horario == false){
                resultado.add(actividad);
            }
        }
            return resultado;
        }
        
        public Actividad buscar (int id){
            
            for (Actividad actividad:listar()){
                if(actividad.getId() == id){
                    return actividad;
                }
        }
            return null;
  
    }
        
        public ArrayList<Tarea> filtrarPorEstado(EstadoTarea estado){
            
            ArrayList<Tarea> resultado = new ArrayList<>();
            
            for (Actividad actividad:listar()){
                if(actividad instanceof Tarea && ((Tarea)actividad).getEstado() == estado){
                    resultado.add((Tarea) actividad);
                }
            }
            return resultado;
            }
        
        public ArrayList<Actividad> filtrarPorFecha(LocalDate fecha){
            ArrayList<Actividad> resultado = new ArrayList<>();
            
            for(Actividad actividad:listar()){
                if(actividad.getFecha().equals(fecha)){
                    resultado.add(actividad);
                }
            }
            return resultado;
        }
    
        public boolean confirmacionEdicion(int id){
            Actividad actividad = buscar(id);
            return actividad instanceof Tarea && ((Tarea) actividad).getEstado() == EstadoTarea.COMPLETADA;
        }
        
        public ResultadoTareas editar(int id, String titulo, String descripcion, LocalDate fecha, LocalTime hora, boolean confirmado){
            
            Actividad actividad = buscar(id);
            
            if(actividad == null){
                return ResultadoTareas.noEncontrado;
            }
            if(confirmacionEdicion(id) && !confirmado){
                return ResultadoTareas.porConfirmar;
            }
            if(validarDatos(titulo, fecha, hora) != null){
                return ResultadoTareas.datosInvalidos;
            }
            
            actividad.setNombreActividad(titulo);
            actividad.setDescripcionActividad(descripcion);
            actividad.setFecha(fecha);
            actividad.setHora(hora);
            
            if(actividad instanceof Tarea){
                Tarea tarea = (Tarea) actividad;
                if (tarea.getEstado() == EstadoTarea.VENCIDA){
                    tarea.marcarPendiente();
                }
                tarea.actualizarEstado();
            }
                return ResultadoTareas.Cambiado;
        }
            
            public ResultadoTareas cambiarEstado(int id, EstadoTarea modificacion){
                Actividad actividad = buscar(id);
                if(!(actividad instanceof Tarea) || modificacion == null){
                    return ResultadoTareas.noEncontrado;
                }
                ((Tarea) actividad).setEstado(modificacion);
                return ResultadoTareas.Cambiado;
                
            }
            
            public int ActualizarCambios(){
                int cambios = 0;
                
                for(Actividad actividad:listar()){
                    if(actividad instanceof Tarea){
                        Tarea tarea = (Tarea) actividad;
                        EstadoTarea antes = tarea.getEstado();
                        tarea.actualizarEstado();
                        if(tarea.getEstado() != antes){
                            cambios++;
                        }
                    }
                }
                return cambios;
            }
            
            public ResultadoTareas eliminar(int id){
                
                if (buscar(id) == null){
                    return ResultadoTareas.noEncontrado;
                }
                
                gestor.eliminarActividad(usuario, id);
                return ResultadoTareas.Cambiado;
            }
        
}
