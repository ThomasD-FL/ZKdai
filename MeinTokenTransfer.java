package de.meinefirma.meinprojekt;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;
import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class MeinTokenTransfer
{
   // Hier die Adresse des MeinToken-Smart-Contracts in der Blockchain
   // und entweder die Passphrase oder den Private Key eintragen:
   public static final String DEFAULT_CONTRACT_ADRESSE      = "0xf07a9248ac043cf039a6f76113b78f89d483b2c8";
   public static final String DEFAULT_PASSPHRASE_OR_PRIVKEY = "Meine Ethereum-Test-Passphrase";
   public static final String DEFAULT_ETHEREUM_URL          = "http://localhost:8545";
   public static final List<String> DEFAULT_KEYSTORE_DIRS   = Arrays.asList(
         "../EthereumDemo/Private-Blockchain/keystore",
         "../EthereumDemo/Rinkeby-Blockchain/keystore" );

   public static void main( String[] args ) throws Exception
   {
      if( args.length < 1 ) {
         System.out.println( "\nFolgende Parameter koennen uebergeben werden:\n" +
               "   Anzahl zu transferierender Tokens\n" +
               "   Passphrase oder Private Key\n" +
               "   Sender-Account-Adresse\n" +
               "   Empfaenger-Account-Adresse\n" +
               "   MeinToken-Contract-Adresse\n" +
               "   Keystore-Verzeichnis\n" +
               "   Ethereum-Blockchain-URL\n" );
      }
      String betragStr    = ( args.length > 0 ) ? args[0] : "0";
      String pssPhrOrPrvK = ( args.length > 1 ) ? args[1] : DEFAULT_PASSPHRASE_OR_PRIVKEY;
      String contractAdr  = ( args.length > 4 ) ? args[4] : DEFAULT_CONTRACT_ADRESSE;
      String keystoreDir  = ( args.length > 5 ) ? args[5] : "";
      String ethereumUrl  = ( args.length > 6 ) ? args[6] : DEFAULT_ETHEREUM_URL;

      List<String> accounts = ermittleAccounts( ethereumUrl );
      String senderAdresse     = ( args.length > 2 ) ? args[2] : accounts.get( 0 );
      String empfaengerAdresse = ( args.length > 3 ) ? args[3] : accounts.get( 1 );
      System.out.println( "Accounts:     " + accounts );

      List<String> keystoreDirs = new ArrayList<>();
      keystoreDirs.add( keystoreDir );
      keystoreDirs.addAll( DEFAULT_KEYSTORE_DIRS );

      MeinToken meinToken = null;
      List<BigInteger> kontostaende = null;
      try {
         meinToken = initMeinToken( ethereumUrl, contractAdr, senderAdresse, pssPhrOrPrvK, keystoreDirs );
         kontostaende = liesKontostaende( meinToken, senderAdresse, empfaengerAdresse );
         System.out.println( "Kontostaende: " + kontostaende.get( 0 ) + " / " + kontostaende.get( 1 ) );
      } catch( Exception ex ) {
         System.out.println( "Fehler bei der Abfrage des MeinToken Smart Contracts. " +
                             "Sind Contract-Adresse sowie Passphrase bzw. Private Key korrekt?" );
         ex.printStackTrace();
         return;
      }

      long betrag = Long.parseLong( betragStr );
      if( betrag > 0 ) {
         System.out.println( betrag + " Tokens werden transferiert, ca. 30 Sekunden auf das Mining warten ..." );
         kontostaende = transferiereTokens( meinToken, senderAdresse, empfaengerAdresse, betrag, 180 );
         System.out.println( "Kontostaende: " + kontostaende.get( 0 ) + " / " + kontostaende.get( 1 ) );
      }
   }

   public static List<String> ermittleAccounts( String ethereumUrl ) throws IOException
   {
      Web3j web3j = Web3j.build( new HttpService( ethereumUrl ) );
      return web3j.ethAccounts().send().getAccounts();
   }

   public static MeinToken initMeinToken( String ethereumUrl, String meinTokenContractAdresse,
                                          String account, String passphraseOrPrivateKey, List<String> keystoreDirs )
   {
      Web3j web3j = Web3j.build( new HttpService( ethereumUrl ) );
      Credentials credentials = null;
      try {
         credentials = getCredentialsFromKeystore( account, passphraseOrPrivateKey, keystoreDirs );
      } catch( Exception ex1 ) {
         try {
            credentials = Credentials.create( passphraseOrPrivateKey );
         } catch( Exception ex2 ) {
            throw new RuntimeException( ex1 );
         }
      }
      return MeinToken.load(
            meinTokenContractAdresse, web3j, credentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT );
   }

   public static List<BigInteger> liesKontostaende(
         MeinToken meinToken, String senderAdresse, String empfaengerAdresse ) throws Exception
   {
      List<BigInteger> kontostaende = new ArrayList<>();
      kontostaende.add( meinToken.balanceOf( senderAdresse     ).send() );
      kontostaende.add( meinToken.balanceOf( empfaengerAdresse ).send() );
      return kontostaende;
   }

   public static List<BigInteger> transferiereTokens(
         MeinToken meinToken, String senderAdresse, String empfaengerAdresse, long betrag, int timeoutSek )
         throws Exception
   {
      if( betrag > 0 ) {
         RemoteCall<TransactionReceipt> rc = meinToken.transfer( empfaengerAdresse, BigInteger.valueOf( betrag ) );
         CompletableFuture<TransactionReceipt> txFuture = rc.sendAsync();
         for( int i = 0; i < timeoutSek; i++ ) {
            if( txFuture.isDone() ) { break; }
            System.out.print( "." );
            Thread.sleep( 1000 );
         }
         if( timeoutSek > 0 ) { System.out.println( "" ); }
         if( txFuture.isCompletedExceptionally() || txFuture.isCancelled() ) {
            throw new Exception( "MeinToken-Transfer fehlgeschlagen: " + txFuture.get().getStatus() );
         }
         if( !txFuture.isDone() && timeoutSek > 0 ) {
            throw new Exception( "Der MeinToken-Transfer konnte nicht innerhalb von " + timeoutSek +
                                 " Sekunden abgeschlossen werden. Ist Mining aktiv?" );
         }
         if( txFuture.isDone() ) {
            TransactionReceipt txReceipt = txFuture.get();
            System.out.println( "MeinToken-Transfer erfolgreich, GasUsed: " + txReceipt.getGasUsed() +
                  ", BlockNumber: " + txReceipt.getBlockNumber() + ", TxHash: " + txReceipt.getTransactionHash() );
         }
      }
      return liesKontostaende( meinToken, senderAdresse, empfaengerAdresse );
   }

   private static Credentials getCredentialsFromKeystore( String account, String passphrase, List<String> keystoreDirs )
         throws IOException, CipherException
   {
      File[] ksDateien = null;
      for( String ksDir : keystoreDirs ) {
         ksDateien = new File( ksDir ).listFiles( f -> f.isFile() && f.getName().contains( account.substring( 2 ) ) );
         if( ksDateien != null && ksDateien.length != 0 ) {
            break;
         }
      }
      if( ksDateien == null || ksDateien.length == 0 ) {
         throw new IOException(
               "Fehler: Keine zum Account " + account + " passende Keystore-Datei in den Keystore-Verzeichnissen " +
                     keystoreDirs + " gefunden." );
      }
      return WalletUtils.loadCredentials( passphrase, ksDateien[0] );
   }
}
