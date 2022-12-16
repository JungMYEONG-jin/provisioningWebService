package mj.provisioning.logging;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.io.StringReader;
import java.util.Scanner;

public class RequestServletWrapper extends HttpServletRequestWrapper {
    private String requestData = null;
    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request The request to wrap
     * @throws IllegalArgumentException if the request is null
     */
    public RequestServletWrapper(HttpServletRequest request) {
        super(request);

        try(Scanner s = new Scanner(request.getInputStream()).useDelimiter("\\A")){
            requestData = s.hasNext() ? s.next() : "";
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {

        StringReader stringReader = new StringReader(requestData);

        return new ServletInputStream() {
            private ReadListener readListener = null;

            @Override
            public boolean isFinished() {
                try{
                    return stringReader.read()<0;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public boolean isReady() {
                return isFinished();
            }

            @Override
            public void setReadListener(ReadListener listener) {
                this.readListener = listener;

                try{
                    if (!isFinished()){
                        readListener.onDataAvailable();
                    }else {
                        readListener.onAllDataRead();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public int read() throws IOException {
                return stringReader.read();
            }
        };
    }
}
