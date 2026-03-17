package main.currencyexchange.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.currencyexchange.models.Currency;
import main.currencyexchange.repositories.CurrencyRepository;
import main.currencyexchange.service.CurrencyService;

import java.io.IOException;
import java.util.List;

@WebServlet("/currencies/*")

public class CurrencyController extends HttpServlet {

    private final CurrencyService currencyService = new CurrencyService();
    private final CurrencyRepository currencyRepository = new CurrencyRepository();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException{

        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");

        if (pathInfo == null || pathInfo.equals("/")) {
            List<Currency> currencies = currencyService.readReq();
            objectMapper.writeValue(resp.getWriter(), currencies);
            resp.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String code = pathInfo.substring(1);
        Currency currency = currencyService.readReq(code);
        objectMapper.writeValue(resp.getWriter(), currency);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Currency currency = objectMapper.readValue(req.getInputStream(), Currency.class);
        currencyRepository.save(currency);
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_CREATED);
    }

    // PATCH /currencies
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp)  throws IOException{
        Currency currency = objectMapper.readValue(req.getInputStream(), Currency.class);
        currencyRepository.save(currency);
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
        }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp){
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            throw new IllegalArgumentException("Currency code required");
        }

        long id = Long.parseLong(pathInfo.substring(1));
        currencyRepository.delete(id);
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }
}