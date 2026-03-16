package main.currencyexchange.exceptions;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/*")
public class ExceptionHandlerFilter extends HttpFilter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        try {
            chain.doFilter(req, res);

        } catch (CurrencyNotFoundException | ExchangeRateNotFoundException e) {
            //..

        } catch (CurrencyAlreadyExistsException | ExchangeRateAlreadyExistsException e) {
            //...

        } catch (DatabaseException e) {
            //...

        } catch (Exception e) {
            //...
        }
    }
}