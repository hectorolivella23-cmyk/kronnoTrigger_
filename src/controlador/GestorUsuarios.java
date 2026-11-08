package controlador;
import modelo.Usuario;

import java.util.ArrayList;

public class GestorUsuarios {

    private ArrayList<Usuario> usuarios;
 
    public GestorUsuarios() {
        usuarios = new ArrayList<>();
    }
 
    public boolean registrarUsuario(Usuario usuario) {
 
        if (buscarUsuario(usuario.getUser()) != null) {
            return false;
        }
 
        usuarios.add(usuario);
        return true;
    }
 
    public Usuario iniciarSesion(String nombreUsuario, String contraseña) {
 
        Usuario usuario = buscarUsuario(nombreUsuario);
 
        if (usuario != null && usuario.getPassword().equals(contraseña)) {
            return usuario;
        }
        return null;
    }
 
    public Usuario buscarUsuario(String nombreUsuario) {
 
        for (Usuario u : usuarios) {
            if (u.getUser().equals(nombreUsuario)) {
                return u;
            }
        }
        return null;
    }
 
    public boolean eliminarUsuario(String nombreUsuario) {
 
        Usuario usuario = buscarUsuario(nombreUsuario);
 
        if (usuario == null) {
            return false;
        }
 
        return usuarios.remove(usuario);
    }
 
    public ArrayList<Usuario> listarUsuarios() {
        return usuarios;
    }
 
}
