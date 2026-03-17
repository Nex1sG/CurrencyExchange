package main.currencyexchange.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.Filter;
import lombok.extern.slf4j.Slf4j;
import main.currencyexchange.exceptions.*;

import java.io.IOException;

@WebFilter("/*")
@Slf4j
public class ExceptionHandlerFilter implements Filter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private void writeError(ServletRequest req, HttpServletResponse response,
                            int status, String message) throws IOException {

        if (response.isCommitted()) return;

        response.resetBuffer();
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String path = ((HttpServletRequest) req).getRequestURI();
        ErrorResponse error = new ErrorResponse(status, message, path);
        String json = objectMapper.writeValueAsString(error);

        response.getWriter().write(json);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, jakarta.servlet.ServletException {

        HttpServletResponse response = (HttpServletResponse) res;

        try {

            chain.doFilter(req, res);

        } catch (CurrencyNotFoundException | ExchangeRateNotFoundException e) {
            log.error("Currency or exchange rate not found", e);
            writeError(req, response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());

        } catch (CurrencyAlreadyExistsException | ExchangeRateAlreadyExistsException e) {
            log.error("The chosen entity already exists: ", e);
            writeError(req, response, HttpServletResponse.SC_CONFLICT, e.getMessage());

        } catch (DatabaseException e) {
            log.error("Processing with Database has failed with error: ", e);
            writeError(req, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());

        } catch (InvalidDataFormatException e) {
            log.error("Incorrect field arguments ", e);
            writeError(req, response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());

        } catch (Exception e) {
            log.error("Some problem with server ", e);
            writeError(req, response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }
}