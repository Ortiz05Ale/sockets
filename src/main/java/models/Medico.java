/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.io.Serializable;

/**
 *
 * @author Asus
 */
public class Medico implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombre;
    private String especialidad;
    private String cedula;
    private String correoElectronico;
    
    public Medico() {}
    
    public Medico(String nombre, String especialidad, String cedula, String correoElectronico) {
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.cedula = cedula;
        this.correoElectronico = correoElectronico;
    }
    
    // Getters y setters
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getEspecialidad() {
        return especialidad;
    }
    
    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }
    
    public String getCedula() {
        return cedula;
    }
    
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }
    
    public String getCorreoElectronico() {
        return correoElectronico;
    }
    
    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }
    
    @Override
    public String toString() {
        return nombre + " - " + especialidad;
    }
}