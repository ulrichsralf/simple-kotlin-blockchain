package io.mc.blockchain.node.server.utils


import io.mc.blockchain.node.server.persistence.Transaction
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

object SignatureUtils {

    private val LOG = getLogger()

    /**
     * The keyFactory defines which algorithms are used to generate the private/public keys.
     */
    private val keyFactory = try {
        KeyFactory.getInstance("DSA", "SUN")
    } catch (e: NoSuchAlgorithmException) {
        LOG.error("Failed initializing keyFactory", e)
        throw e
    } catch (e: NoSuchProviderException) {
        LOG.error("Failed initializing keyFactory", e)
        throw e
    }


    /**
     * Generate a random key pair.
     * @return KeyPair containg private and public key
     */
    fun generateKeyPair(): KeyPair {
        val keyGen = KeyPairGenerator.getInstance("DSA", "SUN")
        val random = SecureRandom.getInstance("SHA1PRNG", "SUN")
        keyGen.initialize(1024, random)
        return keyGen.generateKeyPair()
    }

    /**
     * Verify if the given signature is valid .
     */
    fun verify(transaction: Transaction, publicKey: ByteArray): Boolean {
        // construct a public key from raw bytes
        val keySpec = X509EncodedKeySpec(publicKey)
        val publicKeyObj = keyFactory.generatePublic(keySpec)

        // do the verification
        val sig = signatureObj
        sig.initVerify(publicKeyObj)
        sig.update(transaction.getSignData())
        return sig.verify(transaction.signature?.bytesFromHex())
    }

    /**
     * Sign given data with a private key
     */
    fun sign(signData: ByteArray, privateKey: ByteArray): ByteArray {
        // construct a PrivateKey-object from raw bytes
        val keySpec = PKCS8EncodedKeySpec(privateKey)
        val privateKeyObj = keyFactory.generatePrivate(keySpec)

        // do the signage
        val sig = signatureObj
        sig.initSign(privateKeyObj)
        sig.update(signData)
        return sig.sign()
    }

    private val signatureObj = Signature.getInstance("SHA1withDSA", "SUN")

}
