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
import yechan.inflearn_spring_mvc_2_3.web.validation.dto.ItemSaveDto;
import yechan.inflearn_spring_mvc_2_3.web.validation.dto.ItemUpdateDto;

import java.util.List;

@Controller
@RequestMapping("/validation/v4/items")
@RequiredArgsConstructor
@Slf4j
public class ValidationItemControllerV4 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v4/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v4/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v4/addForm";
    }

    @PostMapping("/add")
    public String addItem(@Validated @ModelAttribute("item") ItemSaveDto itemDto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (itemDto.getPrice() != null && itemDto.getQuantity() != null && itemDto.getPrice() * itemDto.getQuantity() < 10_000) {
            bindingResult.reject("totalPriceMin", new Object[]{10_000, itemDto.getPrice() * itemDto.getQuantity()}, null);
        }

        if (bindingResult.hasErrors()) {
            return "validation/v4/addForm";
        }

        Item item = new Item(itemDto.getItemName(), itemDto.getPrice(), itemDto.getQuantity());

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v4/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v4/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @Validated @ModelAttribute("item") ItemUpdateDto itemDto, BindingResult bindingResult) {
        if (itemDto.getPrice() != null && itemDto.getQuantity() != null && itemDto.getPrice() * itemDto.getQuantity() < 10_000) {
            bindingResult.reject("totalPriceMin", new Object[]{10_000, itemDto.getPrice() * itemDto.getQuantity()}, null);
        }

        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v4/editForm";
        }

        Item item = new Item(itemDto.getItemName(), itemDto.getPrice(), itemDto.getQuantity());

        itemRepository.update(itemId, item);
        return "redirect:/validation/v4/items/{itemId}";
    }
}

