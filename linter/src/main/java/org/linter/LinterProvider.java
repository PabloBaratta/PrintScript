package org.linter;

import org.linter.configurator.Configurator;
import org.linter.configurator.IdentifierConfiguration;
import org.linter.configurator.OneArgFunctionConfiguration;

import java.util.LinkedList;
import java.util.List;

public class LinterProvider {

	public static Linter getLinterV10() {
		List<Configurator> configurators = getConfiguratorsV1();
		LinterConfigurator linterConfigurator = new LinterConfigurator(configurators);
		return new Linter(linterConfigurator);
	}

	public static Linter getLinterV11() {
		List<Configurator> configurators = getConfiguratorsV11();
		LinterConfigurator linterConfigurator = new LinterConfigurator(configurators);
		return new Linter(linterConfigurator);
	}

	private static List<Configurator> getConfiguratorsV11() {
		String readInputOp = "readInputWithIdentifiers";
		String readInpMethodName = "readInput";
		Configurator readInputConfiguration = new OneArgFunctionConfiguration(readInputOp, readInpMethodName);
		List<Configurator> configurators = new LinkedList<>();
		configurators.add(readInputConfiguration);
		configurators.addAll(getConfiguratorsV1());
		return configurators;
	}

	private static List<Configurator> getConfiguratorsV1() {
		String printLnOption = "printWithIdentifiers";
		String printMethodName = "println";
		Configurator printLnConfiguration = new OneArgFunctionConfiguration(printLnOption, printMethodName);
		return List.of(new IdentifierConfiguration(), printLnConfiguration);
	}
}
