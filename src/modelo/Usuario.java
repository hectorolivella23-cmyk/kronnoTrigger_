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

    public ArrayList<Actividad> getActividades() {
        return actividades;
    }
    
    public void agregarActividad(Actividad activity){
        this.actividades.add(activity);
    }
    
    public boolean eliminarActividad(int id){
        
        Iterator<Actividad> it = actividades.iterator();
        
        while(it.hasNext()){
            
            Actividad actividad = it.next();
            
            if(actividad.getId() == id){
                it.remove();
                return true;
            }
            
        }
        
        return false;

    } 
    
    public Actividad buscarActividad(int id){
        return this.actividades.get(id);
    }
    
    public void editarActividad(int id, Actividad nueva){//Corregir esto
        this.actividades.add(id, nueva);
    }
    
    @Override
    public String toString(){
        return "Usuario: " + this.user +
               "\nNumero de actividades registradas: " + this.actividades.size();
    }
    
}
