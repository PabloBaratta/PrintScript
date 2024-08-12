package org.example;

public class Literal<T extends Comparable<T>> implements Expression{

    private final T value;

    public Literal(T value) {
        this.value = value;
    }

    public T getValue(){
        return this.value;
    }

    @Override
    public void accept(ASTVisitor visitor) throws Exception {
        if (this instanceof TextLiteral){
            visitor.visit((TextLiteral) this);
        } else if (this instanceof NumericLiteral){
            visitor.visit((NumericLiteral) this);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
