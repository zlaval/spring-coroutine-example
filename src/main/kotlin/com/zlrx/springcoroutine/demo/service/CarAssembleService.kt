package com.zlrx.springcoroutine.demo.service

import com.zlrx.springcoroutine.demo.model.Battery
import com.zlrx.springcoroutine.demo.model.Car
import com.zlrx.springcoroutine.demo.model.CarBody
import com.zlrx.springcoroutine.demo.model.CarInboard
import com.zlrx.springcoroutine.demo.model.Engine
import com.zlrx.springcoroutine.demo.model.Screw
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

@Service
class CarAssembleService {

    private val logger = LoggerFactory.getLogger(javaClass)


    fun buyEngineMono() =
        Mono.just(Engine(100))
            .delayElement(Duration.ofSeconds(2))
            .doOnSubscribe { logger.info("buyEngineAsync - subscribed") }
            .doOnNext { logger.info("buyEngineAsync - $it") }
            .doOnEach {
                if (it.isOnComplete) {
                    logger.info("buyEngineAsync - completed")
                }
            }


    fun buyBatteryMono() =
        Mono.just(Battery(20))
            .delayElement(Duration.ofSeconds(1))
            .doOnSubscribe { logger.info("buyBatteryAsync - subscribed") }
            .doOnNext { logger.info("buyBatteryAsync - $it") }
            .doOnEach {
                if (it.isOnComplete) {
                    logger.info("buyBatteryAsync - completed")
                }
            }


    fun buyScrewsFlux() =
        Flux.just(Screw(10), Screw(12), Screw(8))
            .delayElements(Duration.ofMillis(500))
            .doOnSubscribe { logger.info("buyScrewsAsync - subscribed") }
            .doOnNext { logger.info("buyScrewsAsync - $it") }
            .doOnEach {
                if (it.isOnComplete) {
                    logger.info("buyScrewsAsync - completed")
                }
            }

    fun produceCarBodyMono() =
        Mono.just(CarBody(700))
            .delayElement(Duration.ofMillis(4000))
            .doOnSubscribe { logger.info("produceCarBodyAsync - subscribed") }
            .doOnNext { logger.info("produceCarBodyAsync - $it") }
            .doOnEach {
                if (it.isOnComplete) {
                    logger.info("produceCarBodyAsync - completed")
                }
            }


    fun assembleEngineWithBatteryMono(engine: Engine, battery: Battery, screws: List<Screw>) =
        Mono.just(CarInboard(engine, battery, screws))
            .delayElement(Duration.ofMillis(2500))
            .doOnSubscribe { logger.info("assembleEngineWithBatteryAsync - subscribed") }
            .doOnNext { logger.info("assembleEngineWithBatteryAsync - $it") }
            .doOnEach {
                if (it.isOnComplete) {
                    logger.info("assembleEngineWithBatteryAsync - completed")
                }
            }

    fun assembleCarMono(carBody: CarBody, carInboard: CarInboard) =
        Mono.just(Car(carBody, carInboard))
            .delayElement(Duration.ofMillis(500))
            .doOnSubscribe { logger.info("assembleCar - subscribed") }
            .doOnNext { logger.info("assembleCar - $it") }
            .doOnEach {
                if (it.isOnComplete) {
                    logger.info("assembleCar - completed")
                }
            }


    suspend fun assembleCarAsync(carBody: CarBody, carInboard: CarInboard): Car {
        delay(500)
        logger.info("Emmit Car")
        return Car(carBody, carInboard)
    }

    suspend fun buyScrewsFlow() = flow {
        delay(500)
        logger.info("emmit screw 1")
        emit(Screw(10))

        delay(500)
        logger.info("emmit screw 2")
        emit(Screw(12))

        delay(500)
        logger.info("emmit screw 3")
        emit(Screw(8))
    }

    suspend fun buyEngineAsync(): Engine {
        delay(2000)
        logger.info("Emmit engine")
        return Engine(100)
    }

    suspend fun buyBatteryAsync(): Battery {
        delay(1000)
        logger.info("Emmit battery")
        return Battery(20)
    }

    suspend fun produceCarBodyAsync(): CarBody {
        delay(4000)
        logger.info("Emmit carbody")
        return CarBody(700)
    }

    suspend fun assembleEngineWithBatteryAsync(engine: Engine, battery: Battery, screws: List<Screw>): CarInboard {
        delay(2500)
        logger.info("Emmit CarInboard")
        return CarInboard(engine, battery, screws)
    }

}