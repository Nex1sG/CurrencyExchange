package main.currencyexchange.servlet;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.currencyexchange.models.ExchangeResponse;

import java.io.IOException;
import java.rmi.RemoteException;

@WebServlet({"/exchange", "/exchange/*"})
public class ExchangeController extends HttpServlet {
    private final ExchangeService exchangeRateService = new ExchangeService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        try{
            String base = req.getParameter("from");
            String target = req.getParameter("to");
            int amount = Integer.parseInt(req.getParameter("amount"));


            ExchangeResponse exchangeResponse = exchangeRateService.exchange(base, target, amount);
            objectMapper.writeValue(resp.getWriter(), exchangeResponse);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
