package main.exchange.output.servlet;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import main.exchange.input.exceptions.InvalidDataFormatException;
import main.exchange.data.models.ExchangeRate;
import main.exchange.input.service.CurrencyService;
import main.exchange.input.service.ExchangeRateService;

import java.io.IOException;
import java.math.BigDecimal;


@WebServlet("/exchangeRates/*")
public class ExchangeRatesController extends HttpServlet {

    private final CurrencyService currencyService = new CurrencyService();
    private final ExchangeRateService exchangeRateService = new ExchangeRateService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public record CurrencyPair(String base, String target) {}

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            writeJson(resp, exchangeRateService.readExchangeRate());
            return;
        }

        CurrencyPair pair = extractPair(req);

        ExchangeRate exchangeRate = exchangeRateService.readExchangeRate(
                pair.base(), pair.target()
        );

        writeJson(resp, exchangeRate);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String base = req.getParameter("baseCurrencyCode");
        String target = req.getParameter("targetCurrencyCode");

        if (base == null || target == null || base.isBlank() || target.isBlank()) {
            throw new InvalidDataFormatException("Currency codes are required");
        }

        BigDecimal rate = extractRate(req);

        ExchangeRate exchangeRate = new ExchangeRate(
                0, currencyService.readReq(base), currencyService.readReq(target), rate
        );

        exchangeRateService.create(exchangeRate);
        writeJson(resp, exchangeRate);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        CurrencyPair pair = extractPair(req);
        BigDecimal rate = extractRate(req);

        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setBaseCurrency(currencyService.readReq(pair.base()));
        exchangeRate.setTargetCurrency(currencyService.readReq(pair.target()));
        exchangeRate.setRate(rate);

        exchangeRateService.update(exchangeRate);

        ExchangeRate updated = exchangeRateService.readExchangeRate(
                pair.base(), pair.target()
        );

        writeJson(resp, updated);
    }


    private CurrencyPair extractPair(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.length() != 7) {
            throw new InvalidDataFormatException("Invalid currency pair: " + pathInfo);
        }

        String pair = pathInfo.substring(1);

        if (!pair.matches("[A-Z]{6}")) {
            throw new InvalidDataFormatException("Invalid currency pair: " + pair);
        }

        return new CurrencyPair(pair.substring(0, 3), pair.substring(3));
    }

    private BigDecimal extractRate(HttpServletRequest req) throws IOException {
        String rateParam = req.getParameter("rate");
        if (rateParam != null) return parseRate(rateParam);
        String body = req.getReader().lines().reduce("", (a, b) -> a + b);

        if (!body.startsWith("rate=")) {
            throw new InvalidDataFormatException("Rate is required");
        }

        return parseRate(body.split("=")[1]);
    }

    private BigDecimal parseRate(String rateStr) {
        if (rateStr == null || rateStr.isBlank()) {
            throw new InvalidDataFormatException("Rate is required");
        }

        try {
            return new BigDecimal(rateStr);
        } catch (Exception e) {
            throw new InvalidDataFormatException("Invalid rate: " + rateStr);
        }
    }

    private void writeJson(HttpServletResponse resp, Object body) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getWriter(), body);
    }
}
