package com.btp.project;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.btp.project.Repository.VehicleRepository;
import com.btp.project.components.vehicle.Vehicle;

@SpringBootApplication
@RestController
public class ProjectApplication {


  public static void main(String[] args) {
    SpringApplication.run(ProjectApplication.class, args);
  }

  @GetMapping("/hello")
  public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
    return String.format("Hello %s!", name);
  }

  @Bean
  public CommandLineRunner demo(VehicleRepository repository) {
    return (args) -> {
      // save the EV models in database
      repository.save(new Vehicle("Hyundai Kona Electric", 3.5f));
      repository.save(new Vehicle("Tesla Model Y", 3.8f));
      repository.save(new Vehicle("Tesla Model 3", 4.56f));

    };
  }

}
