package main.currencyexchange.servlet;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.currencyexchange.models.ExchangeResponse;
import main.currencyexchange.repositories.CurrencyRepository;
import main.currencyexchange.repositories.ExchangeRateRepository;
import main.currencyexchange.service.ExchangeService;

import java.io.IOException;

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
        int amount = Integer.parseInt(req.getParameter("amount"));


        ExchangeResponse exchangeResponse = exchangeRateService.exchange(base, target, amount);
        objectMapper.writeValue(resp.getWriter(), exchangeResponse);
    }
}
