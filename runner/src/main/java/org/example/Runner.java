package org.example;


import org.example.interpreter.Interpreter;
import org.example.lexer.Lexer;
import org.example.lexer.token.Token;
import org.linter.Linter;

import java.util.List;

public class Runner {
	Lexer lexer;
	Parser parser;
	Interpreter interpreter;
	Linter linter;
	Formatter formatter;

	public Runner(Lexer lexer, Parser parser, Interpreter interpreter, Linter linter, Formatter formatter) {
		this.lexer = lexer;
		this.parser = parser;
		this.interpreter = interpreter;
		this.linter = linter;
		this.formatter = formatter;
	}

	public List<Token> lexLine() {
		//return lexer.getSentence();
		//este metodo estaria piola que este en el lexer sino hay que poner la logica aca
		//esto podria devolver un iterator si es que tokenBufer es un iterable
		return null;
	}

	public ASTNode getAST() {
		List<Token> tokenList = lexLine();
		if (tokenList == null || tokenList.isEmpty()){
			return null;
		}
		TokenBuffer buffer = new TokenBuffer(tokenList);
		//return parser.parse(tokenList);
		//estaria bueno que el parser tenga un metodo que reciba un tokenBuffer y no en el constructor
		return null;
	}

	public String format() throws Exception {
		// Program ast = getProgram(); ??
		// eso o que el formatter tenga un metodo que reciba un ASTNode
		ASTNode ast = getAST();
		return formatter.format((Program) ast);
	}

	public String lint() throws Exception {
		ASTNode ast = getAST();
		// Program ast = getProgram(); ??
		// eso o que el linter tenga un metodo que reciba un ASTNode
		//return linter.analyze((Program) ast, );
		return null;
	}

	public void execute() throws Exception {
		ASTNode ast = getAST();
		while (ast != null){
			interpreter.visit(ast);
			ast = getAST();
		}
	}
}
