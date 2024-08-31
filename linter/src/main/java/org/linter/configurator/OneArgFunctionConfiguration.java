package org.linter.configurator;

import org.example.ASTVisitor;
import org.linter.Report;
import org.linter.RuleBasicConfig;
import org.linter.WrongConfigurationException;
import org.linter.visitors.OneArgFunRules;

import java.util.Map;

public class OneArgFunctionConfiguration implements Configurator {

	private final String ruleName;
	private final Map<String, Boolean> mapOptionCase = Map.of(
			"true", true,
			"false", false);
	private final RuleBasicConfig config = RuleBasicConfig.rule(
			mapOptionCase.keySet().toArray(new String[0]), "false");
	private final String methodName;

	public OneArgFunctionConfiguration(String ruleName, String methodName) {
		this.ruleName = ruleName;
		this.methodName = methodName;
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
	public ASTVisitor getLinterRule(Report report, String ruleOption)
			throws WrongConfigurationException {
		if (!isValidRuleOption(ruleOption)) {
			throw new WrongConfigurationException(ruleName, ruleOption, getOptions());
		}
		return new OneArgFunRules(mapOptionCase.get(ruleOption), report, methodName);
	}

	@Override
	public ASTVisitor getLinterRule(Report report) {
		return new OneArgFunRules(mapOptionCase.get(config.defaultValue()), report, methodName);
	}
}
