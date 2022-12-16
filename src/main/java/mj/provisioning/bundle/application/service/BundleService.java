package mj.provisioning.bundle.application.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import mj.provisioning.bundle.application.port.in.BundleUseCase;
import mj.provisioning.bundle.application.port.out.BundleRepositoryPort;
import mj.provisioning.bundle.domain.Bundle;
import mj.provisioning.util.apple.AppleApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BundleService implements BundleUseCase {

    private final BundleRepositoryPort bundleRepositoryPort;
    private final AppleApi appleApi;

    @Override
    public void saveBundles() {
        // 초기화
        bundleRepositoryPort.deleteAll();

        String response = appleApi.getAllBundleId();
        JsonParser parser = new JsonParser();
        JsonObject parse = parser.parse(response).getAsJsonObject();
        JsonArray data = parse.get("data").getAsJsonArray();
        List<Bundle> bundles = new ArrayList<>();
        for (JsonElement datum : data) {
            JsonObject object = datum.getAsJsonObject();
            String type = object.get("type").toString().replaceAll("\"", "");
            String bundleId = object.get("id").toString().replaceAll("\"", "");
            JsonObject attributes = object.getAsJsonObject("attributes");
            String name = attributes.get("name").toString().replaceAll("\"", "");
            String identifier = attributes.get("identifier").toString().replaceAll("\"", "");
            String seedId = attributes.get("seedId").toString().replaceAll("\"", "");

            Bundle bundle = Bundle.builder().bundleId(bundleId)
                    .type(type)
                    .name(name)
                    .identifier(identifier)
                    .seedId(seedId).build();
            bundles.add(bundle);
        }

        bundleRepositoryPort.saveAll(bundles);
    }
}
