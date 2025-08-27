package ru.wb.repo;

import ru.wb.entity.TowarEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MacBookRepo extends JpaRepository<TowarEntity, Long> {

}
