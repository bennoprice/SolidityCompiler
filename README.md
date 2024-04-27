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

This generates two output files in the `./out` directory:

1. **Runtime bytecode** which is deployed smart contract code stored on the blockchain.  
   This can be executed using online EVM interpreters such as [evmcodes](https://www.evm.codes/playground)
2. **Creation bytecode** which is the code that deploys the smart contract to the blockchain.  
   This is sent as transaction data to deploy the smart contract to EVM-compatible networks.
