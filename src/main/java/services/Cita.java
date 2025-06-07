/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import java.io.Serializable;
import java.util.Date;


/**
 *
 * @author Asus
 */
public class Cita implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private Date fecha;
    private String hora;
    private String motivo;
    private String medicoAsignado;
    private String pacienteAsignado;
    private String nombreMedico; // Para mostrar en la interfaz
    private String nombrePaciente; // Para mostrar en la interfaz
    
    public Cita() {}
    
    public Cita(int id, Date fecha, String hora, String motivo, String medicoAsignado, String pacienteAsignado) {
        this.id = id;
        this.fecha = fecha;
        this.hora = hora;
        this.motivo = motivo;
        this.medicoAsignado = medicoAsignado;
        this.pacienteAsignado = pacienteAsignado;
    }
    
    // Getters y setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public Date getFecha() {
        return fecha;
    }
    
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    
    public String getHora() {
        return hora;
    }
    
    public void setHora(String hora) {
        this.hora = hora;
    }
    
    public String getMotivo() {
        return motivo;
    }
    
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
    
    public String getMedicoAsignado() {
        return medicoAsignado;
    }
    
    public void setMedicoAsignado(String medicoAsignado) {
        this.medicoAsignado = medicoAsignado;
    }
    
    public String getPacienteAsignado() {
        return pacienteAsignado;
    }
    
    public void setPacienteAsignado(String pacienteAsignado) {
        this.pacienteAsignado = pacienteAsignado;
    }
    
    public String getNombreMedico() {
        return nombreMedico;
    }
    
    public void setNombreMedico(String nombreMedico) {
        this.nombreMedico = nombreMedico;
    }
    
    public String getNombrePaciente() {
        return nombrePaciente;
    }
    
    public void setNombrePaciente(String nombrePaciente) {
        this.nombrePaciente = nombrePaciente;
    }
    
    @Override
    public String toString() {
        return "Cita: " + fecha + " " + hora + " - " + motivo;
    }
}