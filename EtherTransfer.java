
package de.meinefirma.meinprojekt;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;
import org.web3j.utils.Convert;
import java.math.BigInteger;
import java.util.Optional;

public class EtherTransfer
{
   public static void main( String[] args ) throws Exception
   {
      // Programm so erweitern, dass Parameter ausgelesen oder uebergeben werden:
      transferiereEther( "http://localhost:8545",
            // Hier die Adressen der beiden Accounts eintragen (Ergebnis von: web3.eth.accounts):
            "0x4597a26af9991b297b5ccc2a8c0966e9a1a17035",
            "0x63d7d5b64dc9cc0744dedf87971f8b0777d7e226",
            // Hier den zu transferierenden Betrag und die Passphrase eintragen:
            "10", "Meine Ethereum-Test-Passphrase" );
   }

   public static void transferiereEther(
         String ethereumUrl, String senderAdresse, String empfaengerAdresse, String betragEther, String passphrase )
         throws Exception
   {
      Admin admin = Admin.build( new HttpService( ethereumUrl ) );
      PersonalUnlockAccount personalUnlockAccount = admin.personalUnlockAccount( senderAdresse, passphrase ).send();
      if( !personalUnlockAccount.accountUnlocked() ) {
         System.out.println( "\nFehler: Account-Unlock fehlgeschlagen." );
         return;
      }
      Web3j web3j = Web3j.build( new HttpService( ethereumUrl ) );
      EthGetTransactionCount txCount = web3j.ethGetTransactionCount(
            senderAdresse, DefaultBlockParameterName.LATEST ).sendAsync().get();
      BigInteger nonce = txCount.getTransactionCount();
      BigInteger betrag = Convert.toWei( betragEther, Convert.Unit.ETHER ).toBigInteger();
      Transaction transaction = Transaction.createEtherTransaction(
            senderAdresse, nonce, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT, empfaengerAdresse, betrag );
      EthSendTransaction response = web3j.ethSendTransaction( transaction ).sendAsync().get();
      if( response.hasError() ) {
         System.out.println( "\nFehler: " + response.getError().getMessage() );
         return;
      }
      String txHash = response.getTransactionHash();
      System.out.print( "\nTxHash: " + txHash + "\nWarten auf das Mining " );
      for( int i = 0; i < 600; i++ ) {
         Optional<TransactionReceipt> transactionReceipt =
               web3j.ethGetTransactionReceipt( txHash ).send().getTransactionReceipt();
         if( transactionReceipt.isPresent() ) {
            System.out.println( "\nTransfer von " + betragEther + " Ether abgeschlossen, benutzte Gas-Menge: " +
                                transactionReceipt.get().getGasUsed() );
            return;
         }
         System.out.print( "." );
         Thread.sleep( 1000 );
      }
      System.out.println( "\nDer Transfer konnte innerhalb von 10 Minuten nicht abgeschlossen werden. Ist Mining aktiv?" );
   }
}
