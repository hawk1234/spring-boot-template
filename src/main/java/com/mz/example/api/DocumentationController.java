package com.mz.example.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class DocumentationController {

    @GetMapping(value = "/")
    public void redirectToDocumentation(HttpServletResponse httpResponse) throws IOException {
        httpResponse.sendRedirect("/docs/index.html");
    }
}
