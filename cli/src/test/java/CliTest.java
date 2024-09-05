import org.example.Cli;
import org.example.Runner;
import org.example.lexer.token.Token;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CliTest {
	@Test
	public void testDoesNotThrow() {
		Cli cli = new Cli(Paths.get("").toAbsolutePath() + "/src/test/resources/terminalTest.txt");
		assertDoesNotThrow(cli::run);
	}

	@Test
	public void testThrow1() throws IOException {
		String filePath = Paths.get("").toAbsolutePath() + "/src/test/resources/terminalTest1.txt";

		writeToFile(filePath, "analyze");
		Cli cli1 = new Cli(filePath);
		assertThrows(RuntimeException.class, cli1::run);

		writeToFile(filePath, "format");
		Cli cli2 = new Cli(filePath);
		assertThrows(RuntimeException.class, cli2::run);

		writeToFile(filePath, "execute");
		Cli cli3 = new Cli(filePath);
		assertThrows(RuntimeException.class, cli3::run);

		writeToFile(filePath, "validate");
		Cli cli4 = new Cli(filePath);
		assertThrows(RuntimeException.class, cli4::run);
	}

	private void writeToFile(String path, String content) throws IOException {
		Files.write(Paths.get(path), content.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
	}

	@Test
	public void testRunner() throws Exception {
		List<Token> tokens =Runner.lexV11("const a: number = 1;");
		Runner.parseV11(tokens);
	}
}
