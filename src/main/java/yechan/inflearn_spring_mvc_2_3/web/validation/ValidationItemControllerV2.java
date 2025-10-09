package yechan.inflearn_spring_mvc_2_3.web.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import yechan.inflearn_spring_mvc_2_3.domain.item.Item;
import yechan.inflearn_spring_mvc_2_3.domain.item.ItemRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;

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

    @PostMapping("/add")
    public String addItem(@ModelAttribute Item item, RedirectAttributes redirectAttributes, Model model) {
        Map<String, String> errors = new HashMap<>();

        if (!StringUtils.hasText(item.getItemName())) {
            errors.put("itemName", "상품 이름은 필수입니다");
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1_000_000) {
            errors.put("price", "가격은 1,000 ~ 1,000,000원까지 입력 가능합니다");
        }
        if (item.getQuantity() == null || item.getQuantity() < 0 || item.getQuantity() > 9999) {
            errors.put("quantity", "수량은 최대 9,999개까지 입력 가능합니다");
        }
        if (item.getPrice() != null && item.getQuantity() != null && item.getPrice() * item.getQuantity() < 10_000) {
            errors.put("globalError", "가격과 수량의 곱은 10,000원 이상이어야 합니다. 현재 값 = " + item.getPrice() * item.getQuantity());
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
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

