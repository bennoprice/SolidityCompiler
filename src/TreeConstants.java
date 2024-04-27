import ast.Symbol;

class TreeConstants {
    public static final Symbol void_ = StringTable.idtable.addString("void");
    public static final Symbol uint256 = StringTable.idtable.addString("uint256");
    public static final Symbol uint8 = StringTable.idtable.addString("uint8");
    public static final Symbol bool = StringTable.idtable.addString("bool");

    public static boolean isPrimitive(Symbol type) {
        return
                type == TreeConstants.void_ ||
                type == TreeConstants.uint256 ||
                type == TreeConstants.uint8 ||
                type == TreeConstants.bool;
    }

    public static boolean isInteger(Symbol type) {
        return
                type == TreeConstants.uint256 ||
                type == TreeConstants.uint8;
    }
}
