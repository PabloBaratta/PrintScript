package org.linter.configurator;

import org.example.ASTVisitor;
import org.linter.Report;
import org.linter.RuleBasicConfig;
import org.linter.WrongConfigurationException;
import org.linter.visitors.Case;
import org.linter.visitors.IdentifierRules;
import org.linter.visitors.PrintLineRules;

import java.util.Map;

public class PrintLineConfiguration implements Configurator {

	private final String ruleName = "printWithIdentifiers";
	private final Map<String, Boolean> mapOptionCase = Map.of("true", true, "false", false);
	private final RuleBasicConfig config = RuleBasicConfig.rule(mapOptionCase.keySet().toArray(new String[0]), "false");

	public PrintLineConfiguration() {
	}

	@Override
	public boolean isThisRule(String ruleName) {
		return this.ruleName.equals(ruleName);
	}

	@Override
	public boolean isValidRuleOption(String ruleOption) {
		return config.isThisOption(ruleOption);
	}

	@Override
	public String getProp() {
		return ruleName;
	}

	private String[] getOptions() {
		return config.options();
	}

	@Override
	public ASTVisitor getLinterRule(Report report, String ruleOption) throws WrongConfigurationException {
		if (!isValidRuleOption(ruleOption)) {
			throw new WrongConfigurationException(ruleName, ruleOption, getOptions());
		}
		return new PrintLineRules(mapOptionCase.get(ruleOption), report);
	}

	@Override
	public ASTVisitor getLinterRule(Report report) {
		return new PrintLineRules(mapOptionCase.get(config.defaultValue()), report);
	}
}
