package com.evtnet.evtnetback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.mercadopago.MercadoPagoConfig;

@SpringBootApplication(scanBasePackages = "com.evtnet.evtnetback")
@EnableJpaRepositories(basePackages = "com.evtnet.evtnetback.Repositories")
@EntityScan(basePackages = "com.evtnet.evtnetback.Entities")
public class EvtnetApplication {

	public static void main(String[] args) {
		SpringApplication.run(EvtnetApplication.class, args);

		MercadoPagoConfig.setAccessToken("APP_USR-faketoken0123456789");

		System.out.println("Evtnet se ha iniciado correctamente");
	}

}
