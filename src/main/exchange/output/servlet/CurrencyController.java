package main.exchange.output.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.exchange.input.exceptions.InvalidDataFormatException;
import main.exchange.data.models.Currency;
import main.exchange.input.service.CurrencyService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet("/currencies/*")
public class CurrencyController extends HttpServlet {

    private final CurrencyService currencyService = new CurrencyService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Optional<Currency> getParamsAndCreateEntity(HttpServletRequest req){
        String code = req.getParameter("code");
        String name = req.getParameter("name");
        String sign = req.getParameter("sign");

        if(code == null || name == null || sign == null){
            throw new InvalidDataFormatException("Please, check entered fields");
        }

        if(code.isEmpty() || name.isEmpty() || sign.isEmpty()){
            throw new InvalidDataFormatException("Please, check entered fields");
        }

        if(code.length() != 3){
            throw new InvalidDataFormatException("Code length must be 3");
        }

        Currency currency = new Currency();
        currency.setCode(code);
        currency.setName(name);
        currency.setSign(sign);

        return Optional.of(currency);
    }

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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        Optional<Currency> currency = getParamsAndCreateEntity(req);
        if(currency.isEmpty()) return;
        currencyService.create(currency.get());

        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_CREATED);
        objectMapper.writeValue(resp.getWriter(), currency.get());
    }


    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws  IOException{
        Optional<Currency> currency= getParamsAndCreateEntity(req);
        if(currency.isEmpty()) return;
        currencyService.update(currency.get());

        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_RESET_CONTENT);
        objectMapper.writeValue(resp.getWriter(), currency.get());
        }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp){
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            throw new InvalidDataFormatException("Currency id required");
        }

        long id = Long.parseLong(pathInfo.substring(1));
        currencyService.delete(id);

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