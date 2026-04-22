package com.sjkorpela.RiichiPointsCalculator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class of Riichi Points Calculator.
 */
@SpringBootApplication
public class RiichiPointsCalculatorApplication {

	/**
	 * Spring Boot default app runner function.
	 *
	 * @param args command line arguments, not used
	 */
	public static void main(String[] args) {
		SpringApplication.run(RiichiPointsCalculatorApplication.class, args);
	}

}
