package client;

import services.ServiceLocator;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Cliente {
    private static ServiceLocator serviceLocator;
    
    public static ServiceLocator getServiceLocator() {
        if (serviceLocator == null) {
            try {
                Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                serviceLocator = (ServiceLocator) registry.lookup("ServiceLocator");
            } catch (Exception e) {
                System.err.println("Error al conectar con el servidor: " + e.toString());
                e.printStackTrace();
            }
        }
        return serviceLocator;
    }
    
    public static void main(String[] args) {
        try {
            // Obtener el localizador de servicios
            ServiceLocator serviceLocator = getServiceLocator();
            
            if (serviceLocator != null) {
                System.out.println("Conexión con el servidor establecida correctamente.");
                
                // Iniciar la interfaz gráfica
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
            } else {
                System.err.println("No se pudo establecer conexión con el servidor.");
            }
            
        } catch (Exception e) {
            System.err.println("Error en el cliente: " + e.toString());
            e.printStackTrace();
        }
    }
}