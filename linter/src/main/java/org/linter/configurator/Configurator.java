package org.linter.configurator;

import org.example.ASTVisitor;
import org.linter.Report;
import org.linter.WrongConfigurationException;

public interface Configurator {
    boolean isThisRule(String ruleName);

    boolean isValidRuleOption(String ruleOption);

    String getProp();

    ASTVisitor getLinterRule(Report report, String ruleOption) throws WrongConfigurationException;

    ASTVisitor getLinterRule(Report report);
}
