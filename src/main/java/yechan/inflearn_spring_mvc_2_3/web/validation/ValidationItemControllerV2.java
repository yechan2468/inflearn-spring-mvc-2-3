package yechan.inflearn_spring_mvc_2_3.web.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import yechan.inflearn_spring_mvc_2_3.domain.item.Item;
import yechan.inflearn_spring_mvc_2_3.domain.item.ItemRepository;

import java.util.List;

@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;
    private final ItemValidator itemValidator;

    @InitBinder
    public void init(WebDataBinder dataBinder) {
        dataBinder.addValidators(itemValidator);
    }

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";
    }

//    @PostMapping("/add")
    public String addItemV1(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수입니다"));
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1_000_000) {
            bindingResult.addError(new FieldError("item", "price", "가격은 1,000 ~ 1,000,000원까지 입력 가능합니다"));
        }
        if (item.getQuantity() == null || item.getQuantity() < 0 || item.getQuantity() > 9999) {
            bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9,999개까지 입력 가능합니다"));
        }
        if (item.getPrice() != null && item.getQuantity() != null && item.getPrice() * item.getQuantity() < 10_000) {
            bindingResult.addError(new ObjectError("item", "가격과 수량의 곱은 10,000원 이상이어야 합니다. 현재 값 = " + item.getPrice() * item.getQuantity()));
        }

        if (bindingResult.hasErrors()) {
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    private <T> void addItemFieldErrorV2(String fieldName, T fieldValue, String errorMessage, BindingResult bindingResult) {
        bindingResult.addError(new FieldError("item", fieldName, fieldValue, false, null, null, errorMessage));
    }

//    @PostMapping("/add")
    public String addItemV2(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (!StringUtils.hasText(item.getItemName())) {
            addItemFieldErrorV2("itemName", item.getItemName(), "상품 이름은 필수입니다", bindingResult);
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1_000_000) {
            addItemFieldErrorV2("price", item.getPrice(), "가격은 1,000 ~ 1,000,000원까지 입력 가능합니다", bindingResult);
        }
        if (item.getQuantity() == null || item.getQuantity() < 0 || item.getQuantity() > 9999) {
            addItemFieldErrorV2("quantity", item.getQuantity(), "수량은 최대 9,999개까지 입력 가능합니다", bindingResult);
        }
        if (item.getPrice() != null && item.getQuantity() != null && item.getPrice() * item.getQuantity() < 10_000) {
            bindingResult.addError(new ObjectError("item", "가격과 수량의 곱은 10,000원 이상이어야 합니다. 현재 값 = " + item.getPrice() * item.getQuantity()));
        }

        if (bindingResult.hasErrors()) {
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    private <T> void addItemFieldErrorV3(String fieldName, T fieldValue,
                                         String errorCode, Object[] args,
                                         BindingResult bindingResult) {
        bindingResult.addError(new FieldError(
                "item", fieldName, fieldValue, false, new String[]{errorCode}, args, null
        ));
    }

//    @PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (!StringUtils.hasText(item.getItemName())) {
            addItemFieldErrorV3("itemName", item.getItemName(), "required.item.itemName", null, bindingResult);
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1_000_000) {
            addItemFieldErrorV3("price", item.getPrice(), "range.item.price", new Object[]{1_000, 1_000_000}, bindingResult);
        }
        if (item.getQuantity() == null || item.getQuantity() < 0 || item.getQuantity() > 9999) {
            addItemFieldErrorV3("quantity", item.getQuantity(), "max.item.quantity", new Object[]{9999}, bindingResult);
        }
        if (item.getPrice() != null && item.getQuantity() != null && item.getPrice() * item.getQuantity() < 10_000) {
            bindingResult.addError(
                    new ObjectError("item", new String[]{"totalPriceMin"}, new Object[]{10_000, item.getPrice() * item.getQuantity()}, null)
            );
        }

        if (bindingResult.hasErrors()) {
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

//    @PostMapping("/add")
    public String addItemV4(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "itemName", "required");
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1_000_000) {
            bindingResult.rejectValue("price", "range", new Object[]{1_000, 1_000_000}, null);
        }
        if (item.getQuantity() == null || item.getQuantity() < 0 || item.getQuantity() > 9999) {
            bindingResult.rejectValue("quantity", "max", new Object[]{9999}, null);
        }
        if (item.getPrice() != null && item.getQuantity() != null && item.getPrice() * item.getQuantity() < 10_000) {
            bindingResult.reject("totalPriceMin", new Object[]{10_000, item.getPrice() * item.getQuantity()}, null);
        }

        if (bindingResult.hasErrors()) {
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

//    @PostMapping("/add")
    public String addItemV5(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        itemValidator.validate(item, bindingResult);
        if (bindingResult.hasErrors()) {
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @PostMapping("/add")
    public String addItemV6(@Validated @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

}

