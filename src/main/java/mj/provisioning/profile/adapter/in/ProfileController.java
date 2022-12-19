package mj.provisioning.profile.adapter.in;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mj.provisioning.profile.application.port.in.*;
import mj.provisioning.profile.domain.Profile;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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

}
