package Pruebas;

import controlador.Autentificacion;
import controlador.AdministrarTareas;
import controlador.ResultadoAutentificacion;
import controlador.ResultadoTareas;
import controlador.GestorUsuarios;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import modelo.Actividad;
import modelo.EstadoTarea;
import modelo.HorarioRecurrente;
import modelo.Tarea;
import modelo.Usuario;

public class PruebaApp {

    private static int pasadas = 0;
    private static int falladas = 0;

    public static void main(String[] args) {

        pruebasAutentificacion();
        pruebasRegistroDeActividades();
        pruebasConsulta();
        pruebasEdicion();
        pruebasEstados();
        pruebasEliminacion();

        System.out.println("Pruebas pasadas: " + pasadas + " | falladas: " + falladas);
    }

    private static void verificar(String descripcion, boolean condicion) {
        if (condicion) {
            pasadas++;
            System.out.println("bien " + descripcion);
        } else {
            falladas++;
            System.out.println("MAL " + descripcion);
        }
    }

    private static void pruebasAutentificacion() {

        System.out.println("\n--- Autentificacion (RF1, RF2) ---");
        Autentificacion auten1 = new Autentificacion();

        verificar("RF1 CU001 Usuario disponible", auten1.registrar("Hector", "2269112") == ResultadoAutentificacion.valido);
        verificar("RF1 CU014 nombre en uso", auten1.registrar("Hector", "2263115") == ResultadoAutentificacion.usuarioEnUso);
        verificar("RF1 CU014 mayusculas = usuario distinto", auten1.registrar("hector", "2269112") == ResultadoAutentificacion.valido);
        verificar("RF1 CU013 Usuario vacio", auten1.registrar("", "1234") == ResultadoAutentificacion.faltanDatos);
        verificar("RF1 CU013 Contrasena vacia", auten1.registrar("Hector", "") == ResultadoAutentificacion.faltanDatos);
        verificar("RF1 CU013 no hay datos", auten1.registrar(null, null) == ResultadoAutentificacion.faltanDatos);

        verificar("RF2 CU002 ingresa con datos correctos", auten1.iniciarSesion("hector", "2269112") == ResultadoAutentificacion.valido);
        verificar("RF2 CU002 Queda la sesion abierta", auten1.iniciarSesion());
        verificar("RF2 CU002 El usuario actual es el que entro", auten1.getUsuarioActual().getUser().equals("hector"));

        auten1.cerrarSesion();
        verificar("RF2 Cerrar sesion", !auten1.iniciarSesion());
        verificar("RF2 CU016 Contrasena incorrecta", auten1.iniciarSesion("hector", "9999") == ResultadoAutentificacion.datosIncorrectos);
        verificar("RF2 CU016 Usuario inexistente cuenta como contrasena incorrecta", auten1.iniciarSesion("RiotGames", "1234") == ResultadoAutentificacion.datosIncorrectos);
        verificar("RF2 CU002 Campos vacios", auten1.iniciarSesion("", "") == ResultadoAutentificacion.faltanDatos);
        verificar("RF2 un login fallido no inicia sesion", !auten1.iniciarSesion());

        ArrayList<Usuario> cargados = new ArrayList<>();
        cargados.add(new Usuario("Lyann", "contraseña"));
        Autentificacion cargas = new Autentificacion(new GestorUsuarios(cargados));
        verificar("RF2 RF12 Un usuario cargado desde archivo puede iniciar sesion", cargas.iniciarSesion("Lyann", "contraseña") == ResultadoAutentificacion.valido);
        verificar("RF1 RF12 No deja registrar un nombre que ya estaba en el archivo", cargas.registrar("Lyann", "blablabla") == ResultadoAutentificacion.usuarioEnUso);
    }

