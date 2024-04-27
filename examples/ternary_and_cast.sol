// SPDX-License-Identifier: MIT
pragma solidity 0.8.23;

contract TernaryAndCast {
    uint256 private attr;

    function foo(bool x, uint8 y) public packed {
        attr = x ? double(uint256(y)) : uint256(y);
    }

    function double(uint256 y) internal returns (uint256) {
        return y * 2;
    }
}
