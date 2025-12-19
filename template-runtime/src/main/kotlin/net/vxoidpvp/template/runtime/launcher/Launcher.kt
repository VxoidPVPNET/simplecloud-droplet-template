package net.vxoidpvp.template.runtime.launcher

import com.github.ajalt.clikt.command.main
import org.apache.logging.log4j.LogManager

suspend fun main(args: Array<String>) {
    configureLog4j()
    TemplateStartCommand().main(args)
}

fun configureLog4j() {
    val globalExceptionHandlerLogger = LogManager.getLogger("GlobalExceptionHandler")
    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
        globalExceptionHandlerLogger.error("Uncaught exception in thread ${thread.name}", throwable)
    }
}