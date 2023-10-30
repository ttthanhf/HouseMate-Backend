package housemate.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentMethod {
    VNPAY("vnpay"),
    MOMO("momo");

    private final String value;
}