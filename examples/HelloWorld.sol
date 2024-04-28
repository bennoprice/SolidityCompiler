// SPDX-License-Identifier: MIT
pragma solidity 0.8.23;

contract HelloWorld {
    uint256 private attr;

    // accepts many single-byte values and sums them up
    // passes the sum to "bar"
    function foo(uint8 arg1, uint8 arg2, uint8 arg3, uint8 arg4, uint8 arg5, uint8 arg6, uint8 arg7, uint8 arg8) public packed {
        uint256 local = arg1 + arg2 + arg3 + arg4 + arg5 + arg6 + arg7 + arg8;
        local = mul(local, 2);
        bar(local);

        // single line comment

        /*
        this is a...
        multiline comment
        */
    }

    // receives the sum from "foo"
    // assigns the sum to an attribute (i.e., writes it to storage)
    function bar(uint256 arg) internal {
        attr = arg;
    }

    // returns the product of a * b
    function mul(uint256 a, uint256 b) internal returns (uint256) {
        return a * b;
    }
}
