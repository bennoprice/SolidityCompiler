import ast.Symbol;
import ast.TreeNode;

import java.io.PrintStream;
import java.util.EnumMap;

public class Utilities {
    private static int lexErrors = 0;
    private static int parseErrors = 0;
    private static int semantErrors = 0;
    private static PrintStream errorStream = System.err;;

    public enum ErrorCode {
		OPEN_FILE,
		EXCEPTION_LEXER,
		EXCEPTION_PARSER,
		LEXER_PARSER
	}

	private static EnumMap<ErrorCode, String> errorMessages = new EnumMap<ErrorCode, String>(ErrorCode.class);
	static {
			errorMessages.put(ErrorCode.OPEN_FILE, "Could not open input file %s.");
			errorMessages.put(ErrorCode.EXCEPTION_LEXER, "Unexpected exception in lexer");
			errorMessages.put(ErrorCode.EXCEPTION_PARSER, "Unexpected exception in parser");
			errorMessages.put(ErrorCode.LEXER_PARSER, "Compilation halted due to lex and parse errors");
	};

    /** Prints error message and exits
    *
    * @param msg the error message
    * */
	public static void fatalError(String msg) {
        errorStream.println(msg);
		System.exit(1);
	}

    /** Prints error message and exits
     *
     * @param code the error message
     * */
	public static void fatalError(ErrorCode code, Object... args) {
		fatalError(String.format(errorMessages.get(code), args));
	}

    /**
     * Prints the file name and the line number of the given tree node.
     * <p>
     * Also increments semantic error count.
     *
     * @param filename the file name
     * @param t        the tree node
     * to be printed.
     */
    public static void semantError(Symbol filename, TreeNode t, String msg) {
        fatalError(filename.getName() + ":" + t.getLineNumber() + ": " + msg);
    }

    /**
     * Returns true if there are any static semantic errors.
     */
    public static boolean errors() {
        return (semantErrors != 0 || parseErrors != 0 || lexErrors != 0);
    }

    public static PrintStream lexError() {
        lexErrors++;
        return errorStream;
    }
}


	    
	

