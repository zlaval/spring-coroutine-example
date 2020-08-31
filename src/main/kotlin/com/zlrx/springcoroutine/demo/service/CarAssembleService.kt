package com.zlrx.springcoroutine.demo.service

import com.zlrx.springcoroutine.demo.model.Battery
import com.zlrx.springcoroutine.demo.model.Car
import com.zlrx.springcoroutine.demo.model.CarBody
import com.zlrx.springcoroutine.demo.model.CarInboard
import com.zlrx.springcoroutine.demo.model.Engine
import com.zlrx.springcoroutine.demo.model.Screw
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.reactive.awaitFirst
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

@Service
class CarAssembleService {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val dispatcher = newFixedThreadPoolContext(3, "CarAssembleServiceDispatcher") + CoroutineName("CAR ASSEMBLE")

    suspend fun buyEngineAsync() = GlobalScope.async(dispatcher) {
        Mono.just(Engine(100))
            .delayElement(Duration.ofSeconds(2))
            .doOnSubscribe { logger.info("buyEngineAsync - subscribed") }
            .doOnNext { logger.info("buyEngineAsync - $it") }
            .doOnEach {
                if (it.isOnComplete) {
                    logger.info("buyEngineAsync - completed")
                }
            }
            .awaitFirst()
    }

    suspend fun buyBatteryAsync() = GlobalScope.async(dispatcher) {
        Mono.just(Battery(20))
            .delayElement(Duration.ofSeconds(1))
            .doOnSubscribe { logger.info("buyBatteryAsync - subscribed") }
            .doOnNext { logger.info("buyBatteryAsync - $it") }
            .doOnEach {
                if (it.isOnComplete) {
                    logger.info("buyBatteryAsync - completed")
                }
            }
            .awaitFirst()
    }

    suspend fun buyScrewsAsync() = GlobalScope.async(dispatcher) {
        Flux.just(Screw(10), Screw(12), Screw(8))
            .delayElements(Duration.ofMillis(500))
            .doOnSubscribe { logger.info("buyScrewsAsync - subscribed") }
            .doOnNext { logger.info("buyScrewsAsync - $it") }
            .doOnEach {
                if (it.isOnComplete) {
                    logger.info("buyScrewsAsync - completed")
                }
            }
            .collectList()
            .awaitFirst()
    }

    suspend fun produceCarBodyAsync() = GlobalScope.async(dispatcher) {
        Mono.just(CarBody(700))
            .delayElement(Duration.ofMillis(1500))
            .doOnSubscribe { logger.info("produceCarBodyAsync - subscribed") }
            .doOnNext { logger.info("produceCarBodyAsync - $it") }
            .doOnEach {
                if (it.isOnComplete) {
                    logger.info("produceCarBodyAsync - completed")
                }
            }
            .awaitFirst()
    }

    suspend fun assembleEngineWithBatteryAsync(engine: Engine, battery: Battery, screws: List<Screw>) = GlobalScope.async(dispatcher) {
        Mono.just(CarInboard(engine, battery, screws))
            .delayElement(Duration.ofMillis(500))
            .doOnSubscribe { logger.info("assembleEngineWithBatteryAsync - subscribed") }
            .doOnNext { logger.info("assembleEngineWithBatteryAsync - $it") }
            .doOnEach {
                if (it.isOnComplete) {
                    logger.info("assembleEngineWithBatteryAsync - completed")
                }
            }
            .awaitFirst()
    }

    suspend fun assembleCar(carBody: CarBody, carInboard: CarInboard) =
        Mono.just(Car(carBody, carInboard))
            .delayElement(Duration.ofMillis(500))
            .doOnSubscribe { logger.info("assembleCar - subscribed") }
            .doOnNext { logger.info("assembleCar - $it") }
            .doOnEach {
                if (it.isOnComplete) {
                    logger.info("assembleCar - completed")
                }
            }


}