import ast.visitor.DumpVisitor;

public class Program {
    public static void main(String[] args) {
        if (args.length < 1)
            Utilities.fatalError("must specify (.SOL) source file");

        var program = Frontend.lexparse(args[0]);
        Semant.analyze(program);
        Cgen.emit(program);

        //var dump = new DumpVisitor(System.out);
        //dump.visit(program, "");
    }
}
