package io.mc.blockchain.node.server.config

import com.datastax.driver.core.Session
import io.mc.blockchain.node.server.persistence.Block
import org.springframework.cassandra.config.DataCenterReplication
import org.springframework.cassandra.core.keyspace.CreateKeyspaceSpecification
import org.springframework.cassandra.core.keyspace.KeyspaceOption
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.cassandra.config.SchemaAction
import org.springframework.data.cassandra.config.java.AbstractCassandraConfiguration
import org.springframework.data.cassandra.core.CassandraTemplate
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories
import java.util.*


/**
 * Created by ralfulrich on 23.02.17.
 */
@Configuration
@EnableCassandraRepositories
class CassandraConfig : AbstractCassandraConfiguration() {

    public override fun getContactPoints(): String {
        return "localhost"
    }

    override fun getKeyspaceName(): String {
        return "blocks"
    }

    override fun getKeyspaceCreations(): List<CreateKeyspaceSpecification> {

        val specification = CreateKeyspaceSpecification.createKeyspace("blocks")
                .ifNotExists()
                .with(KeyspaceOption.DURABLE_WRITES, true)
                .withNetworkReplication(DataCenterReplication("datacenter1", 1))
        return Arrays.asList(specification)
    }

//    override fun getKeyspaceDrops(): List<DropKeyspaceSpecification> {
//        return Arrays.asList(DropKeyspaceSpecification.dropKeyspace("blocks"))
//    }

    @Bean
    fun cassandraTemplate(session: Session): CassandraTemplate {
        return CassandraTemplate(session)
    }

    override fun getEntityBasePackages(): Array<String> {
        return arrayOf(Block::class.java.`package`.name)
    }

    override fun getSchemaAction(): SchemaAction {
        return SchemaAction.RECREATE
    }
}
