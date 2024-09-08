package org.linter;

import org.example.TextLiteral;
import org.example.lexer.token.Position;
import org.junit.jupiter.api.Test;
import org.linter.configurator.Configurator;
import org.linter.configurator.IdentifierConfiguration;
import org.linter.configurator.OneArgFunctionConfiguration;

import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;


public class LinterTest {

	@Test
	public void analyzeCorrectTest() throws Exception {
		Linter linter = LinterProvider.getLinterV10();
		Map<String, String> configMap = Map.of("case", "camel", "printWithIdentifiers", "true");
		String identifier = "camel";
		TextLiteral hi = new TextLiteral("hi", new Position(0, 0, 0, 0));
		Report analyze = linter.analyze(LinterConfiguratorTest.iteratorProgram(identifier, hi), configMap);
		assertTrue(analyze.getReportLines().isEmpty());
	}

	@Test
	public void analyzeIncorrectCase() throws Exception {
		Linter linter = LinterProvider.getLinterV10();
		Map<String, String> configMap = Map.of("case", "camel", "printWithIdentifiers", "true");
		String identifier = "snake__";
		TextLiteral hi = new TextLiteral("hi", new Position(0, 0, 0, 0));
		Report analyze = linter.analyze(LinterConfiguratorTest.iteratorProgram(identifier, hi), configMap);
		assertFalse(analyze.getReportLines().isEmpty());
	}

	@Test
	public void analyzeNotKnownProperty() {
		Linter linter = LinterProvider.getLinterV10();
		Map<String, String> configMap = Map.of("case", "camel", "growPotatoes", "true");
		String identifier = "someIdentifier";
		TextLiteral hi = new TextLiteral("hi", new Position(0, 0, 0, 0));
		assertThrows(WrongConfigurationException.class,
			() -> linter.analyze(LinterConfiguratorTest.iteratorProgram(identifier, hi), configMap));
	}

	@Test
	public void analyzeCorrectTestV11() throws Exception {
		Linter linter = LinterProvider.getLinterV11();
		Map<String, String> configMap = Map.of("case", "camel", "printWithIdentifiers", "true");
		String identifier = "camel";
		TextLiteral hi = new TextLiteral("hi", new Position(0, 0, 0, 0));
		Report analyze = linter.analyze(LinterConfiguratorTest.iteratorProgramIf(identifier, hi), configMap);
		assertTrue(analyze.getReportLines().isEmpty());
	}

	@Test
	public void analyzeCorrectIfTest() throws Exception {
		Linter linter = LinterProvider.getLinterV11();
		Map<String, String> configMap = Map.of("case", "camel", "printWithIdentifiers", "true");
		String identifier = "camel";
		TextLiteral hi = new TextLiteral("hi", new Position(0, 0, 0, 0));
		Report analyze = linter.analyze(LinterConfiguratorTest.iteratorProgramIf(identifier, hi), configMap);
		assertTrue(analyze.getReportLines().isEmpty());
	}

	@Test
	public void analyzeInCorrectIfTest() throws Exception {
		Linter linter = LinterProvider.getLinterV11();
		Map<String, String> configMap = Map.of("case", "camel", "printWithIdentifiers", "true");
		String identifier = "camel__case";
		TextLiteral hi = new TextLiteral("hi", new Position(0, 0, 0, 0));
		Report analyze = linter.analyze(LinterConfiguratorTest.iteratorProgramIf(identifier, hi), configMap);
		assertFalse(analyze.getReportLines().isEmpty());
	}




}
