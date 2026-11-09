package controlador;
import modelo.Usuario;

public class Autentificacion {
    
    private GestorUsuarios gestor; 
    private Usuario usuarioActual;
    
    public Autentificacion(GestorUsuarios gestor){
        this.gestor = gestor;
        this.usuarioActual = null;
    }
    public Autentificacion(){
        this(new GestorUsuarios());
    }
    
    public ResultadoAutentificacion registrar(String usuario, String contraseña){
        
        if (faltanDatos(usuario, contraseña)){
            return ResultadoAutentificacion.faltanDatos;
        }
        boolean registrado = gestor.registrarUsuario(new Usuario(usuario, contraseña));
        
        if(!registrado){
            return ResultadoAutentificacion.usuarioEnUso;
        }
            return ResultadoAutentificacion.valido;
        }
    
    public ResultadoAutentificacion iniciarSesion(String usuario, String contraseña){
        if (faltanDatos(usuario, contraseña)){
            return ResultadoAutentificacion.faltanDatos;
        }
        
        Usuario encontrado = gestor.iniciarSesion(usuario, contraseña);
        
        if(encontrado == null){
            return ResultadoAutentificacion.datosIncorrectos;
        }
       
        
        this.usuarioActual = encontrado;
        return ResultadoAutentificacion.valido;
    }
    
    public void cerrarSesion(){
        this.usuarioActual = null;
    }
    
    public boolean iniciarSesion(){
        return this.usuarioActual != null;
    }
    
    public Usuario getUsuarioActual(){
        return usuarioActual;
    }
    
    private boolean estaVacio(String algo){
        return algo == null || algo.isEmpty();
    }
    
    private boolean faltanDatos(String usuario, String contraseña){
        return estaVacio(usuario) || estaVacio(contraseña);
    }
    
}