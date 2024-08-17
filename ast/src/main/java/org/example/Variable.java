package org.example;

import java.util.Optional;

public class Variable {

    private Type type;
    private Optional<Expression> expression;

    public Variable(Type type, Expression expression){
        this.type = type;
        this.expression = Optional.ofNullable(expression);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Optional<Expression> getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = Optional.ofNullable(expression);
    }
}
