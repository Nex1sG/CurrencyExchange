package main.currencyexchange.servlet;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.currencyexchange.models.ExchangeRate;
import main.currencyexchange.repositories.ExchangeRateRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet("/exchangeRates/*")
public class ExchangeRatesController extends HttpServlet {
    private final ExchangeRateRepository exchangeRateRepository = new ExchangeRateRepository();
    private final ObjectMapper objectMapper = new ObjectMapper();

    //Get /exchangeRates
    //Get /exchangeRates/USDEUR
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String pathInfo = req.getPathInfo();
            resp.setContentType("application/json");
            if (pathInfo == null || pathInfo.equals("/")) {
                Optional<List<ExchangeRate>> rates = exchangeRateRepository.findAll();
                objectMapper.writeValue(resp.getWriter(), rates.get());
            } else {

                String pair = pathInfo.substring(1);
                String baseCurrency = pair.substring(0, 3);
                String targetCurrency = pair.substring(3);

                Optional<ExchangeRate> rate = exchangeRateRepository.findByCurrencies(baseCurrency, targetCurrency);
                objectMapper.writeValue(resp.getWriter(), rate.get());
            }

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            ExchangeRate exchangeRate = objectMapper.readValue(req.getInputStream(), ExchangeRate.class);
            exchangeRateRepository.save(exchangeRate);

            resp.setContentType("application/json");

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
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
        try{
            ExchangeRate exchangeRate = objectMapper.readValue(req.getInputStream(), ExchangeRate.class);
            exchangeRateRepository.update(exchangeRate);

//            resp.setContentType("application/json");
//            objectMapper.writeValue(resp.getWriter(), updated);


        } catch (Exception e){
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
