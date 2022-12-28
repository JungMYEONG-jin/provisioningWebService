package mj.provisioning.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.tmatesoft.svn.core.SVNException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileUploadUtilsTest {

    @Autowired
    FileUploadUtils fileUploadUtils;

    @Test
    void getDirectoryListing() {
        String uri = "buildsetting/iOS/MobileProvisioning/대고객앱/글로벌";
        try {
            List<String> currentDirectoryList = fileUploadUtils.getCurrentDirectoryList(uri);
            for (String s : currentDirectoryList) {
                System.out.println(uri+"/" + s);
            }
        } catch (SVNException e) {
            throw new RuntimeException(e);
        }

    }
}