package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import models.ServiceLocator;

public class Server {
    public static void main(String[] args) {
        try {
            // Crear el registro RMI 
            Registry registry = LocateRegistry.createRegistry(1099);
            
            // Crear e instanciar el localizador de servicios
            ServiceLocator serviceLocator = new ServiceLocatorImpl();
            
            // Registrar el localizador de servicios en el registro RMI
            registry.rebind("ServiceLocator", serviceLocator);
            
            System.out.println("Servidor RMI iniciado exitosamente.");
            System.out.println("Esperando conexiones");
            
        } catch (Exception e) {
            System.err.println("Error en el servidor: " + e.toString());
            e.printStackTrace();
        }
    }
}