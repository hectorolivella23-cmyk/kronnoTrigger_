package controlador;
import modelo.Usuario;

import java.util.ArrayList;

public class GestorUsuarios {
    
    private ArrayList<Usuario> usuarios;

    public GestorUsuarios(ArrayList<Usuario> usuarios) {
        this.usuarios = usuarios;
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
    
    public Usuario buscarUsuario(String nombreUsuario){
        
        for(Usuario usuario: usuarios){
            if(usuario.getUser().equalsIgnoreCase(nombreUsuario)){
                return usuario;
            }
        }
        return null;
    }
    
    public boolean eliminarUsuario(String nombreUsuario){
        
        Usuario usuario = buscarUsuario(nombreUsuario);
        
        if(usuario != null){
            usuarios.remove(usuario);
            return true;
        }
        return false;
    }

    public ArrayList<Usuario> getUsuarios() {
        return usuarios;
    }
    
    
    
}
