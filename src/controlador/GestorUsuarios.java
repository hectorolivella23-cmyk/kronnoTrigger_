package controlador;
import modelo.Usuario;

import java.util.ArrayList;

public class GestorUsuarios {

    public GestorUsuarios() {
        usuarios = new ArrayList<>();
    }
    
    public boolean registrarUsuario(Usuario usuario){
        
        if(buscarUsuario(usuario.getUser()) != null){
            return false;
        }
        
        usuarios.add(usuario);
        return true;
        
    }
    
    public Usuario iniciarSesion(String nombreUsuario, String contraseña){
        
        Usuario usuario = buscarUsuario(nombreUsuario);
        
        if(usuario != null && usuario.getPassword().equals(contraseña)){
            return usuario;
        }
        return null;    
    }
    
    public Usuario buscarUsuario(Usuario usuario, int id){
        usuario.
    }
    
    public boolean eliminarUsuario(Usuario usuario, int id){
        
         return usuario.eliminarActividad(id);
    }
    
    
    
}
