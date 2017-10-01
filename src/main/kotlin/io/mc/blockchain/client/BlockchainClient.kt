package io.mc.blockchain.client


import feign.Feign
import feign.Headers
import feign.RequestLine
import feign.gson.GsonEncoder
import io.mc.blockchain.common.*
import io.mc.blockchain.node.server.persistence.sha256Hash
import io.mc.blockchain.node.server.utils.SignatureUtils
import io.mc.blockchain.node.server.utils.getLogger
import io.mc.blockchain.node.server.utils.toHexString
import org.apache.commons.cli.*
import org.apache.commons.codec.digest.DigestUtils
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
//
//fun main(args: Array<String>) {
//    val client = BlockchainClient()
//    val parser = DefaultParser()
//    val options = client.options
//    try {
//        val line = parser.parse(options, args)
//        client.executeCommand(line)
//    } catch (e: ParseException) {
//        System.err.println(e.message)
//        val formatter = HelpFormatter()
//        formatter.printHelp("BlockchainClient", options, true)
//    }
//}


class BlockchainClient( serverNode: String = "http://localhost:8080") {


    private val restClient = Feign.builder().encoder(GsonEncoder()).target(Blockchain::class.java, serverNode)
    private val LOG = getLogger()

    fun executeCommand(line: CommandLine) {
        if (line.hasOption("keypair")) {
            generateKeyPair()
        } else if (line.hasOption("address")) {
            val publickey = line.getOptionValue("publickey")
            if (publickey == null) {
                throw ParseException("publickey is required")
            }
            publishAddress(generateAddress(Paths.get(publickey)))

        } else if (line.hasOption("transaction")) {
            val message = line.getOptionValue("message")
            val sender = line.getOptionValue("sender")
            val privatekey = line.getOptionValue("privatekey")
            if (message == null || sender == null || privatekey == null) {
                throw ParseException("message, sender and privatekey is required")
            }
            publishTransaction(generateTransaction( Paths.get(privatekey), message, sender))
        }
    }

    val options: Options
        get() {
            val actions = OptionGroup()
            actions.addOption(Option("k", "keypair", false, "generate private/public key pair"))
            actions.addOption(Option("a", "address", false, "publish new address"))
            actions.addOption(Option("t", "transaction", false, "publish new transaction"))
            actions.isRequired = true

            val options = Options()
            options.addOptionGroup(actions)
            options.addOption(Option.builder("p")
                    .longOpt("publickey")
                    .hasArg()
                    .argName("path to key file")
                    .desc("needed for address publishing")
                    .build())
            options.addOption(Option.builder("v")
                    .longOpt("privatekey")
                    .hasArg()
                    .argName("path to key file")
                    .desc("needed for transaction publishing")
                    .build())
            options.addOption(Option.builder("m")
                    .longOpt("message")
                    .hasArg()
                    .argName("message to post")
                    .desc("needed for transaction publishing")
                    .build())
            options.addOption(Option.builder("s")
                    .longOpt("sender")
                    .hasArg()
                    .argName("address hash (Base16)")
                    .desc("needed for transaction publishing")
                    .build())

            return options
        }

    fun generateKeyPair() {
        val keyPair = SignatureUtils.generateKeyPair()
        Files.write(Paths.get("key.priv"), keyPair.getPrivate().getEncoded())
        Files.write(Paths.get("key.pub"), keyPair.getPublic().getEncoded())
    }

    fun generateAddress(publicKey: Path): Address {
        val key = Files.readAllBytes(publicKey)
        val hash = DigestUtils.sha256Hex(key)
        return Address(hash, key.toHexString())
    }

    fun generateTransaction(privateKey: Path, text: String, senderId: String): Transaction {
        val out1 = TxOutputData(100,"VPF_Dollar", senderId,1)
        val outHash = out1.sha256Hash()

        val payload = TransactionData("hello blockchain",senderId, listOf(), listOf(TxOutput(outHash,out1)),System.currentTimeMillis())
        val id = DigestUtils.sha256(payload.getSignedBytes())
        val signature = SignatureUtils.sign(id, Files.readAllBytes(privateKey))
        return Transaction(id,signature, payload)
    }

    fun publishAddress(address: Address) {
        restClient.addAddress(address)
        LOG.info("Added $address")
    }

    fun publishTransaction(transaction: Transaction) {
        restClient.addTransaction(transaction)
        LOG.info("Added $transaction")
    }
}


interface Blockchain {

    @RequestLine("PUT /address")
    @Headers("Content-Type: application/json")
    fun addAddress(address: Address)

    @RequestLine("PUT /transaction")
    @Headers("Content-Type: application/json")
    fun addTransaction(transaction: Transaction)
}