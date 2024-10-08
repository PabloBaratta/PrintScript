package org.example;

import org.token.Position;

import java.math.BigDecimal;

public class NumericLiteral extends Literal<BigDecimal> {

	public NumericLiteral(BigDecimal value, Position position) {
		super(value, position);
	}

	@Override
	public void accept(ASTVisitor visitor) throws Exception {
		visitor.visit(this);
	}

	@Override
	public String toString() {

		BigDecimal bigDecimalValue = getValue();
		String plainString = bigDecimalValue.toPlainString();

		if (plainString.contains(".")) {
			BigDecimal strippedValue = bigDecimalValue.stripTrailingZeros();
			String result = strippedValue.toPlainString();

			if (plainString.matches(".*\\d+\\.\\d+")) {
				if (!result.contains(".")) {
					return result + ".0";
				}
			}

			return result;
		}

		return plainString;
	}

	@Override
	public String toFormat() {
		return toString();
	}
}
