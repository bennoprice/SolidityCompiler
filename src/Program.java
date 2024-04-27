import ast.visitor.DumpVisitor;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Program {
    public static void main(String[] args) {
        if (args.length < 1)
            Utilities.fatalError("Must specify (.SOL) source file");

        var program = Frontend.lexparse(args[0]);
        Semant.analyze(program);

        var runtimeCode = Cgen.emit(program);
        var creationCode = CgenCreation.emit(runtimeCode);

        var contractName = program.getContract().getName();
        var runtimeCodePath = "../out/" + contractName + ".runtime";
        var creationCodePath = "../out/" + contractName + ".creation";

        try (var out = new PrintWriter(runtimeCodePath)) {
            out.print(runtimeCode);
            System.out.println("Runtime code written to: " + runtimeCodePath);
        } catch (FileNotFoundException e) {
            Utilities.fatalError("Failed to write runtime output file: " + e.getMessage());
        }

        try (var out = new PrintWriter(creationCodePath)) {
            out.print(creationCode);
            System.out.println("Creation code written to: " + creationCodePath);
        } catch (FileNotFoundException e) {
            Utilities.fatalError("Failed to write creation output file: " + e.getMessage());
        }

        //var dump = new DumpVisitor(System.out);
        //dump.visit(program, "");
    }
}
