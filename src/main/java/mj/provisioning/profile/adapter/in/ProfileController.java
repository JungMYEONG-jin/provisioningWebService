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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tmatesoft.svn.core.SVNException;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@Slf4j
public class ProfileController {

    private final ProfileUseCase profileUseCase;
    private final FileUploadUtils fileUploadUtils;
    private final SvnRepository svnRepository;

    /**
     * 전체 프로비저닝 리스트
     * @param condition
     * @return
     */
    @GetMapping("/apple/profile/list")
    public ResponseEntity<ProfileShowListDto> getList(ProfileSearchCondition condition){
        List<ProfileShowDto> profileShowDtos = profileUseCase.searchByCondition(condition);
        ProfileShowListDto build = ProfileShowListDto.builder().data(profileShowDtos).build();
        return ResponseEntity.ok(build);
    }

    /**
     * 특정 프로비저닝 상세 정보
     * @param profileId
     * @return
     */
    @GetMapping("/resources/profiles/edit/{profileId}")
    public ResponseEntity<ProfileEditShowDto> getEdit(@PathVariable(name = "profileId") String profileId){
        log.info("profileId : {}", profileId);
        ProfileEditShowDto editShow = profileUseCase.getEditShow(profileId);
        return ResponseEntity.ok(editShow);
    }

    /**
     * 삭제 요청
     * @param profileId
     * @return
     */
    @DeleteMapping("/resources/profiles/{profileId}")
    public ResponseEntity<CommonResponse> deleteProfile(@PathVariable(name = "profileId") String profileId) {
        log.info("profileId : {}", profileId);
        profileUseCase.deleteProfile(profileId);
        CommonResponse commonResponse = new CommonResponse(HttpStatus.OK.value(), "삭제에 성공하였습니다.");
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(commonResponse);
    }

    /**
     * 다운로드
     * @param profileId
     * @return
     */
    @GetMapping("/resources/profiles/{profileId}")
    public ResponseEntity<ByteArrayResource> downloadProfile(@PathVariable(name = "profileId") String profileId) {
        log.info("profileId : {}", profileId);
        Profile profile = profileUseCase.getProfile(profileId);
        String fileName = profile.getName() + ".mobileprovision";
        byte[] contents = DatatypeConverter.parseBase64Binary(profile.getProfileContent());
        ByteArrayResource resource = new ByteArrayResource(contents);
        try {
            return ResponseEntity
                    .ok()
                    .contentLength(contents.length)
                    .header("Content-type", "application/octet-stream")
                    .header("Content-disposition", "attachment; filename=\"" + URLEncoder.encode(fileName, "utf-8") + "\"")
                    .body(resource);
        } catch (UnsupportedEncodingException e) {
            throw new CustomException(ErrorCode.DOWNLOAD_FAILED.getMessage(), ErrorCode.DOWNLOAD_FAILED);
        }
    }

    @GetMapping("/")
    public ResponseEntity<String> hello(){
        return ResponseEntity.ok("hello");
    }

    /**
     * 프로비저닝 등록
     * @param profileId
     * @param profileEditRequestDto
     * @return
     * @throws SVNException
     */
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
