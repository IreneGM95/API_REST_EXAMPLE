package com.example.utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

@Component
public class FileDownloadUtil {
    
    private Path foundFile;

    public Resource getFileAsResource(String fileCode) throws IOException {

        Path dirPath = Paths.get("Files-Upload");

        Files.list(dirPath).forEach(file -> {
            if(file.getFileName().toString().startsWith(fileCode)) {
                foundFile = file;
//Por cada elemento de la carpeta files-upload, asumiendo que son todos files, analiza el nombre. Devuelve nada (es un return vacio), es como si fuese un break
                return ;
            }
        });

        if(foundFile != null) {
            return new UrlResource(foundFile.toUri());
        }
//si no encuentra el fichero, devuelve null
        return null;
    }
}
