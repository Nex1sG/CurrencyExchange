package main.currencyexchange.output.servlet;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import main.currencyexchange.data.models.ExchangeResponse;
import main.currencyexchange.input.exceptions.InvalidDataFormatException;
import main.currencyexchange.data.models.ExchangeRate;
import main.currencyexchange.input.service.CurrencyService;
import main.currencyexchange.input.service.ExchangeRateService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;


@WebServlet("/exchangeRates/*")
public class ExchangeRatesController extends HttpServlet {

    private final CurrencyService currencyService = new CurrencyService();
    private final ExchangeRateService exchangeRateService = new ExchangeRateService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ExchangeRate mapperToExchangeRate(HttpServletRequest req) {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");

        if (baseCurrencyCode == null || targetCurrencyCode == null) {
            throw new InvalidDataFormatException("Please make sure that fields are correct");
        }
        if (baseCurrencyCode.isEmpty() || targetCurrencyCode.isEmpty()) {
            throw new InvalidDataFormatException("Currency code cannot be null");
        }

        BigDecimal rate;
        try {
            rate = new BigDecimal(req.getParameter("rate"));
        } catch (Exception e) {
            throw new InvalidDataFormatException("Invalid rate");
        }

        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setBaseCurrency(currencyService.readReq(baseCurrencyCode));
        exchangeRate.setTargetCurrency(currencyService.readReq(targetCurrencyCode));
        exchangeRate.setRate(rate);


        return exchangeRate;
    }

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
        String targetCurrency = pair.substring(3, 6);

        ExchangeRate exchangeRate = exchangeRateService.readExchangeRate(baseCurrency, targetCurrency);

        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getWriter(), exchangeRate);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BigDecimal rate;
        try {
            rate = new BigDecimal(req.getParameter("rate"));
        } catch (Exception e) {
            throw new InvalidDataFormatException("Invalid rate");
        }
        ExchangeRate exchangeRate = mapperToExchangeRate(req);
        exchangeRate.setRate(rate);
        exchangeRateService.create(exchangeRate);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getWriter(), exchangeRate);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.length() != 7) {
            throw new InvalidDataFormatException("Invalid currency pair: " + pathInfo);
        }

        String pair = pathInfo.substring(1);

        String baseCode = pair.substring(0, 3);
        String targetCode = pair.substring(3);

        String body = req.getReader().lines().reduce("", (acc, line) -> acc + line);

        if (body == null || !body.startsWith("rate=")) {
            throw new InvalidDataFormatException("Rate is required");
        }

        String rateStr = body.split("=")[1];

        BigDecimal rate;
        try {
            rate = new BigDecimal(rateStr);
        } catch (Exception e) {
            throw new InvalidDataFormatException("Invalid rate");
        }

        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setBaseCurrency(currencyService.readReq(baseCode));
        exchangeRate.setTargetCurrency(currencyService.readReq(targetCode));
        exchangeRate.setRate(rate);

        exchangeRateService.update(exchangeRate);

        ExchangeRate updated = exchangeRateService.readExchangeRate(baseCode, targetCode);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getWriter(), updated);
    }
}