# Crypto_SysSec
Anonymizing crypto currency transactions

In this project we will analyze ways to anonymize crypto currency transactions using zero knowledge proofs.
1. Step: Understanding smart contracts

To enter the world of crypto-currencies, we have designed our own Smart Contract in Solidity as a first step. Our newly invented currency is called "FlensCoin". With the help of the Solidity-IDE "Remix" you can test our created contract (flenscoin.sol). For testing on a test network you will need to install the MetaMask Chrome extension which allows using several testing networks.
The next task we set up our own blockchain was created on a virtual machine under the operating system ubuntu 20.4. A private test Ethereum blockchain was set up and started.
the following programs were installed geth (Go Ethereum) and solc(Solidity Compiler). To start your own private test Ethereum blockchain, manual initialization of the first Genesis block is required.
With the command geth we generate Genesis-Block.json

 geth --datadir "Flens-arena" init src/genesis-block.json

An account was also created and mining was started manually.Then a simple Solidity script for an Ethereum Smart Contract was created, compiled and deployed.

2. Step: Understanding crypto currency

Nonce: In cryptography, the term nonce was taken up to designate a combination of numbers or letters that is only used once in the respective context. Typical ways of generating a nonce are the use of (cryptographically secure) random values that are sufficiently large so that the probability of double use is negligible (see birthday paradox),

Transaction: Each node in the peer-to-peer network acts as a register and trustee who carries out changes of ownership and automatically maps verifiable rules about these transactions. All transactions are always audited by all other nodes.

If a participant now wants to transfer an amount to an account, he creates a transfer order with the amount and the public key of the target account and signs this order with his secret key. This order is published via the P2P network. It now has to be checked and certified and archived as a transaction in the joint accounting.

The steps in the operation of a decentralized cryptocurrency are:
1. New transactions are signed and sent to all nodes.
2. Each node collects new transactions in a block.
3. Each node looks for the nonce that validates its block.
4. When a node finds a valid block, it sends the block to all other nodes.
5. The nodes only accept the block if it is valid according to the rules:

    a. The hash value of the block must correspond to the current level of difficulty.
    
    b. All transactions must be correctly signed.
    
   c. The transactions must be covered in accordance with the previous blocks (no double spending).
   
   d. New issue and transaction fees must conform to the accepted rules.
   
6. The nodes express their acceptance of the block by adopting its hash value in their new blocks.

A smart bond is a special type of automated bond contract that leverages the capabilities of blockchain databases that can function as cryptographically secure, yet open and transparent ledgers. It belongs to a class of financial instruments known as a smart contract, "a computerized transaction log that executes the terms of a contract" 


3. Step : How DAI works

Stablecoins are cryptocurrencies, the price of which is controlled through active or automatic monetary policy with the aim of low volatility in relation to a national currency, a currency basket or other assets

Dai: The DAI Coin is a crypto currency that represents the equivalent of exactly 1 US dollar. Other crypto currencies such as Bitcoin, Ether & Co. have their own value that increases or decreases in value compared to a normal currency such as US dollars or euros.

Technologically speaking, DAI runs on the Ethereum blockchain. This coin therefore also represents a decentralized cryptocurrency.

There are several useful reasons to use DAI instead of the US dollar:
       1. The almost 2 billion people in the world without access to the banking system can participate in business life with the help of this cryptocurrency. In addition, this         currency serves as an inflation protection for citizens of a country with high inflation. To do this, they simply invest the local currency in DAI
       2. You can use DAI for smart contracts and thus carry out automated transactions.
       3. You can send the DAI Coin very quickly and cheaply around the world. In contrast to conventional international transfers, you save a lot of time and money.
       4. Since the cryptocurrency runs decentrally on the blockchain, you don't need to trust a bank or other central authority. The coin is distributed decentrally in the network.

4. Step Understanding zkdai

With ZkDai you can shield the sender, recipient and the amount of the transaction.

ZkDai implementation
Zero-knowledge protocols are used, among other things. of authentication. With some crypto currencies such as Zcash or mobile payment services such as Bluecode, they increase the anonymity of payment transactions

ZkDai has the concept of a secret note. A note is identified by a tuple, which is a combination of two elements - the note owner's public key (pk) and the note's value in Dai (v).

Output a ZkDai note
The ZkDai notes are output like UTXOs (Unspent Transaction Output). In order to transfer a certain value to a recipient, one selects some secret notes, the net value of which is at least equal to the value with which one would like to carry out the transactions. This value is sent to the recipient in the form of a new ZkDai note.

Note that this transaction hides the transaction diagram. The sender is hidden in the sense that one could use a new eth address each time to perform a transaction that the zkp sends in the chain. Maan just needs to prove knowledge of "sk" which is the secret key corresponding to the public key that the note belongs to, i.e. H. A null proof of knowledge proves possession of a note, not the transactional sender. The recipient is always hidden because this information is encoded in the hash note.
