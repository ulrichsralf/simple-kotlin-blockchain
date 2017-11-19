//package io.mc.blockchain.node.server
//
//import io.mc.blockchain.node.server.persistence.ZipRepository
//import junit.framework.Assert.assertEquals
//import org.junit.Test
//import java.util.*
//
//class ZipRepositoryTest{
//
//    @Test
//    fun testDelete(){
//
//        val zr = ZipRepository()
//        val key = UUID.randomUUID().toString()
//        zr.save(key, "t1")
//        zr.save(key, "t2")
//        zr.save(key, "t3")
//        zr.save(key, "t4")
//        val result =  zr.list(key, String::class.java).size
//        zr.close()
//        assertEquals(4,result)
//
//    }
//
//
//
//
//}