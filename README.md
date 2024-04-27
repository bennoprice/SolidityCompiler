# Featherweight Solidity Compiler
This compiler implements a proprietary **Compact ABI-encoding** which packs function arguments (i.e., *calldata*) tightly rather than padding it to 32-bytes.
This optimization is designed with Ethereum Rollups in mind, where calldata size constitutes the majority of the transaction cost.
Packing arguments tightly drastically decreases *calldata* size and lowers costs for end-users.

> **Note**  
> The compiler implements a featherweight Solidity and is not feature complete.

## Quick Start

> **Warning**  
> The compiler has not been audited and should not be used in a production setting.

1. Navigate to project root
   ```
   cd ...
   ```
2. Build the project
   ```
   ./buildme
   ```
3. Navigate to build folder
   ```
   cd ./build
   ```
4. Run the compiler passing source code path as first command-line argument
   ```
   ./compile "{PROJECT_ROOT_PATH}/examples/HelloWorld.sol"
   ```
