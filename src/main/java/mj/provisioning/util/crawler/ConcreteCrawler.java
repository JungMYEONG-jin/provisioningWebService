package mj.provisioning.util.crawler;


import mj.provisioning.util.apple.AppleApi;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConcreteCrawler {

    /**
     * 1 and, 2 ios
     * @param pacakgeName
     * @param osType
     * @return
     */
    public List<JSONObject> getReviewList(String pacakgeName, String osType){
        Crawler crawler = new AppleApi();
        return crawler.getReview(pacakgeName);
    }

}
