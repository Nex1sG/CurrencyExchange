package main.currencyexchange.output.servlet;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.currencyexchange.input.exceptions.InvalidDataFormatException;
import main.currencyexchange.data.models.ExchangeRate;
import main.currencyexchange.input.service.ExchangeRateService;

import java.io.IOException;
import java.util.List;

@WebServlet("/exchangeRates/*")
public class ExchangeRatesController extends HttpServlet {
    private final ExchangeRateService exchangeRateService = new ExchangeRateService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    //Get /exchangeRates
    //Get /exchangeRates/USDEUR
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");

        if (pathInfo == null || pathInfo.equals("/")) {
            List<ExchangeRate> rates = exchangeRateService.readExchangeRate();
            objectMapper.writeValue(resp.getWriter(), rates);
            resp.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String pair = pathInfo.substring(1);
        String baseCurrency = pair.substring(0, 3);
        String targetCurrency = pair.substring(3);

        if (pair.length() != 6 || !pair.matches("[A-Z]{6}")) {
            throw new InvalidDataFormatException("Invalid currency pair: " + pair);
        }


        ExchangeRate rate = exchangeRateService.readExchangeRate(baseCurrency, targetCurrency);
        objectMapper.writeValue(resp.getWriter(), rate);
        resp.setStatus(HttpServletResponse.SC_OK);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ExchangeRate exchangeRate = objectMapper.readValue(req.getInputStream(), ExchangeRate.class);
        exchangeRateService.create(exchangeRate);
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_CREATED);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        ExchangeRate exchangeRate = objectMapper.readValue(req.getInputStream(), ExchangeRate.class);
        exchangeRateService.update(exchangeRate);

        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_CREATED);
    }
}
