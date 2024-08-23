package org.linter;

import org.example.ASTVisitor;
import org.junit.jupiter.api.Test;
import org.linter.configurator.Configurator;
import org.linter.configurator.IdentifierConfiguration;
import org.linter.configurator.PrintLineConfiguration;

import static org.junit.jupiter.api.Assertions.*;

public class ConfiguratorTest {

	@Test
	public void identifierConfiguration() {

		IdentifierConfiguration identifierConfiguration = new IdentifierConfiguration();
		Report report = new Report();

		assertEquals("case", identifierConfiguration.getProp());
		assertTrue(identifierConfiguration.isThisRule("case"));
		assertTrue(identifierConfiguration.isValidRuleOption("snake"));
		assertTrue(identifierConfiguration.isValidRuleOption("camel"));


		assertDoesNotThrow(() -> {
			identifierConfiguration.getLinterRule(report, "camel");
		});

		assertDoesNotThrow(() -> {
			identifierConfiguration.getLinterRule(report, "snake");
		});

		assertThrows(WrongConfigurationException.class,() -> {
			identifierConfiguration.getLinterRule(report, "pascal");
		});

	}

	@Test
	public void printLineConfiguration() {

		PrintLineConfiguration printLineConfiguration = new PrintLineConfiguration();
		Report report = new Report();

		assertEquals("printWithIdentifiers", printLineConfiguration.getProp());
		assertTrue(printLineConfiguration.isThisRule("printWithIdentifiers"));
		assertTrue(printLineConfiguration.isValidRuleOption("true"));
		assertTrue(printLineConfiguration.isValidRuleOption("false"));


		assertDoesNotThrow(() -> {
			printLineConfiguration.getLinterRule(report, "true");
		});

		assertThrows(WrongConfigurationException.class,() -> {
			printLineConfiguration.getLinterRule(report, "True");
		});

	}
}
