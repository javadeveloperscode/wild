package ru.wb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.wb.entity.TowarEntity;
import ru.wb.model.ItemRequest;
import ru.wb.model.ItemView;
import ru.wb.repo.MacBookRepo;


@Service
@RequiredArgsConstructor
public class ItemService {

  private final MacBookRepo macBookRepo;

  public ItemView addItem(ItemRequest itemRequest) {

    TowarEntity towarEntity = new TowarEntity();
    towarEntity.setBrand(itemRequest.getBrand());
    towarEntity.setName(itemRequest.getName());
    towarEntity.setPriceRub(itemRequest.getPrice());

    if (itemRequest.getPrice() > 1000){
      towarEntity.setPriceRub(itemRequest.getPrice() / 2);
    }

    macBookRepo.save(towarEntity);

    return new ItemView(towarEntity.getBrand(), towarEntity.getName(),towarEntity.getPriceRub());
  }

  public ItemView getItem(Long id) {
    TowarEntity towarEntity = macBookRepo.findById(id).orElse(null);
    if (towarEntity == null) {
      throw new RuntimeException("Idi nax");
    }
    return new ItemView(towarEntity.getBrand(), towarEntity.getName(),towarEntity.getPriceRub());
  }
}