    private static void pruebasRegistroDeActividades() {

        AdministrarTareas adm = new AdministrarTareas(new Usuario("luis", "1234"));
        LocalDate manana = LocalDate.now().plusDays(1);
        LocalTime hora = LocalTime.of(10, 0);

        Tarea t1 = adm.crearTarea("Entregar lab", "Laboratorio 9", manana, hora);
        Tarea t2 = adm.crearTarea("Estudiar POO", "Parcial", manana, hora);
        Actividad evento = adm.crearEvento("Charla", "Auditorio", manana, hora);

        verificar("RF3 CU003 Crea una tarea valida", t1 != null);
        verificar("RF3 CU003 La tarea nace en estado PENDIENTE", t1.getEstado() == EstadoTarea.PENDIENTE);
        verificar("RF3 Crea un evento (actividad sin estado)", evento != null && !(evento instanceof Tarea));
        verificar("RF3 Cada actividad recibe un ID distinto", t1.getId() != t2.getId() && t2.getId() != evento.getId());

        verificar("RF3 CU017 Sin titulo no crea", adm.crearTarea("  ", "x", manana, hora) == null);
        verificar("RF3 CU017 Sin fecha no crea", adm.crearTarea("Algo", "x", null, hora) == null);
        verificar("RF3 CU017 Sin hora no crea", adm.crearEvento("Algo", "x", manana, null) == null);
        verificar("RF3 CU017 El mensaje dice que campo falta", adm.validarDatos("", manana, hora).equals("falta el titulo"));
        verificar("RF3 CU017 Datos completos no dan mensaje", adm.validarDatos("Algo", manana, hora) == null);

        LocalDateTime hace1min = LocalDateTime.now().minusMinutes(1);
        LocalDateTime en1min = LocalDateTime.now().plusMinutes(1);
        verificar("RF3 CU018 Ayer es fecha pasada", adm.fechaCaducada(LocalDate.now().minusDays(1), hora));
        verificar("RF3 CU018 Manana no es fecha pasada", !adm.fechaCaducada(manana, hora));
        verificar("RF3 CU018 Hoy con una hora que ya paso, es pasada", adm.fechaCaducada(hace1min.toLocalDate(), hace1min.toLocalTime()));
        verificar("RF3 CU018 Hoy con una hora que no ha llegado, no es pasada", !adm.fechaCaducada(en1min.toLocalDate(), en1min.toLocalTime()));
        verificar("RF3 CU018 Una fecha pasada igual se puede guardar", adm.crearTarea("Atrasada", "x", LocalDate.now().minusDays(2), hora) != null);
    }

    private static void pruebasConsulta() {

        Usuario usuario = new Usuario("luis", "1234");
        AdministrarTareas admin = new AdministrarTareas(usuario);
        LocalDate manana = LocalDate.now().plusDays(1);
        LocalTime hora = LocalTime.of(10, 0);

        verificar("RF6/CU006 Sin actividades devuelve lista vacia, no null", admin.listar().isEmpty());

        Tarea t1 = admin.crearTarea("Lab", "x", manana, hora);
        Tarea t2 = admin.crearTarea("Parcial", "x", manana.plusDays(2), hora);
        admin.crearEvento("Charla", "x", manana, hora);
        usuario.agregarActividad(new HorarioRecurrente(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(10, 0), manana.plusWeeks(4), "Calculo", "Clase", 900, manana, LocalTime.of(8, 0)));
        t2.marcarCompletada();

        verificar("RF6 CU006 Lista tareas y eventos pero no horarios recurrentes", admin.listar().size() == 3);
        verificar("RF6 Busca por ID existente", admin.buscar(t1.getId()) == t1);
        verificar("RF6 Busca por ID inexistente devuelve null", admin.buscar(9999) == null);
        verificar("RF6 No encuentra un horario recurrente", admin.buscar(900) == null);
        verificar("RF6 CU021 Filtra por estado COMPLETADA", admin.filtrarPorEstado(EstadoTarea.COMPLETADA).size() == 1);
        verificar("RF6 CU021 Filtro por estado sin coincidencias queda vacio", admin.filtrarPorEstado(EstadoTarea.VENCIDA).isEmpty());
        verificar("RF6 CU021 Filtra por fecha (una tarea y un evento)", admin.filtrarPorFecha(manana).size() == 2);
    }

    private static void pruebasEdicion() {

        System.out.println("\n--- Edicion (RF4) ---");
        AdministrarTareas ejemplo = new AdministrarTareas(new Usuario("luis", "1234"));
        LocalDate manana = LocalDate.now().plusDays(1);
        LocalTime hora = LocalTime.of(10, 0);
        Tarea t = ejemplo.crearTarea("Lab", "x", manana, hora);
        int id = t.getId();

        verificar("RF4 CU004 Edita con datos validos", ejemplo.editar(id, "Lab 9", "Con informe", manana.plusDays(1), hora, false) == ResultadoTareas.Cambiado);
        verificar("RF4 CU004 El cambio se refleja en la tarea", t.getNombreActividad().equals("Lab 9") && t.getFecha().equals(manana.plusDays(1)));
        verificar("RF4 CU004 Conserva su ID", ejemplo.buscar(id) == t);
        verificar("RF4 CU004 Datos invalidos no se guardan", ejemplo.editar(id, "", "x", manana, hora, false) == ResultadoTareas.datosInvalidos);
        verificar("RF4 CU004 El titulo no cambio tras el error", t.getNombreActividad().equals("Lab 9"));
        verificar("RF4 ID inexistente", ejemplo.editar(9999, "x", "x", manana, hora, false) == ResultadoTareas.noEncontrado);

        t.marcarCompletada();
        verificar("RF4 CU019 Una tarea completada requiere confirmar", ejemplo.confirmacionEdicion(id));
        verificar("RF4 CU019 Sin confirmacion devuelve porConfirmar", ejemplo.editar(id, "Nuevo", "x", manana, hora, false) == ResultadoTareas.porConfirmar);
        verificar("RF4 CU019 Sin confirmacion no se modifica nada", t.getNombreActividad().equals("Lab 9"));

        t.setFecha(LocalDate.now().minusDays(3));
        t.marcarPendiente();
        t.actualizarEstado();
        verificar("RF4 CU019 Una tarea vencida queda VENCIDA", t.getEstado() == EstadoTarea.VENCIDA);
        verificar("RF4 CU019 Si se edita con fecha futura deja de estar VENCIDA", ejemplo.editar(id, "Nuevo", "x", manana, hora, false) == ResultadoTareas.Cambiado && t.getEstado() == EstadoTarea.PENDIENTE);

        t.marcarCompletada();
        verificar("RF4 CU019 Con confirmacion si se edita", ejemplo.editar(id, "Nuevo", "x", manana, hora, true) == ResultadoTareas.Cambiado && t.getNombreActividad().equals("Nuevo"));
    }

