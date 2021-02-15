package com.vkaja.dagger.application

import androidx.lifecycle.ViewModel
import dagger.*
import dagger.multibindings.IntoSet
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class MainViewModel @Inject constructor(private val client: Lazy<Client>): ViewModel() {
    fun data(): String = client.get().fetchData()
}

class Client @Inject constructor(private val connection : Provider<Connection>,
                                 private val dao : DAO,
                                 private val processor: Processor) {
    fun fetchData() : String = connection.get().doReq()
}

data class Decorator @Inject constructor(val displayName: String, val fn: (String) -> String)

class Processor @Inject constructor(private val decorators: Set<Decorator>) {

    fun processor(string: String) {
        fun processor(string: String) = decorators.map { it -> it.fn(string) }
    }
}

@Module
object ProcessorModule {
    @JvmStatic
    @Provides
    @IntoSet
    fun providesDecorator() = Decorator("Sparkle", {s -> "*$s*"})
}

class DAO private constructor() {
    companion object {
        fun get123() = DAO()
    }
}

interface Connection {
    fun doReq() : String
}

class NetworkConnection @Inject constructor(@Named("endPoint") private val endPoint: String) : Connection {

    override fun doReq() = endPoint
}

@Module
interface ConnectionModule {
    @Binds
    fun bindConnection(connection: NetworkConnection) : Connection
}

@Module
object DAOModule {
    @Provides
    @JvmStatic
    fun dao() = DAO.get123()
}

@Module
object EndpointModule {
    @JvmStatic
    @Provides
    @Named("endPoint")
    fun provideEndpoint() = when (BuildConfig.DEBUG) {
            true -> "Hurray :)"
            else -> "Blah X-("
        }
}

@Component(modules = [ConnectionModule::class, DAOModule::class, ProcessorModule::class, EndpointModule::class])
interface MainViewModelFactory {
    fun getMainViewModel() : MainViewModel
    fun inject(activity: MainActivity)
}