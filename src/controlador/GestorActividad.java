package controlador;
import java.util.ArrayList;
import modelo.Actividad;
import modelo.Usuario;

public class GestorActividad {
    
    public void agregarActividad(Usuario usuario, Actividad actividad) {
        usuario.agregarActividad(actividad);
    }
 
    public boolean eliminarActividad(Usuario usuario, int id) {
        return usuario.eliminarActividad(id);
    }
 
    public Actividad buscarActividad(Usuario usuario, int id) {
        return usuario.buscarActividad(id);
    }
 
    public boolean editarActividad(Usuario usuario, int id, Actividad nuevaActividad) {
        return usuario.editarActividad(id, nuevaActividad);
    }
 
    public ArrayList<Actividad> listarActividades(Usuario usuario) {
        return usuario.getActividades();
    }
    
}
