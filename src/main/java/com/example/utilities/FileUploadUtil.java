package com.example.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.apache.commons.lang3.RandomStringUtils;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUploadUtil {
    public String saveFile(String fileName, MultipartFile multipartFile)
            throws IOException {
        Path uploadPath = Paths.get("Files-Upload");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileCode = RandomStringUtils.randomAlphanumeric(8); //aunque haya varias imagenes con el mismo nombre, les añade un codigo de 8 digitos para diferenciarlos
//cuando el try tiene algo entre (), no es un try_catch normal. Es un try-with-resources. Los ponemos para que los abra, los use y los cierre cuando dejes de usarlo, elimina la necedidad de un finlly que cierre expresamente
//Los recursos que se pueden manejar son los implementados por la interfaz closeable       
try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileCode + "-" + fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            throw new IOException("Could not save file: " + fileName, ioe);
        }

        return fileCode;
    }
}
