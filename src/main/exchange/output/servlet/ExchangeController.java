package main.exchange.output.servlet;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.exchange.data.models.ExchangeResponse;
import main.exchange.data.repositories.CurrencyRepository;
import main.exchange.data.repositories.ExchangeRateRepository;
import main.exchange.input.exceptions.InvalidDataFormatException;
import main.exchange.input.service.ExchangeService;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet({"/exchange", "/exchange/*"})
public class ExchangeController extends HttpServlet {
    private final CurrencyRepository currencyRepository = new CurrencyRepository();
    private final ExchangeRateRepository exchangeRateRepository = new ExchangeRateRepository(currencyRepository);
    private final ExchangeService exchangeRateService = new ExchangeService(currencyRepository, exchangeRateRepository);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{

        String base = req.getParameter("from");
        String target = req.getParameter("to");
        BigDecimal amount;

        try {
            amount = new BigDecimal(req.getParameter("amount"));
        } catch (NumberFormatException e) {
            throw new InvalidDataFormatException("Some problems with amount: ");
        }

        ExchangeResponse exchangeResponse = exchangeRateService.exchange(base, target, amount);
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getWriter(), exchangeResponse);
    }
}
