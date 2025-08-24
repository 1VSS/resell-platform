package br.com.vss.resell_platform.repository;

import br.com.vss.resell_platform.model.Item;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> , JpaSpecificationExecutor<Item> {

    List<Item> findAll(Specification<Item> specification);

}
