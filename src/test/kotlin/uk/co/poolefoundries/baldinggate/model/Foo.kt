package uk.co.poolefoundries.baldinggate.model

import kotlin.test.assertEquals

interface Reader {
    fun read() : String
}

interface Writer {
    fun write(string: String)
}

class File : Writer,Reader {
    override fun read(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun write(string: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class Console : Writer,Reader {
    override fun read(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun write(string: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class StringWriter : Writer {

    var s : String = ""
    override fun write(string: String) {
        s+=string
    }
}

class Service(private val logger: Writer) {
    fun handleRequest() {
        logger.write("got request")
    }
}

fun TestService() {
    val sw = StringWriter()
    val service = Service(sw)

    service.handleRequest()

    assertEquals("got request", sw.s)
}