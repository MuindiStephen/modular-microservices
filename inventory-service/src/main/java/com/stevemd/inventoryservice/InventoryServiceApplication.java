package com.stevemd.inventoryservice;

//import com.stevemd.inventoryservice.util.DataLoader;
import com.stevemd.inventoryservice.util.DataLoader;
import lombok.Getter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Getter
@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	DataLoader dataLoader;

}
