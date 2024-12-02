package org.tfl.backend;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AppSecurityHeadersFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(AppSecurityHeadersFilter.class);

    public AppSecurityHeadersFilter() {}

    @Override
    public void destroy() {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        ResponseWrapper resp = new ResponseWrapper((HttpServletResponse) response);
        // pass the request along the filter chain
        chain.doFilter(request, resp);
    }

    @Override
    public void init(FilterConfig fConfig) {}

    private static class ResponseWrapper extends HttpServletResponseWrapper {
        public ResponseWrapper(HttpServletResponse response) {
            super(response);
            response.setHeader("Strict-Transport-Security", "max-age=31536000;includeSubDomains");
            response.setHeader("Content-Security-Policy", "default-src 'self';");
            response.setHeader("X-Frame-Options", "DENY");
            response.setHeader("X-XSS-Protection", "1; mode=block");
            response.setHeader("X-Content-Type-Options", "nosniff");
            response.setHeader("Referrer-Policy", "no-referrer");
            response.setHeader("Cache-Control", "no-store");
        }
    }
}
