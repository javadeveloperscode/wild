package ru.wb.restapi;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.wb.model.ItemRequest;
import ru.wb.model.ItemView;
import ru.wb.service.ItemService;


@RestController
@RequiredArgsConstructor
public class ItemController {

  private final ItemService itemService;

  @PostMapping("/v1/create_item")
  public ItemView create(@RequestBody ItemRequest itemRequest) {
    return itemService.addItem(itemRequest);
  }


  @GetMapping("/v1/get_item")
  public ItemView getItem(@RequestParam Long id) {
    return itemService.getItem(id);
  }



}
