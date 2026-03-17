package main.currencyexchange.utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.Filter;
import main.currencyexchange.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebFilter("/*")
public class ExceptionHandlerFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerFilter.class);

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        if(response.isCommitted()) return;
        response.resetBuffer();
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, jakarta.servlet.ServletException {

        HttpServletResponse response = (HttpServletResponse) res;

        try {

            chain.doFilter(req, res);

        } catch (CurrencyNotFoundException | ExchangeRateNotFoundException e) {
            logger.error("Currency or exchange rate not found", e);
            writeError(response, 404, e.getMessage());

        } catch (CurrencyAlreadyExistsException | ExchangeRateAlreadyExistsException e) {
            logger.error("The chosen entity already exists: ", e);
            writeError(response, 409, e.getMessage());

        } catch (DatabaseException e) {
            logger.error("Processing with Database has failed with error: ", e);
            writeError(response, 500, e.getMessage());

        } catch (InvalidDataFormatException e) {
            logger.error("Incorrect field arguments ", e);
            writeError(response, 400, e.getMessage());

        } catch (Exception e) {
            logger.error("Some problem with server ", e);
            writeError(response, 500, "Internal server error");
        }
    }
}