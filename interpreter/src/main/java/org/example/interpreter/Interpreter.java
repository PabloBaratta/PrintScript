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
        if (environment.containsKey(identifier.toString())){
            throw new Exception("la variable ya esta declarada");
        }
        if (variableDeclaration.getExpression().isPresent()){ // type safety
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
    public void visit(Identifier identifier) throws Exception {
        String identifierName = identifier.getName();
        if (environment.containsKey(identifierName)) {

            Variable variable = environment.get(identifierName);
            Optional<Expression> optionalExpression = variable.getExpression();

            if (optionalExpression.isEmpty()) {
                throw new Exception("variable was declared but not assigned");
            }

            stack.push(optionalExpression.get());
        }
        else {
            throw new Exception("undeclared variable");
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
        System.out.println(stack.pop().getValue());
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
                stack.push(new TextLiteral( left.getValue().toString() + right.getValue().toString()));
            }
        } else if (!(left instanceof NumericLiteral && right instanceof NumericLiteral)){
            throw new Exception("para las siguientes operaciones tienen que ser numeros");
        } else {
            switch (binaryExpression.getOperator()){
                case "-":
                    stack.push(new NumericLiteral((Double) left.getValue() - (Double) right.getValue()));
                    break;
                case "/":
                    stack.push(new NumericLiteral((Double) left.getValue() / (Double) right.getValue()));
                    break; // ver que pasa si es cero
                case "*":
                    stack.push(new NumericLiteral((Double) left.getValue() * (Double) right.getValue()));
                    break;
                default:
                    throw new Exception("Operador no válido: " + binaryExpression.getOperator());
            }
        }
    }

    @Override
    public void visit(Program program) throws Exception {
        List<ASTNode> children = program.getChildren();
        for (ASTNode child : children) {
            child.accept(this);
        }
    }

    @Override
    public void visit(Parenthesis parenthesis) throws Exception {
        evaluate(parenthesis.getExpression());
    }

    private void evaluate(ASTNode node) throws Exception {
        node.accept(this);
    }

    public Map<String, Variable> getEnvironment() {
        return environment;
    }

    public Stack<Expression> getStack() {
        return stack;
    }
}
