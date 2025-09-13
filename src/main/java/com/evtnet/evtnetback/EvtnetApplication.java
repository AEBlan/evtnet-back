package com.evtnet.evtnetback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class EvtnetApplication {

	public static void main(String[] args) {
		SpringApplication.run(EvtnetApplication.class, args);
		System.out.println("Evtnet se ha iniciado correctamente");
	}

}
