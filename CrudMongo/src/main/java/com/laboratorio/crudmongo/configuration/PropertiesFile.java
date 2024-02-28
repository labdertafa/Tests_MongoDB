package com.laboratorio.crudmongo.configuration;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesFile {
    private final String fileName;

    public PropertiesFile(String fileName) {
        this.fileName = fileName;
    }
    
    public Properties readPropertiesFile() throws Exception {
        Properties properties = null;
        
        try {
            try (InputStream inputStream = PropertiesFile.class.getClassLoader().getResourceAsStream(fileName)) {
                if (inputStream == null) {
                    throw new Exception("No se pudo abrir el fichero de propiedades");
                }
                properties = new Properties();
                properties.load(inputStream);
                inputStream.close();
            }
        } catch (Exception e) {
            throw e;
        }
        
        return properties;
    }
}