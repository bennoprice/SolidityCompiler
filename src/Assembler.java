import java.util.ArrayList;
import java.util.HashMap;

class OpCode {
    public static int STOP = 0x00;
    public static int ADD = 0x01;
    public static int MUL = 0x02;
    public static int SUB = 0x03;
    public static int DIV = 0x04;
    public static int PUSH = 0x5f;
    public static int POP = 0x50;
    public static int DUP = 0x80;
    public static int SWAP = 0x90;
    public static int SHR = 0x1c;
    public static int REVERT = 0xfd;
    public static int CALLDATASIZE = 0x36;
    public static int CALLDATALOAD = 0x35;
    public static int CODECOPY = 0x39;
    public static int RETURN = 0xf3;
    public static int SLOAD = 0x54;
    public static int SSTORE = 0x55;
    public static int JUMP = 0x56;
    public static int JUMPI = 0x57;
    public static int JUMPDEST = 0x5B;
    public static int ISZERO = 0x15;
    public static int LT = 0x10;
    public static int GT = 0x11;
    public static int EQ = 0x14;

}

class LabelReference {
    public int label;
    public int offset;

    LabelReference(int label, int offset) {
        this.label = label;
        this.offset = offset;
    }
}

public class Assembler {
    private int label = 0;
    private StringBuilder s = new StringBuilder();
    private ArrayList<LabelReference> unresolved = new ArrayList<>();
    private HashMap<Integer, Integer> resolved = new HashMap<>();

    private void addLabelRef(int label) {
        unresolved.add(new LabelReference(label, s.length() + 2));
    }

    private void resolveLabelRefs() {
        for (var ref : unresolved) {
            if (!resolved.containsKey(ref.label))
                Utilities.fatalError("Assembler.get: unresolved reference/s");

            var address = String.format("%08X", resolved.get(ref.label) / 2);
            s.replace(ref.offset, ref.offset + 8, address);
        }
        unresolved.clear();
    }

    public Assembler OP(int opcode) {
        s.append(String.format("%02X", opcode));
        return this;
    }

    public int getLabel() {
        return label++;
    }

    public Assembler PUSH(int bytes, String immediate) {
        if (bytes > 32)
            Utilities.fatalError("Assembler.PUSH: bytes exceeds maximum of 32");

        if (immediate.length() != bytes * 2)
            Utilities.fatalError("Assembler.PUSH: immediate length mismatch");

        s.append(String.format("%02X", OpCode.PUSH + bytes));
        s.append(immediate);
        return this;
    }

    public Assembler PUSH(int bytes, int immediate) {
        var str = String.format("%0" + (bytes * 2) + "X", immediate);
        return PUSH(bytes, str);
    }

    public Assembler PUSH(int label) {
        addLabelRef(label);
        return PUSH(4, 0);
    }

    public Assembler JUMPI(int label) {
        addLabelRef(label);
        PUSH(4, 0);
        return OP(OpCode.JUMPI);
    }

    public Assembler JUMP(int label) {
        addLabelRef(label);
        PUSH(4, 0);
        return OP(OpCode.JUMP);
    }

    public Assembler JUMP() {
        return OP(OpCode.JUMP);
    }

    public Assembler JUMPDEST(int label) {
        resolved.put(label, s.length());
        return OP(OpCode.JUMPDEST);
    }

    public Assembler DUP(int slot) {
        if (slot > 16)
            Utilities.fatalError("Assembler.PUSH: stack too deep");

        s.append(String.format("%02X", OpCode.DUP + slot - 1));
        return this;
    }

    public Assembler SWAP(int slot) {
        if (slot > 16)
            Utilities.fatalError("Assembler.SWAP: stack too deep");

        s.append(String.format("%02X", OpCode.SWAP + slot - 1));
        return this;
    }

    public Assembler CALLDATASIZE() {
        return OP(OpCode.CALLDATASIZE);
    }

    public Assembler POP() {
        return OP(OpCode.POP);
    }

    public Assembler STOP() {
        return OP(OpCode.STOP);
    }

    public Assembler ADD() {
        return OP(OpCode.ADD);
    }

    public Assembler MUL() {
        return OP(OpCode.MUL);
    }

    public Assembler SUB() {
        return OP(OpCode.SUB);
    }

    public Assembler DIV() {
        return OP(OpCode.DIV);
    }

    public Assembler ISZERO() {
        return OP(OpCode.ISZERO);
    }

    public Assembler LT() {
        return OP(OpCode.LT);
    }

    public Assembler GT() {
        return OP(OpCode.GT);
    }

    public Assembler EQ() {
        return OP(OpCode.EQ);
    }

    public Assembler REVERT() {
        return OP(OpCode.REVERT);
    }

    public Assembler CALLDATALOAD() {
        return OP(OpCode.CALLDATALOAD);
    }

    public Assembler CODECOPY() {
        return OP(OpCode.CODECOPY);
    }

    public Assembler RETURN() {
        return OP(OpCode.RETURN);
    }

    public Assembler SLOAD() {
        return OP(OpCode.SLOAD);
    }

    public Assembler SSTORE() {
        return OP(OpCode.SSTORE);
    }

    public Assembler SHR() {
        return OP(OpCode.SHR);
    }

    public String get() {
        resolveLabelRefs();
        return s.toString();
    }
}
