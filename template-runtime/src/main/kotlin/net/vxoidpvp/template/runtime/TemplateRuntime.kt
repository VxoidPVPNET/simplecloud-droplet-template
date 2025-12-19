package net.vxoidpvp.template.runtime

import app.simplecloud.controller.api.ControllerApi
import app.simplecloud.droplet.api.auth.AuthCallCredentials
import app.simplecloud.droplet.api.auth.AuthSecretInterceptor
import app.simplecloud.droplet.api.droplet.Droplet
import app.simplecloud.droplet.player.api.PlayerApi
import build.buf.gen.simplecloud.controller.v1.ControllerDropletServiceGrpcKt
import io.grpc.ManagedChannelBuilder
import io.grpc.Server
import io.grpc.ServerBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import net.vxoidpvp.template.runtime.controller.Attacher
import net.vxoidpvp.template.runtime.launcher.TemplateStartCommand
import net.vxoidpvp.template.shared.Contributors
import org.apache.logging.log4j.LogManager

class TemplateRuntime(
    private val args: TemplateStartCommand
) {
    private val logger = LogManager.getLogger(TemplateRuntime::class.java)

    private val controllerApi = ControllerApi.createCoroutineApi(args.authSecret)
    private val playerApi = PlayerApi.createCoroutineApi(args.authSecret)

    private val server = createGrpcServer()
    private val callCredentials = AuthCallCredentials(args.authSecret)
    private val channel = ManagedChannelBuilder.forAddress(args.controllerGrpcHost, args.controllerGrpcPort).usePlaintext().build()
    private val controllerStub = ControllerDropletServiceGrpcKt.ControllerDropletServiceCoroutineStub(channel).withCallCredentials(callCredentials)
    private val attacher = Attacher(Droplet("template", "internal-template", args.grpcHost, args.grpcPort, 8081), channel, controllerStub)


    suspend fun start() {
        logger.info("Starting template droplet by $Contributors")
        logger.info("Attaching to Controller...")
        attacher.enforceAttachBlocking()
        attacher.enforceAttach()
        startGrpcServer()
        suspendCancellableCoroutine<Unit> { continuation ->
            Runtime.getRuntime().addShutdownHook(Thread {
                server.shutdown()
                continuation.resume(Unit) { cause, _, _ ->
                    logger.info("Server shutdown due to: $cause")
                }
            })
        }
    }

    private fun startGrpcServer() {
        logger.info("Starting gRPC server...")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                server.start()
                server.awaitTermination()
            } catch (e: Exception) {
                logger.error("Error in gRPC server", e)
                throw e
            }
        }
    }

    private fun createGrpcServer(): Server {
        return ServerBuilder.forPort(args.grpcPort)
            .intercept(AuthSecretInterceptor(args.controllerGrpcHost, args.authorizationPort))
            .build()
    }
}