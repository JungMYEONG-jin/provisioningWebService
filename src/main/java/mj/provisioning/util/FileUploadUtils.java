package mj.provisioning.util;

import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
public class FileUploadUtils {
    public static void saveProvisioning(String toUploadDir, String fileName, InputStream inputStream){
        Path uploadPath = Paths.get(System.getProperty("user.dir")+toUploadDir);

        if (!Files.exists(uploadPath)) {
            try{
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                log.error("Could not create Directory {}",e);
            }
        }

        try {
            fileName = fileName+".mobileprovision";
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Could not save"+fileName,e);
        }
    }

    public static void writeProvisioning(String profileContent, String name, String toUploadDir){
        byte[] data = DatatypeConverter.parseBase64Binary(profileContent);
        String fileName = name+".mobileprovision";
        Path uploadPath = Paths.get(System.getProperty("user.dir")+toUploadDir);
        if (!Files.exists(uploadPath)) {
            try{
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new RuntimeException("can not create");
            }
        }
        Path resolve = uploadPath.resolve(fileName);
        File file = new File(resolve.toString());
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(data);
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("can not create");
        }
    }

    public static void cleanDir(String dir){
        Path dirPath = Paths.get(System.getProperty("user.dir")+dir);
        try{
            Files.list(dirPath).forEach(file-> {
                if (!Files.isDirectory(file)) {
                    try {
                        Files.delete(file);
                    } catch (IOException e) {
                        log.error("Could not delete file "+ file, e);
                    }
                }
            });
        } catch (IOException e) {
            log.error("Can not list directory ", e);
        }
    }
}
