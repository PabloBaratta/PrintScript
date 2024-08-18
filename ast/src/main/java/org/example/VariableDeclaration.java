package org.example;

import org.example.lexer.token.Position;

import java.util.Optional;

public class VariableDeclaration implements ASTNode {

    private final Identifier identifier;
    private final Type type;
    private final Optional<Expression> expression;
    private final Position position;

    public VariableDeclaration(Identifier identifier, Type type, Optional<Expression> expression, Position position) {
        this.identifier = identifier;
        this.type = type;
        this.expression = expression;
        this.position = position;
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
    public void accept(ASTVisitor visitor) throws Exception {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return identifier.toString() + " : " + type.toString() + ";\n";
    }

    @Override
    public Position getPosition() {
        return position;
    }
}
