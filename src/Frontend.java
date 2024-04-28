import ast.ProgramNode;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Frontend {
    public static ProgramNode lexparse(String path) {
        try {
            var input = new ANTLRFileStream(path);
            var lexer = new SolidityLexer(input);
            var tokens = new CommonTokenStream(lexer);
            var parser = new SolidityParser(tokens);

            var tree = parser.program();

            if (Utilities.errors())
                Utilities.fatalError(Utilities.ErrorCode.LEXER_PARSER);

            var builder = new ASTBuilder();
            return (ProgramNode)builder.visit(tree);

        } catch (FileNotFoundException ex) {
            Utilities.fatalError(Utilities.ErrorCode.OPEN_FILE, path);
        } catch (IOException ex) {
            Utilities.fatalError(Utilities.ErrorCode.EXCEPTION_LEXER, ex);
        } catch (Exception ex) {
            ex.printStackTrace();
            Utilities.fatalError(Utilities.ErrorCode.EXCEPTION_PARSER, ex);
        }
        return null;
    }
}
