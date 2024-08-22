package org.linter;

import org.example.ASTVisitor;
import org.linter.configurator.Configurator;
import org.linter.visitors.LinterVisitor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
public class LinterConfigurator {

	private final Map<String, Configurator> propToConfigurators;

	public LinterConfigurator(List<Configurator> configurators) {
		this.propToConfigurators = getPropToConfigurators(configurators);
	}

	public LinterVisitor getLinterFromConfig(Map<String, String> config, Report report)
			throws WrongConfigurationException {

		List<ASTVisitor> linters = new LinkedList<>();

		for (Map.Entry<String, String> stringStringEntry : config.entrySet()) {

			String property = stringStringEntry.getKey();
			checkIsValidOption(property);
			String option = stringStringEntry.getValue();

			Configurator configurator = propToConfigurators.get(property);
			ASTVisitor linterRule = configurator.getLinterRule(report, option);

			linters.add(linterRule);
		}

		return new LinterVisitor(linters);
	}

	private void checkIsValidOption(String property) throws WrongConfigurationException{
		if (!propToConfigurators.containsKey(property)) {
			throw new WrongConfigurationException(property);
		}
	}

	private Map<String, Configurator> getPropToConfigurators(List<Configurator> configurators) {

		Map<String, Configurator> map = new HashMap<>();
		configurators.forEach(c -> map.put(c.getProp(), c));
		return map;
	}
}
