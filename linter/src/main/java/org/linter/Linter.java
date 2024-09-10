package org.linter;

import org.example.ASTNode;
import org.example.PrintScriptIterator;
import org.example.Program;
import org.linter.visitors.LinterVisitor;

import java.util.Map;

public class Linter {

	private final LinterConfigurator configurator;

	public Linter(LinterConfigurator configurator) {
		this.configurator = configurator;
	}

	public Report analyze (PrintScriptIterator<ASTNode> iterator, Map<String, String> conf)
			throws Exception {

		Report report = new Report();
		LinterVisitor linterFromConfig = configurator.getLinterFromConfig(conf, report);
		linterFromConfig.visitAll(iterator);
		return report;
	}
}
