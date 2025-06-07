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
public class Paciente implements Serializable {
    private static final long serialVersionUID = 1L;

    public static Object getServiceLocator;

    private String nombre;
    private String curp;
    private String telefono;
    private String correoElectronico;
    
    public Paciente() {}
    
    public Paciente(String nombre, String curp, String telefono, String correoElectronico) {
        this.nombre = nombre;
        this.curp = curp;
        this.telefono = telefono;
        this.correoElectronico = correoElectronico;
    }
    
    // Getters y setters
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getCurp() {
        return curp;
    }
    
    public void setCurp(String curp) {
        this.curp = curp;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public String getCorreoElectronico() {
        return correoElectronico;
    }
    
    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }
    
    @Override
    public String toString() {
        return nombre + " - " + curp;
    }
}