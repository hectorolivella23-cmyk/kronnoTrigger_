package persistencia;

import modelo.Actividad;
import modelo.EstadoTarea;
import modelo.HorarioRecurrente;
import modelo.Tarea;
import modelo.Usuario;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class EscrituraLectura {
    
    //Un archivo para los usuarios y otro para las actividades.
    private static final String RUTA_USUARIOS = "archivos/usuarios.txt";
    private static final String RUTA_ACTIVIDADES = "archivos/actividades.txt";
 
    //Por simplicidad decidi poner los archivos en formato txt
 
    public ArrayList<Usuario> leerUsuarios() {
        ArrayList<Usuario> usuarios = new ArrayList<>();
        BufferedReader lectura;
 
        try {
            File archivo = new File(RUTA_USUARIOS);
            if(!archivo.exists()) {
                return usuarios; //Primera vez que se abre la app.
            }
            lectura = new BufferedReader(new FileReader(archivo));
            String linea;
            while((linea = lectura.readLine()) != null){
                if(linea.trim().isEmpty()){
                    continue;
                }
                String[] datosIndividuales = linea.split(";");
 
                String user = decodificar(datosIndividuales[0]);
                String password = decodificar(datosIndividuales[1]);
 
                usuarios.add(new Usuario(user, password));
            }
            lectura.close();
        }
        catch(Exception error) {
            System.out.println("Error: " + error.getMessage());
        }
 
        leerActividades(usuarios);
        return usuarios;
    }
 
    //Lee actividades.txt y asigna cada actividad a su usuario.
    private void leerActividades(ArrayList<Usuario> usuarios) {
        BufferedReader lectura;
 
        try{
            File archivo = new File(RUTA_ACTIVIDADES);
            if(!archivo.exists()) {
                return;
            }
            lectura = new BufferedReader(new FileReader(archivo));
            String linea;
            while((linea = lectura.readLine()) != null){
                if(linea.trim().isEmpty()) {
                    continue;
                }
                try{
                    String[] datosIndividuales = linea.split(";", -1);
 
                    String tipo = datosIndividuales[0];
                    Usuario dueño = buscarPorNombre(usuarios, decodificar(datosIndividuales[1]));
                    if(dueño == null) {
                        continue;
                    }
                    int id = Integer.parseInt(datosIndividuales[2]);
                    String nombre = decodificar(datosIndividuales[3]);
                    String descripcion = decodificar(datosIndividuales[4]);
                    LocalDate fecha = LocalDate.parse(datosIndividuales[5]);
                    LocalTime hora = LocalTime.parse(datosIndividuales[6]);
 
                    if(tipo.equals("TAREA")) {
                        Tarea tarea = new Tarea(nombre, descripcion, id, fecha, hora);
                        tarea.setEstado(EstadoTarea.valueOf(datosIndividuales[7]));
                        dueño.agregarActividad(tarea);
                    } else if(tipo.equals("HORARIO")) {
                        DayOfWeek dia = DayOfWeek.valueOf(datosIndividuales[7]);
                        LocalTime horaInicio = LocalTime.parse(datosIndividuales[8]);
                        LocalTime horaFin = LocalTime.parse(datosIndividuales[9]);
                        LocalDate fechaFin = LocalDate.parse(datosIndividuales[10]);
                        dueño.agregarActividad(new HorarioRecurrente(dia, horaInicio, horaFin, fechaFin, nombre, descripcion, id, fecha, hora));
                    } else {
                        dueño.agregarActividad(new Actividad(nombre, descripcion, id, fecha, hora));
                    }
                }
                catch(Exception error) {
                    //Una linea danada no debe impedir cargar las demas.
                    System.out.println("Linea ignorada (" + error.getMessage() + "): " + linea);
                }
            }
            lectura.close();
        }
        catch(Exception error) {
            System.out.println("Error: " + error.getMessage());
        }
    }
 
    public void escribirUsuarios(ArrayList<Usuario> usuarios) {
        BufferedWriter escrituraUsuarios;
        BufferedWriter escrituraActividades;
 
        try{
            File carpeta = new File("archivos");
            if(!carpeta.exists()){
                carpeta.mkdirs();
            }
            escrituraUsuarios = new BufferedWriter(new FileWriter(new File(RUTA_USUARIOS)));
            escrituraActividades = new BufferedWriter(new FileWriter(new File(RUTA_ACTIVIDADES)));
 
            for(Usuario u : usuarios){
                escrituraUsuarios.write(codificar(u.getUser()) + ";" + codificar(u.getPassword()));
                escrituraUsuarios.newLine();
 
                for(Actividad a : u.getActividades()) {
                    escrituraActividades.write(formatearActividad(u, a));
                    escrituraActividades.newLine();
                }
            }
            escrituraUsuarios.close();
            escrituraActividades.close();
        }
        catch(Exception error){
            System.out.println("Error: " + error.getMessage());
        }
    }
 
    private String formatearActividad(Usuario u, Actividad a) {
 
        String comunes = codificar(u.getUser()) + ";" + a.getId() + ";" + codificar(a.getNombreActividad()) + ";" +
                         codificar(a.getDescripcionActividad()) + ";" + a.getFecha() + ";" + a.getHora();
 
        if(a instanceof Tarea) {
            Tarea t = (Tarea) a;
            return "TAREA;" + comunes + ";" + t.getEstado();
        } else if(a instanceof HorarioRecurrente){
            HorarioRecurrente h = (HorarioRecurrente) a;
            return "HORARIO;" + comunes + ";" + h.getDia() + ";" + h.getHoraInicio() + ";" +
                   h.getHoraFin() + ";" + h.getFechaFinRecurrencia();
        }
        return "ACTIVIDAD;" + comunes;
    }
 
    private Usuario buscarPorNombre(ArrayList<Usuario> usuarios, String nombre){
        for(Usuario u : usuarios) {
            if(u.getUser().equals(nombre)) {
                return u;
            }
        }
        return null;
    }
 
    //Los textos escritos por el usuario pueden tener ";" o saltos de linea, que romperian el formato del archivo.
    //Por eso se reemplazan al guardar y se restauran al leer.
    private String codificar(String texto) {
        return texto.replace("%", "%25").replace(";", "%3B").replace("\n", "%0A").replace("\r", "%0D");
    }
 
    private String decodificar(String texto) {
        return texto.replace("%0D", "\r").replace("%0A", "\n").replace("%3B", ";").replace("%25", "%");
    }
 
}
    

