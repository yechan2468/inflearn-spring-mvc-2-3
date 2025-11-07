package yechan.inflearn_spring_mvc_2_3.validation;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;

public class MessageCodesResolverTest {

    MessageCodesResolver messageCodesResolver = new DefaultMessageCodesResolver();

    @Test
    void messageCodesResolverObject() {
        String[] resolvedMessageCodes = messageCodesResolver.resolveMessageCodes("required", "item");

        Assertions.assertThat(resolvedMessageCodes).containsExactly("required.item", "required");
    }

    @Test
    void messageCodesResolverField() {
        String[] resolvedMessageCodes = messageCodesResolver.resolveMessageCodes(
                "required", "item", "itemName", String.class
        );

        Assertions.assertThat(resolvedMessageCodes).containsExactly(
                "required.item.itemName", "required.itemName", "required.java.lang.String", "required"
        );
    }
}
