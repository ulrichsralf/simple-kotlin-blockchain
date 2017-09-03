package io.mc.blockchain.node.server.utils


import io.mc.blockchain.node.server.persistence.Transaction
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.Signature
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
     * Verify if the given signature is valid .
     */
    fun verify(transaction: Transaction, publicKey: ByteArray): Boolean {
        // construct a public key from raw bytes
        val keySpec = X509EncodedKeySpec(publicKey)
        val publicKeyObj = keyFactory.generatePublic(keySpec)

        // do the verification
        val sig = signatureObj
        sig.initVerify(publicKeyObj)
        sig.update(transaction.text!!.bytesFromHex())
        return sig.verify(transaction.signature?.bytesFromHex())
    }


    private val signatureObj = Signature.getInstance("SHA1withDSA", "SUN")

}
