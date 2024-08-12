package org.example;

import javax.swing.text.Position;
import java.util.Optional;

public class VariableDeclaration implements ASTNode {

    private final Identifier identifier;
    private final Type type;
    private final Optional<Expression> expression;

    public VariableDeclaration(Identifier identifier, Type type, Optional<Expression> expression) {
        this.identifier = identifier;
        this.type = type;
        this.expression = expression;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public Type getType() {
        return type;
    }

    public Optional<Expression> getExpression() {
        return expression;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
        identifier.accept(visitor);
        type.accept(visitor);
        if (expression.isPresent()){
            expression.get().accept(visitor);
        }
    }

    @Override
    public String toString() {
        return identifier.toString() + " : " + type.toString() + ";\n";
    }
}
