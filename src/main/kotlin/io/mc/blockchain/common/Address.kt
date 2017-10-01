package io.mc.blockchain.common

data class Address(

        var id: String? = null,
        /**
         * The public key for this Address to ensure everybody is able to verify signed messages
         */
        var publicKey: String? = null) {

    override fun equals(o: Any?) = this === o || o is Address && id == o.id
    override fun hashCode() = id!!.hashCode()

}
