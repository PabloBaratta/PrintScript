package org.example;
import java.util.Optional;

public class Rule {

	private final boolean rule;
	private final Optional<Integer> qty;

	public Rule(boolean rule, Optional<Integer> qty) {
		this.rule = rule;
		this.qty = qty;
	}

	public boolean getRule() {
		return rule;
	}

	public Optional<Integer> getQty() {
		return qty;
	}
}
