package com.evtnet.evtnetback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.mercadopago.MercadoPagoConfig;

@SpringBootApplication(scanBasePackages = "com.evtnet.evtnetback")
@EnableJpaRepositories(basePackages = "com.evtnet.evtnetback.repository")
@EntityScan(basePackages = "com.evtnet.evtnetback.entity")
public class EvtnetApplication {

	public static void main(String[] args) {
		SpringApplication.run(EvtnetApplication.class, args);

		MercadoPagoConfig.setAccessToken("APP_USR-8930527766625121-101017-4c842a3303d545b27dd702af7008a195-2918163140");

		System.out.println("Evtnet se ha iniciado correctamente");
	}

}
