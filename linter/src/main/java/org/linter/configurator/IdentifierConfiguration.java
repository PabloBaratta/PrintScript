package org.linter.configurator;

import org.example.ASTVisitor;
import org.linter.Report;
import org.linter.RuleBasicConfig;
import org.linter.WrongConfigurationException;
import org.linter.visitors.Case;
import org.linter.visitors.IdentifierRules;

import java.util.Map;

public class IdentifierConfiguration implements Configurator {

	private final String ruleName = "case";
	private final Map<String, Case> mapOptionCase = Map.of(
			"camel", Case.CAMEL_CASE,
			"snake", Case.SNAKE_CASE);
	private final RuleBasicConfig config = RuleBasicConfig.rule(
			mapOptionCase.keySet().toArray(new String[0]), "camel");

	public IdentifierConfiguration() {}
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
	public ASTVisitor getLinterRule(Report report, String ruleOption)
			throws WrongConfigurationException {
		if (!isValidRuleOption(ruleOption)) {
			throw new WrongConfigurationException(ruleName, ruleOption, getOptions());
		}
		return new IdentifierRules(mapOptionCase.get(ruleOption), report);
	}
    @Override
	public ASTVisitor getLinterRule(Report report) {
		return new IdentifierRules(mapOptionCase.get(config.defaultValue()), report);
	}
}
