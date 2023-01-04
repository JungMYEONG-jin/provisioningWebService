package mj.provisioning.profile.adapter.in;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mj.provisioning.common.CommonResponse;
import mj.provisioning.common.exception.CustomException;
import mj.provisioning.common.exception.ErrorCode;
import mj.provisioning.profile.application.port.in.*;
import mj.provisioning.profile.domain.Profile;
import mj.provisioning.svn.domain.SvnRepoInfo;
import mj.provisioning.svn.dto.ProvisioningRepositoryDto;
import mj.provisioning.svn.repository.SvnRepository;
import mj.provisioning.util.FileUploadUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tmatesoft.svn.core.SVNException;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@Slf4j
public class ProfileController {

    private final ProfileUseCase profileUseCase;
    private final FileUploadUtils fileUploadUtils;
    private final SvnRepository svnRepository;

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

    @DeleteMapping("/resources/profiles/{profileId}")
    public ResponseEntity<CommonResponse> deleteProfile(@PathVariable(name = "profileId") String profileId) {
        log.info("profileId : {}", profileId);
        profileUseCase.deleteProfile(profileId);
        CommonResponse commonResponse = new CommonResponse(HttpStatus.OK.value(), "삭제에 성공하였습니다.");
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(commonResponse);
    }


    @GetMapping("/")
    public ResponseEntity<String> hello(){
        return ResponseEntity.ok("hello");
    }

    @PostMapping(value = "/resources/profiles/edit/{profileId}", produces = "application/text; charset=UTF-8")
    public ResponseEntity postEdit(@PathVariable(name = "profileId") String profileId, @RequestBody ProfileEditRequestDto profileEditRequestDto) throws SVNException {
        Profile profile = profileUseCase.editProvisioning(profileEditRequestDto);
        try {
            List<ProvisioningRepositoryDto> collect = profileEditRequestDto.getSvnRepos().stream().filter(ProvisioningRepositoryDto::isChosen).collect(Collectors.toList());
            if (collect.size()>1) // 1개 여야만 됨..
                throw new CustomException(ErrorCode.PATH_NOT_MULTI.getMessage(), ErrorCode.PATH_NOT_MULTI);
            SvnRepoInfo svnRepoInfo = svnRepository.findByProvisioningName(collect.get(0).getAppName());
            fileUploadUtils.uploadToSVN(svnRepoInfo.getUri(), profile.getName(), profile.getProfileContent());
        } catch (SVNException e) {
            throw new CustomException(ErrorCode.UPLOAD_FAILED.getMessage(), ErrorCode.UPLOAD_FAILED);
        }
        return ResponseEntity.ok("수정에 성공하였습니다.");
    }

}
