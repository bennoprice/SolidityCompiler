public class CgenCreation {
    public static String emit(String runtimeCode) {
        var asm = new Assembler();

        asm.PUSH(4, runtimeCode.length() / 2);
        asm.DUP(1);

        // this is the length of the creation code we are building here
        // hardcoding is a bit hacky but acceptable since it remains constant
        asm.PUSH(1, 14);

        asm.PUSH(1, 0);
        asm.CODECOPY();
        asm.PUSH(1, 0);
        asm.RETURN();

        return asm.get() + runtimeCode;
    }
}
