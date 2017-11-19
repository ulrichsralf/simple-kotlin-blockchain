//package io.mc.blockchain.node.server.persistence
//
//import io.mc.blockchain.common.parseJson
//import io.mc.blockchain.common.toJsonString
//import org.springframework.stereotype.Component
//import java.io.BufferedReader
//import java.io.BufferedWriter
//import java.io.Closeable
//import java.net.URI
//import java.nio.file.FileSystems
//import java.nio.file.Files
//import java.nio.file.StandardOpenOption
//import java.util.concurrent.Executors
//
//@Component
//class ZipRepository(val zipFile: String = System.getProperty("user.home") + "/blockchain.data") : Closeable {
//
//    val uri = URI.create("jar:file:$zipFile")
//    private val zipfs = FileSystems.newFileSystem(uri, mutableMapOf("create" to "true"))
//    private val es = Executors.newSingleThreadExecutor()
//    private val writerMap = hashMapOf<String, BufferedWriter>()
//    private val readerMap = hashMapOf<String, BufferedReader>()
//
//
//    private fun String.getWriter(): BufferedWriter {
//        return writerMap.getOrPut(this, {
//            val path = zipfs.getPath(this)
//            if (!Files.exists(path)) Files.createFile(path)
//            Files.newBufferedWriter(path,
//                    StandardOpenOption.APPEND,
//                    StandardOpenOption.CREATE,
//                    StandardOpenOption.SYNC)
//        })
//    }
//
//    private fun String.getReader(): BufferedReader {
//        return readerMap.getOrPut(this, {
//            Files.newBufferedReader(zipfs.getPath(this))
//        })
//    }
//
//
//    fun save(key: String, element: Any) {
//        es.execute {
//            val string = element.toJsonString()
//            "$key.json".getWriter().apply {
//                append(string)
//                newLine()
//                flush()
//            }
//        }
//    }
//
//    fun <T> remove(key: String, clazz: Class<T>, matcher: (T) -> Boolean) {
//        es.execute {
//            val filename = "$key.json"
//            val result = filename.getReader()
//                    .lineSequence()
//                    .map { it.parseJson(clazz) }
//                    .filterNot(matcher)
//                    .toList()
//            val path = zipfs.getPath(filename)
//            writerMap.remove(key)?.close()
//            Files.delete(path)
//            val writer = "$key.json".getWriter()
//            result.forEach {
//                writer.write(it?.toJsonString())
//                writer.newLine()
//            }
//            writer.flush()
//        }
//    }
//
//    fun <T> list(key: String, clazz: Class<T>): List<T> {
//        return es.submit<List<T>> {
//            "$key.json".getReader().lineSequence().map { it.parseJson(clazz) }.toList()
//        }.get() as List<T>
//    }
//
//    override fun close() {
//        readerMap.values.forEach { it.close() }
//        writerMap.values.forEach { it.close() }
//        es.shutdown()
//        zipfs.close()
//    }
//}