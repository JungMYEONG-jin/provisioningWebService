package mj.provisioning.util;

import lombok.extern.slf4j.Slf4j;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.diff.SVNDeltaGenerator;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
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

    public static SVNCommitInfo addFile(ISVNEditor editor, String filePath, byte[] data) throws SVNException {

        /**
         * 항상 첫번째를 연다. 현재 root dir을 연다. 즉 다른 엔트리가 열리거나 추가전까진 해당 dir에서 작업을 한다는걸 의미한다.
         * -1 is head
         */
        editor.openRoot(-1);
        /**
         * 현재 repository에 폴더를 추가한다.
         * dir은 root 경로 기준 상대경로임.
         */
//        editor.addDir(dir, null, -1);
        /**
         * 위에서 생성한 폴더에 파일을 넣는다.
         */
        editor.addFile(filePath, null, -1);

        editor.applyTextDelta(filePath, null);
        SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
        String checksum = deltaGenerator.sendDelta(filePath, new ByteArrayInputStream(data), editor, true);

        editor.closeFile(filePath, checksum);
        /**
         * close current dir
         */
        editor.closeDir();
        return editor.closeEdit();
    }

    public static SVNCommitInfo addDir(ISVNEditor editor, String dir, String filePath, byte[] data) throws SVNException {

        /**
         * 항상 첫번째를 연다. 현재 root dir을 연다. 즉 다른 엔트리가 열리거나 추가전까진 해당 dir에서 작업을 한다는걸 의미한다.
         * -1 is head
         */
        editor.openRoot(-1);
        /**
         * 현재 repository에 폴더를 추가한다.
         * dir은 root 경로 기준 상대경로임.
         */
        editor.addDir(dir, null, -1);
        /**
         * 위에서 생성한 폴더에 파일을 넣는다.
         */
        editor.addFile(filePath, null, -1);

        editor.applyTextDelta(filePath, null);
        SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
        String checksum = deltaGenerator.sendDelta(filePath, new ByteArrayInputStream(data), editor, true);

        editor.closeFile(filePath, checksum);
        /**
         * close current dir
         */
        editor.closeDir();
        /**
         * close root
         */
        editor.closeDir();
        return editor.closeEdit();
    }
}
