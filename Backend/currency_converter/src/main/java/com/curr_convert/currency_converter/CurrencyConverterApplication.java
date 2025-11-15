package com.curr_convert.currency_converter;

import com.curr_convert.currency_converter.service.api.APIConnector;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class CurrencyConverterApplication {

	public static void main(String[] args) {

        ApplicationContext context= SpringApplication.run(CurrencyConverterApplication.class, args);
	}

}
