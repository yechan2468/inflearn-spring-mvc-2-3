package yechan.inflearn_spring_mvc_2_3.web.validation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class ItemSaveDto {

    @NotBlank
    private String itemName;

    @NotNull
    @Range(min = 1000, max = 1_000_000)
    private Integer price;

    @NotNull
    @Max(value = 9999)
    private Integer quantity;

    public ItemSaveDto(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
