package org.linter;

import java.util.List;

public record RuleBasicConfig (String[] options, String defaultValue) {

	public static RuleBasicConfig rule(String[] options, String defaultValue) {
		return new RuleBasicConfig(options, defaultValue);
	}

	public boolean isThisOption(String option) {
		return List.of(options).contains(option);
	}
}
