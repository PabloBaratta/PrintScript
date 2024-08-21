package org.linter;

import org.example.Program;
import org.linter.visitors.LinterVisitor;

import java.util.Map;

public class Linter {

	private final LinterConfigurator configurator;

	public Linter(LinterConfigurator configurator) {
		this.configurator = configurator;
	}

	public Report analyze (Program program, Map<String, String> conf) throws WrongConfigurationException, Exception {

		Report report = new Report();
		LinterVisitor linterFromConfig = configurator.getLinterFromConfig(conf, report);
		linterFromConfig.visit(program);

		return report;
	}
}
