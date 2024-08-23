package org.linter.visitors;


import java.util.regex.Pattern;

public enum Case {
	CAMEL_CASE ("(__)?[a-z]+(?:[A-Z][a-z]*)*\\d*"),
	SNAKE_CASE("(?!.*__)[a-z]+(_[a-z]+)*(_\\d+)?");

	private final String regex;

	Case(String regex){
		this.regex = regex;
	}


	public Pattern getRegex() {
		return Pattern.compile("^" + regex);
	}

}
