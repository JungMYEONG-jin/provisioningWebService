package mj.provisioning.profile.adapter.in;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mj.provisioning.profile.application.port.in.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
public class ProfileController {

    private final ProfileUseCase profileUseCase;

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

    @PostMapping("/resources/profiles/edit/{profileId}")
    public ResponseEntity postEdit(@PathVariable(name = "profileId") String profileId, @RequestBody ProfileEditRequestDto profileEditRequestDto){
//        profileEditRequestDto.setProfileId(profileId);
        profileUseCase.editProvisioning(profileEditRequestDto);
        return ResponseEntity.ok("수정에 성공하였습니다.");
    }

}
