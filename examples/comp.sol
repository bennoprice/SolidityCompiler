// SPDX-License-Identifier: MIT
pragma solidity 0.8.23;

contract Comp {
    bool private attr;

    function foo(uint32 x, uint32 y) public packed {
        attr = x < y;
    }
}
