package main.currencyexchange.servlet;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.currencyexchange.exceptions.ExchangeRateNotFoundException;
import main.currencyexchange.exceptions.InvalidDataFormatException;
import main.currencyexchange.models.ExchangeRate;
import main.currencyexchange.repositories.CurrencyRepository;
import main.currencyexchange.repositories.ExchangeRateRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet("/exchangeRates/*")
public class ExchangeRatesController extends HttpServlet {
    private final CurrencyRepository currencyRepository = new CurrencyRepository();
    private final ExchangeRateRepository exchangeRateRepository = new ExchangeRateRepository(currencyRepository);
    private final ObjectMapper objectMapper = new ObjectMapper();

    //Get /exchangeRates
    //Get /exchangeRates/USDEUR
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        if (pathInfo == null || pathInfo.equals("/")) {
            List<ExchangeRate> rates = exchangeRateRepository.findAll();
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


        Optional<ExchangeRate> rate = exchangeRateRepository.findByCurrencies(baseCurrency, targetCurrency);
        if(rate.isEmpty()) throw new ExchangeRateNotFoundException("Exchange rate not found");
        objectMapper.writeValue(resp.getWriter(), rate.get());
        resp.setStatus(HttpServletResponse.SC_OK);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ExchangeRate exchangeRate = objectMapper.readValue(req.getInputStream(), ExchangeRate.class);
        exchangeRateRepository.save(exchangeRate);
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
        exchangeRateRepository.update(exchangeRate);

        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_CREATED);
    }
}
