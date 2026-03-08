package main.currencyexchange.model;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Currency {
    private int id;
    private String code;
    private String fullName;
    private String sign;
}
