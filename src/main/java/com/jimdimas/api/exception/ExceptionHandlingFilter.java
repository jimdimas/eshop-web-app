package com.jimdimas.api.exception;

import com.jimdimas.api.util.JsonResponse;
import com.jimdimas.api.util.UtilService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class ExceptionHandlingFilter extends OncePerRequestFilter {

    /*This filter is needed because filters get executed before controllers,servlets etc.
    * This means that exceptions thrown in a filter are not caught and processed by the
    * ResponseExceptionHandler,that's why we need a filter that handles exceptions.
    * You also need to add this filter before the JWTAuthFilter in SecurityConfig.
    */

    private final UtilService utilService;
    @Override
    public void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (RuntimeException e) {
            Logger logger = Logger.getAnonymousLogger();
            logger.log(Level.SEVERE,"An exception was thrown.",e);
            JsonResponse errorResponse = new JsonResponse();
            errorResponse.setMessage("Something went wrong,please try again.");  //more logic to be added here
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.getWriter().write(utilService.objectToJson(errorResponse));
        }
    }
}
