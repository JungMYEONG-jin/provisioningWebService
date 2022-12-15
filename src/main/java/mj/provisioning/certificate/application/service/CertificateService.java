package mj.provisioning.certificate.application.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mj.provisioning.certificate.application.port.in.CertificateShowDto;
import mj.provisioning.certificate.application.port.in.CertificateShowListDto;
import mj.provisioning.certificate.application.port.in.CertificateUseCase;
import mj.provisioning.certificate.application.port.out.CertificateRepositoryPort;
import mj.provisioning.certificate.domain.Certificate;
import mj.provisioning.certificate.domain.CertificateType;
import mj.provisioning.util.apple.AppleApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CertificateService implements CertificateUseCase {

    private final CertificateRepositoryPort certificateRepositoryPort;
    private final AppleApi appleApi;

    @Override
    public void saveCertificates() {
        // 초기화
        certificateRepositoryPort.deleteAll();
        // 파싱
        String allCertificates = appleApi.getAllCertificates();
        JsonParser jsonParser = new JsonParser();
        JsonObject asJsonObject = jsonParser.parse(allCertificates).getAsJsonObject();
        JsonArray data = asJsonObject.getAsJsonArray("data");
        List<Certificate> certificateList = new ArrayList<>();
        for (JsonElement datum : data) {
            JsonObject object = datum.getAsJsonObject();
            String type = object.get("type").toString().replaceAll("\"", "");
            String certificateId = object.get("id").toString().replaceAll("\"", "");
            JsonObject attributes = object.getAsJsonObject("attributes");
            String serialNumber = attributes.get("serialNumber").toString().replaceAll("\"", "");
            String displayName = attributes.get("displayName").toString().replaceAll("\"", "");
            String name = attributes.get("name").toString().replaceAll("\"", "");
            String expirationDate = attributes.get("expirationDate").toString().replaceAll("\"", "").replaceAll("[^0-9]", "");
            expirationDate = expirationDate.substring(0, 4) + "/" + expirationDate.substring(4, 6) + "/" + expirationDate.substring(6, 8);
            String certificateType = attributes.get("certificateType").toString().replaceAll("\"", "");

            Certificate certificate = Certificate.builder().certificateId(certificateId)
                    .certificateType(CertificateType.get(certificateType))
                    .displayName(displayName)
                    .expirationDate(expirationDate)
                    .serialNumber(serialNumber)
                    .type(type)
                    .name(name)
                    .build();
            certificateList.add(certificate);
        }
        // 저장
        certificateRepositoryPort.saveAll(certificateList);
    }

    @Override
    public void saveCertificate(Certificate certificate) {
        certificateRepositoryPort.save(certificate);
    }

    @Override
    public void updateCertificates(String certificateId) {

    }

    @Override
    public void deleteCertificate(String certificateId) {
        certificateRepositoryPort.deleteByCertificateId(certificateId);
    }

    @Override
    public CertificateShowListDto findAll() {
        List<Certificate> all = certificateRepositoryPort.findAll();
        List<CertificateShowDto> collect = all.stream().map(CertificateShowDto::of).collect(Collectors.toList());
        return CertificateShowListDto.builder().data(collect).build();
    }
}
