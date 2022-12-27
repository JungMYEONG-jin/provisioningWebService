package mj.provisioning.profile.adapter.in;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mj.provisioning.profile.application.port.in.*;
import mj.provisioning.profile.domain.Profile;
import mj.provisioning.util.FileUploadUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tmatesoft.svn.core.SVNException;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
public class ProfileController {

    private final ProfileUseCase profileUseCase;
    private final FileUploadUtils fileUploadUtils;

    @GetMapping("/apple/profile/list")
    public ResponseEntity<ProfileShowListDto> getList(ProfileSearchCondition condition){
        List<ProfileShowDto> profileShowDtos = profileUseCase.searchByCondition(condition);
        ProfileShowListDto build = ProfileShowListDto.builder().data(profileShowDtos).build();
        return ResponseEntity.ok(build);
    }

    @GetMapping("/resources/profiles/edit/{profileId}")
    public ResponseEntity<ProfileEditShowDto> getEdit(@PathVariable(name = "profileId") String profileId){
        log.info("profileId : {}", profileId);
        ProfileEditShowDto editShow = profileUseCase.getEditShow(profileId);
        return ResponseEntity.ok(editShow);
    }

    @GetMapping("/")
    public ResponseEntity<String> hello(){
        return ResponseEntity.ok("hello");
    }

    @PostMapping(value = "/resources/profiles/edit/{profileId}", produces = "application/text; charset=UTF-8")
    public ResponseEntity postEdit(@PathVariable(name = "profileId") String profileId, @RequestBody ProfileEditRequestDto profileEditRequestDto) throws SVNException {
        Profile profile = profileUseCase.editProvisioning(profileEditRequestDto);
        try {
            fileUploadUtils.uploadToSVN("svntest", profile.getName(), profile.getProfileContent());
        } catch (SVNException e) {
            throw new RuntimeException("Fail to upload svn");
        }
        return ResponseEntity.ok("수정에 성공하였습니다.");
    }

}
