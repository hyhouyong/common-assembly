package com.kcwl.common.monitor.health;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author ckwl
 */
@RestController
public class HealthController {
    private boolean serverStatus = true;

    @GetMapping(value = "/api/healthchk")
    public void healthCheck(HttpServletRequest request, HttpServletResponse response) {
        try {
            setResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping(value = "/stopService")
    public void stopService(HttpServletRequest request, HttpServletResponse response) {
        serverStatus = false;
    }

    @GetMapping(value = "/startService")
    public void startService(HttpServletRequest request, HttpServletResponse response) {
        serverStatus = true;
    }

    private void setResponse(HttpServletResponse response)
            throws IOException {
        HttpStatus httpStatus  = null;
        if ( serverStatus ) {
            httpStatus = HttpStatus.OK;
        } else {
            httpStatus = HttpStatus.NOT_FOUND;
        }
        response.setStatus(httpStatus.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().print(httpStatus.getReasonPhrase());
    }

}
