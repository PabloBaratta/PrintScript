package org.example;

import org.token.Position;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

		if (bigDecimalValue.scale() > 1) {
			// Forzamos a que tenga exactamente 1 decimal, si tiene más los reducimos
			return bigDecimalValue.setScale(1, RoundingMode.DOWN).toPlainString();
		}

		// Si no tiene decimales, mostramos el número sin punto decimal
		return bigDecimalValue.toPlainString();
	}
}
