package org.linter;

import org.example.TextLiteral;
import org.example.lexer.token.Position;
import org.junit.jupiter.api.Test;
import org.linter.configurator.Configurator;
import org.linter.configurator.IdentifierConfiguration;
import org.linter.configurator.PrintLineConfiguration;

import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;


public class LinterTest {

	@Test
	public void analyzeCorrectTest() throws Exception {
		List<Configurator> configurators = List.of(new IdentifierConfiguration(), new PrintLineConfiguration());
		LinterConfigurator linterConfigurator = new LinterConfigurator(configurators);
		Linter linter = new Linter(linterConfigurator);
		Map<String, String> configMap = Map.of("case", "camel", "printWithIdentifiers", "true");
		String identifierString = "camel";
		TextLiteral hi = new TextLiteral("hi", new Position(0, 0, 0, 0));
		Report analyze = linter.analyze(LinterConfiguratorTest.getProgram(identifierString, hi), configMap);
		assertTrue(analyze.getReportLines().isEmpty());
	}

	@Test
	public void analyzeIncorrectCase() throws Exception {
		List<Configurator> configurators = List.of(new IdentifierConfiguration(), new PrintLineConfiguration());
		LinterConfigurator linterConfigurator = new LinterConfigurator(configurators);
		Linter linter = new Linter(linterConfigurator);
		Map<String, String> configMap = Map.of("case", "camel", "printWithIdentifiers", "true");
		String identifierString = "snake__";
		TextLiteral hi = new TextLiteral("hi", new Position(0, 0, 0, 0));
		Report analyze = linter.analyze(LinterConfiguratorTest.getProgram(identifierString, hi), configMap);
		assertFalse(analyze.getReportLines().isEmpty());
	}

	@Test
	public void analyzeNotKnownProperty() {
		List<Configurator> configurators = List.of(new IdentifierConfiguration(), new PrintLineConfiguration());
		LinterConfigurator linterConfigurator = new LinterConfigurator(configurators);
		Linter linter = new Linter(linterConfigurator);
		Map<String, String> configMap = Map.of("case", "camel", "growPotatoes", "true");
		String identifierString = "someIdentifier";
		TextLiteral hi = new TextLiteral("hi", new Position(0, 0, 0, 0));
		assertThrows(WrongConfigurationException.class,
			() -> linter.analyze(LinterConfiguratorTest.getProgram(identifierString, hi), configMap));
	}


}
