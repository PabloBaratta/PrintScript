package org.example.interpreter;

import org.example.*;

import java.util.*;

public class Interpreter implements ASTVisitor {

    private final Map<String, Variable> environment = new HashMap<>();
    private final Stack<Expression> stack = new Stack<>();

    @Override
    public void visit(Assignation assignation) throws Exception {
        Identifier identifier = assignation.getIdentifier();
        Expression expression = assignation.getExpression();
        if (environment.containsKey(identifier.toString())){
            evaluate(expression); // ver type safety
            Expression astNodeResult = stack.pop();
            Variable variable = environment.get(identifier.toString());
            if (astNodeResult instanceof TextLiteral && variable.getType().getTypeName().equals("string")){
                variable.setExpression(astNodeResult);
                environment.put(identifier.toString(), variable);
            } else if(astNodeResult instanceof NumericLiteral && variable.getType().getTypeName().equals("number")){
                variable.setExpression(astNodeResult);
                environment.put(identifier.toString(), variable);
            } else {
                throw new Exception("los tipos no coinciden");
            }
        }
        else {
            throw new Exception("la variable no esta declarada");
        }
    }

    @Override
    public void visit(VariableDeclaration variableDeclaration) throws Exception {
        Identifier identifier = variableDeclaration.getIdentifier();
        Type type = variableDeclaration.getType();
        if (variableDeclaration.getExpression().isPresent()){ // ver si la variable ya esta declarada, type safety
            evaluate(variableDeclaration.getExpression().get());
            Expression astNodeResult = stack.pop();
            if (type.getTypeName().equals("string") && astNodeResult instanceof TextLiteral){
                environment.put(identifier.toString(), new Variable(type, astNodeResult));
            } else if (type.getTypeName().equals("number") && astNodeResult instanceof NumericLiteral) {
                environment.put(identifier.toString(), new Variable(type, astNodeResult));
            }
            else {
               throw new Exception("los tipos no coinciden");
            }
        }
        else {
            environment.put(identifier.toString(), new Variable(type, null));
        }
    }

    @Override
    public void visit(Identifier identifier) {
        String identifierName = identifier.getName();
        if (environment.containsKey(identifierName)) {
            stack.push(environment.get(identifierName).getExpression());
        }
    }

    @Override
    public void visit(TextLiteral textLiteral) {
        stack.push(textLiteral);
    }

    @Override
    public void visit(NumericLiteral numericLiteral) {
        stack.push(numericLiteral);
    }

    @Override
    public void visit(Method method) throws Exception {
        evaluate(method.getArguments().getFirst()); // ver de evaluar otras funciones
        System.out.println(stack.pop());
    }


    @Override
    public void visit(UnaryExpression unaryExpression) throws Exception {
        /*if (!(unaryExpression.getArgument() instanceof Identifier)){
           throw new Exception("no es un identifier"); // ver operaciones logicas
        }
        if (environment.containsKey(unaryExpression.getArgument().toString())) {
            Variable variable = environment.get(unaryExpression.getArgument().toString());
            switch (unaryExpression.getOperator()) {
                case "++":
                    Object object = (double) variable.getObject() + 1;
                    variable.setObject(object);
                    environment.put(unaryExpression.getArgument().toString(), variable);
                    return object;
                case "--":
                    Object object1 = (double) variable.getObject() - 1;
                    variable.setObject(object1);
                    environment.put(unaryExpression.getArgument().toString(), variable);
                    return object1;
            }
        }*/
    }

    @Override
    public void visit(BinaryExpression binaryExpression) throws Exception {
        evaluate(binaryExpression.getLeft());
        evaluate(binaryExpression.getRight());
        Expression right = stack.pop();
        Expression left = stack.pop();
        if (binaryExpression.getOperator().equals("+")){
            if (left instanceof NumericLiteral && right instanceof NumericLiteral){
                stack.push(new NumericLiteral((Double) left.getValue() + (Double) right.getValue()));
            } else {
                stack.push(new TextLiteral((String) left.getValue() + (String) right.getValue()));
            }
        } else if (!(left instanceof NumericLiteral && right instanceof NumericLiteral)){
            throw new Exception("para las siguientes operaciones tienen que ser numeros");
        } else {
            switch (binaryExpression.getOperator()){
                case "-":
                    stack.push(new NumericLiteral((Double) left.getValue() - (Double) right.getValue()));
                case "/":
                    stack.push(new NumericLiteral((Double) left.getValue() / (Double) right.getValue())); // ver que pasa si es cero
                case "*":
                    stack.push(new NumericLiteral((Double) left.getValue() * (Double) right.getValue()));;
            }
        } throw new Exception("no es posible hacer la operacion");
    }

    @Override
    public void visit(Program program) throws Exception {
        List<ASTNode> children = program.getChildren();
        for (ASTNode child : children) {
            child.accept(this);
        }
    }


    private void evaluate(ASTNode node) throws Exception {
        node.accept(this);
    }

    public void visit(org.example.ASTNode ast) {
    }
}
