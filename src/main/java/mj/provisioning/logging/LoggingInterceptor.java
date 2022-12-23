package mj.provisioning.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

/**
 * interceptor 등록해서 요청 처리
 */
@Component
@Slf4j
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (Objects.equals(request.getMethod(), "POST")){
            CharsetDecoder charsetDecoder = StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.IGNORE);
            InputStreamReader reader = new InputStreamReader(request.getInputStream(), charsetDecoder);
            Map<String, Object> map = new ObjectMapper().readValue(reader, Map.class);
            log.info("RequestBody : {}", map);
            log.info("RequestURI : {}", request.getRequestURI());
            return true;
        }else{
            log.info("Request : {}", request.getQueryString());
            log.info("RequestURI : {}", request.getRequestURI());
            return true;
        }
    }

}
