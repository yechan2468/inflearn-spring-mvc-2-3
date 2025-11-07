package yechan.inflearn_spring_mvc_2_3.web.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import yechan.inflearn_spring_mvc_2_3.domain.item.Item;
import yechan.inflearn_spring_mvc_2_3.domain.item.ItemRepository;
import yechan.inflearn_spring_mvc_2_3.domain.item.SaveCheck;
import yechan.inflearn_spring_mvc_2_3.domain.item.UpdateCheck;

import java.util.List;

@Controller
@RequestMapping("/validation/v3/items")
@RequiredArgsConstructor
@Slf4j
public class ValidationItemControllerV3 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v3/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v3/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v3/addForm";
    }

//    @PostMapping("/add")
    public String addItem(@Validated(SaveCheck.class) @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (item.getPrice() != null && item.getQuantity() != null && item.getPrice() * item.getQuantity() < 10_000) {
            bindingResult.reject("totalPriceMin", new Object[]{10_000, item.getPrice() * item.getQuantity()}, null);
        }

        if (bindingResult.hasErrors()) {
            return "validation/v3/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v3/items/{itemId}";
    }

    @PostMapping("/add")
    public String addItem2(@Validated @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (item.getPrice() != null && item.getQuantity() != null && item.getPrice() * item.getQuantity() < 10_000) {
            bindingResult.reject("totalPriceMin", new Object[]{10_000, item.getPrice() * item.getQuantity()}, null);
        }

        if (bindingResult.hasErrors()) {
            return "validation/v3/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v3/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v3/editForm";
    }

//    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @Validated @ModelAttribute Item item, BindingResult bindingResult) {
        if (item.getPrice() != null && item.getQuantity() != null && item.getPrice() * item.getQuantity() < 10_000) {
            bindingResult.reject("totalPriceMin", new Object[]{10_000, item.getPrice() * item.getQuantity()}, null);
        }

        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v3/editForm";
        }

        itemRepository.update(itemId, item);
        return "redirect:/validation/v3/items/{itemId}";
    }

    @PostMapping("/{itemId}/edit")
    public String editV2(@PathVariable Long itemId, @Validated(UpdateCheck.class) @ModelAttribute Item item, BindingResult bindingResult) {
        if (item.getPrice() != null && item.getQuantity() != null && item.getPrice() * item.getQuantity() < 10_000) {
            bindingResult.reject("totalPriceMin", new Object[]{10_000, item.getPrice() * item.getQuantity()}, null);
        }

        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v3/editForm";
        }

        itemRepository.update(itemId, item);
        return "redirect:/validation/v3/items/{itemId}";
    }
}

