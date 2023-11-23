package com.akhund.inventoryservice;

import com.akhund.inventoryservice.model.Inventory;
import com.akhund.inventoryservice.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadData(InventoryRepository inventoryRepository) {
		return args -> {
			Inventory inventory1 = Inventory.builder()
					.skuCode("iphone_13")
					.quantity(100)
					.build();

			Inventory inventory2 = Inventory.builder()
					.skuCode("iphone_14")
					.quantity(0)
					.build();

			Inventory inventory3 = Inventory.builder()
					.skuCode("iphone_15")
					.quantity(10)
					.build();

			inventoryRepository.save(inventory1);
			inventoryRepository.save(inventory2);
			inventoryRepository.save(inventory3);
		};
	}

}
