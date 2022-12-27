package mj.provisioning.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.io.diff.SVNDeltaGenerator;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
@Component
public class FileUploadUtils {

    @Value("${svn.user}")
    private static String user;
    @Value("${svn.password}")
    private static String pw;

    public void saveProvisioning(String toUploadDir, String fileName, InputStream inputStream){
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

    public void writeProvisioning(String profileContent, String name, String toUploadDir){
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

    public void cleanDir(String dir){
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

    /**
     * checkout example
     * https 연결 초기화를 한다.
     * SVNRepository을 연다
     * User 인증 정보는 ISVNAuthenticationManager 통해 제공 된다.
     * repository에 인증 정보를 세팅한다.
     * 해당 url에 지정된 node type 파악하고 directory가 아니라면 종료시킨다.
     * 가장 최신 헤드 버전을 가져온다.
     * commit 전까지 SVNRepository의 메소드를 invoke 하면 안된다.
     * commit 메세지는 커밋 로그에 남는다.
     * ISVNWorkspaceMediator 는 임시 파일을 저장하기 위해 사용된다. 만약 null 이 넘겨지면 기본 임시 폴더가 사용될것이다.
     * 작업중인 svn url에 폴더나 파일을 add하자.
     * SVNCommitInfo는 기본 커밋 정보를 담는다.
     * Revision number, 작성자, 커밋 날짜, 커밋 메세지등
     * 프로비저닝 디코드
     * @param savePath
     * @param name
     * @param data
     * @throws SVNException
     */
    @Async
    public void uploadToSVN(String savePath, String name, String data) throws SVNException {
        DAVRepositoryFactory.setup();
        SVNURL url = SVNURL.parseURIEncoded("https://10.25.219.102/svn/FILESHARE_REPOSITORY/"+savePath);
        SVNRepository svnRepository = SVNRepositoryFactory.create(url);
        ISVNAuthenticationManager authenticationManager = SVNWCUtil.createDefaultAuthenticationManager(user, pw);
        svnRepository.setAuthenticationManager(authenticationManager);

        SVNNodeKind nodeKind = svnRepository.checkPath("", -1);
        if (nodeKind == SVNNodeKind.NONE) {
            SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.UNKNOWN, "No entry at URL {0}", url);
            throw new SVNException(err);
        } else if (nodeKind == SVNNodeKind.FILE) {
            SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.UNKNOWN, "Entry at URL {0} is a file while directory ws expected", url);
            throw new SVNException(err);
        }
        // get latest head version
        long latestRevision = svnRepository.getLatestRevision();
        log.info("latestRevision = " + latestRevision);
        ISVNEditor editor = svnRepository.getCommitEditor("provisioning test", null);
        byte[] contents = DatatypeConverter.parseBase64Binary(data);
        SVNCommitInfo commitInfo = addFile(editor,name+".mobileprovision", contents);
        log.info("commitInfo = {}", commitInfo);
    }


    private SVNCommitInfo addFile(ISVNEditor editor, String filePath, byte[] data) throws SVNException {

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

    private SVNCommitInfo addDir(ISVNEditor editor, String dir, String filePath, byte[] data) throws SVNException {

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
