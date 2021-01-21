pragma solidity >=0.4.20;

/*
    Cryptocurrency "FlensCoin".
*/

contract FlensCoin {
    address public creator;
    mapping (address => uint) public balances;
    
    // Event that notifies when a transaction is completed
    event Delivered(address from, address to, uint amount);
    
    // Constructor
    constructor() {
        creator = msg.sender;
    }
    
    // Function to give anybody FlensCoin-Token (only possible if you are the creator)
    function create (address receiver, uint amount) public {
        require (msg.sender == creator);
        balances[receiver] += amount; 
    }
    
    // Function to transfer FlensCoin to another address
    function transferTo(address receiver, uint amount) public {
        require (balances[msg.sender] > amount);
        balances[msg.sender] -= amount;
        balances[receiver] += amount;
        emit Delivered(msg.sender, receiver, amount);
    }
}
