package main.currencyexchange.models;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Currency {
    private Long id;
    private String code;
    private String fullName;
    private String sign;
}