    private static void pruebasEstados() {

        AdministrarTareas ejemplo = new AdministrarTareas(new Usuario("luis", "1234"));
        LocalDate ayer = LocalDate.now().minusDays(1);
        LocalDate manana = LocalDate.now().plusDays(1);
        LocalTime hora = LocalTime.of(10, 0);

        Tarea manual = ejemplo.crearTarea("Manual", "x", manana, hora);
        Actividad evento = ejemplo.crearEvento("Evento", "x", manana, hora);

        verificar("RF9 CU009 Cambio manual a EN_CURSO", ejemplo.cambiarEstado(manual.getId(), EstadoTarea.EN_CURSO) == ResultadoTareas.Cambiado && manual.getEstado() == EstadoTarea.EN_CURSO);
        verificar("RF9 Un evento no tiene estado", ejemplo.cambiarEstado(evento.getId(), EstadoTarea.EN_CURSO) == ResultadoTareas.noEncontrado);
        verificar("RF9 Estado nulo se rechaza", ejemplo.cambiarEstado(manual.getId(), null) == ResultadoTareas.noEncontrado);

        Tarea pendiente = ejemplo.crearTarea("Pendiente vieja", "x", ayer, hora);
        Tarea enCurso = ejemplo.crearTarea("En curso vieja", "x", ayer, hora);
        enCurso.marcarEnCurso();
        Tarea completada = ejemplo.crearTarea("Completada vieja", "x", ayer, hora);
        completada.marcarCompletada();

        int cambiadas = ejemplo.ActualizarCambios();
        verificar("RF9 CU023 Pendiente con fecha pasada pasa a VENCIDA", pendiente.getEstado() == EstadoTarea.VENCIDA);
        verificar("RF9 CU023 En curso con fecha pasada pasa a VENCIDA", enCurso.getEstado() == EstadoTarea.VENCIDA);
        verificar("RF9 CU023 Completada nunca pasa a VENCIDA", completada.getEstado() == EstadoTarea.COMPLETADA);
        verificar("RF9 CU023 Una tarea con fecha futura no cambia", manual.getEstado() == EstadoTarea.EN_CURSO);
        verificar("RF9 CU023 Informa cuantas cambiaron (para guardarlas)", cambiadas == 2);
        verificar("RF9 CU023 Una segunda revision ya no cambia nada", ejemplo.ActualizarCambios() == 0);
    }

    private static void pruebasEliminacion() {

        Usuario usuario = new Usuario("luis", "1234");
        AdministrarTareas crud = new AdministrarTareas(usuario);
        LocalDate manana = LocalDate.now().plusDays(1);
        LocalTime hora = LocalTime.of(10, 0);

        Tarea t = crud.crearTarea("Lab", "x", manana, hora);
        Actividad evento = crud.crearEvento("Charla", "x", manana, hora);
        usuario.agregarActividad(new HorarioRecurrente(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(10, 0), manana.plusWeeks(4), "Calculo", "Clase", 900, manana, LocalTime.of(8, 0)));

        verificar("RF5 CU005 Elimina una tarea existente", crud.eliminar(t.getId()) == ResultadoTareas.Cambiado);
        verificar("RF5 CU005 Ya no se encuentra", crud.buscar(t.getId()) == null);
        verificar("RF5 CU005 Eliminar de nuevo da noEncontrado", crud.eliminar(t.getId()) == ResultadoTareas.noEncontrado);
        verificar("RF5 Tambien elimina un evento", crud.eliminar(evento.getId()) == ResultadoTareas.Cambiado);
        verificar("RF5 No borra un horario recurrente", crud.eliminar(900) == ResultadoTareas.noEncontrado && usuario.getActividades().size() == 1);
    }

}
