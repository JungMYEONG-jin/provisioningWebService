package mj.provisioning.profilecertificate.adapter.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProfileCertificateControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void getCertificateList() throws Exception {
        String profileId = "2BV6CUSYMK";
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/test/profile/{id}/certificate", profileId))
                .andReturn();
        System.out.println("mvcResult = " + mvcResult.getResponse().toString());
        System.out.println("mvcResult = " + mvcResult.getResponse().getContentAsString());

    }
}