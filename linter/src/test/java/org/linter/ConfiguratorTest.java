package org.linter;

import org.junit.jupiter.api.Test;
import org.linter.configurator.Configurator;
import org.linter.configurator.IdentifierConfiguration;
import org.linter.configurator.OneArgFunctionConfiguration;

import static org.junit.jupiter.api.Assertions.*;

public class ConfiguratorTest {

	@Test
	public void identifierConfiguration() {

		IdentifierConfiguration identifierConfiguration = new IdentifierConfiguration();
		Report report = new Report();

		assertEquals("identifier_format", identifierConfiguration.getProp());
		assertTrue(identifierConfiguration.isThisRule("identifier_format"));
		assertTrue(identifierConfiguration.isValidRuleOption("snake case"));
		assertTrue(identifierConfiguration.isValidRuleOption("camel case"));

		assertDoesNotThrow(() -> {
			identifierConfiguration.getLinterRule(report, "camel case");
		});

		assertDoesNotThrow(() -> {
			identifierConfiguration.getLinterRule(report, "snake case");
		});

		assertThrows(WrongConfigurationException.class,() -> {
			identifierConfiguration.getLinterRule(report, "pascal");
		});

	}

	@Test
	public void printLineConfiguration() {

		String printOptions = "printWithIdentifiers";
		String method = "println";
		Configurator oneArgFunctionConfiguration = new OneArgFunctionConfiguration(printOptions, method);
		Report report = new Report();

		assertEquals("printWithIdentifiers", oneArgFunctionConfiguration.getProp());
		assertTrue(oneArgFunctionConfiguration.isThisRule("printWithIdentifiers"));
		assertTrue(oneArgFunctionConfiguration.isValidRuleOption("true"));
		assertTrue(oneArgFunctionConfiguration.isValidRuleOption("false"));

		assertDoesNotThrow(() -> {
			oneArgFunctionConfiguration.getLinterRule(report, "true");
		});

		assertThrows(WrongConfigurationException.class,() -> {
			oneArgFunctionConfiguration.getLinterRule(report, "True");
		});

	}
}
