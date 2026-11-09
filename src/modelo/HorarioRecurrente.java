package modelo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.time.temporal.TemporalAdjusters;

public class HorarioRecurrente extends Actividad{

    private DayOfWeek dia;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private LocalDate FechaFinRecurrencia;

    public HorarioRecurrente(DayOfWeek dia, LocalTime horaInicio, LocalTime horaFin, LocalDate FechaFinRecurrencia, String nombreActividad, String descripcionActividad, int id, LocalDate fecha, LocalTime hora) {
        super(nombreActividad, descripcionActividad, id, ajustarFecha(fecha, dia), hora);
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.FechaFinRecurrencia = FechaFinRecurrencia;
    }

    public HorarioRecurrente(DayOfWeek dia, LocalTime horaInicio, LocalTime horaFin, LocalDate FechaFinRecurrencia, String nombreActividad, String descripcionActividad, LocalDate fecha, LocalTime hora) {
        super(nombreActividad, descripcionActividad, ajustarFecha(fecha, dia), hora);
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.FechaFinRecurrencia = FechaFinRecurrencia;
    }

    public DayOfWeek getDia() {
        return dia;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public LocalDate getFechaFinRecurrencia() {
        return FechaFinRecurrencia;
    }

    public void setDia(DayOfWeek dia) {
        this.dia = dia;
        super.setFecha(ajustarFecha(getFecha(), dia));
    }
    
    public boolean hayConflicto(HorarioRecurrente otro){
        
        if(this.dia != otro.dia){
            return false;
        }
        
        return this.horaInicio.isBefore(otro.horaFin) && this.horaFin.isAfter(otro.horaInicio);
        
    }
    
    public ArrayList<LocalDate> generarRepeticiones(){
        
        ArrayList<LocalDate> repeticiones = new ArrayList<>();
        
        LocalDate fechaActual = this.getFecha();
        
        while(!fechaActual.isAfter(this.FechaFinRecurrencia)){
            
            repeticiones.add(fechaActual);
            fechaActual = fechaActual.plusWeeks(1);
            
        }
        return repeticiones;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
        setHora(horaInicio);
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public void setFechaFinRecurrencia(LocalDate FechaFinRecurrencia) {
        this.FechaFinRecurrencia = FechaFinRecurrencia;
    }
    
    private static LocalDate ajustarFecha(LocalDate fecha, DayOfWeek dia){
    return fecha.with(TemporalAdjusters.nextOrSame(dia));
    }
    
    @Override
    public void setFecha(LocalDate fecha){
        super.setFecha(ajustarFecha(fecha, this.dia));
    }
    
}
