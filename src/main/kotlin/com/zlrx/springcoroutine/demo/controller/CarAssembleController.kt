package com.zlrx.springcoroutine.demo.controller

import com.zlrx.springcoroutine.demo.model.Car
import com.zlrx.springcoroutine.demo.model.Screw
import com.zlrx.springcoroutine.demo.service.CarAssembleService
import com.zlrx.springcoroutine.demo.service.async
import com.zlrx.springcoroutine.demo.service.dispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactor.mono
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class CarAssembleController @Autowired constructor(
    private val carAssembleService: CarAssembleService
) {

    @GetMapping(path = ["/assemble-coroutine-reactor"], produces = [MediaType.APPLICATION_STREAM_JSON_VALUE])
    fun assembleCar(): Mono<Car> {
        return mono(Dispatchers.Default + CoroutineName("Rest")) {
            val screws = async { carAssembleService.buyScrewsFlux().collectList().awaitFirst() }
            val battery = async { carAssembleService.buyBatteryMono().awaitFirst() }
            val engine = async { carAssembleService.buyEngineMono().awaitFirst() }
            val carBody = async { carAssembleService.produceCarBodyMono().awaitFirst() }

            val carInboard =
                carAssembleService.assembleEngineWithBatteryMono(
                    engine.await(),
                    battery.await(),
                    screws.await()
                ).awaitFirst()
            carAssembleService.assembleCarMono(carBody.await(), carInboard).awaitFirst()
        }.log()
    }

    @GetMapping(path = ["/assemble-coroutine-reactor-suspended"], produces = [MediaType.APPLICATION_STREAM_JSON_VALUE])
    suspend fun assembleCarSuspendReact(): Car {
        val engine = async { carAssembleService.buyEngineMono().awaitFirst() }
        val battery = async { carAssembleService.buyBatteryAsync() }
        val carBody = async { carAssembleService.produceCarBodyMono().awaitFirst() }
        val screws = async { carAssembleService.buyScrewsFlow().flowOn(dispatcher).toList() }

        val carInboard =
            carAssembleService.assembleEngineWithBatteryMono(
                engine.await(),
                battery.await(),
                screws.await()
            ).awaitFirst()

        return carAssembleService.assembleCarAsync(carBody.await(), carInboard)

    }

    @GetMapping(path = ["/assemble-coroutine-suspended"], produces = [MediaType.APPLICATION_STREAM_JSON_VALUE])
    suspend fun assembleCarCoroutine(): Car {
        val battery = async { carAssembleService.buyBatteryAsync() }
        val engine = async { carAssembleService.buyEngineAsync() }
        val carBody = async { carAssembleService.produceCarBodyAsync() }
        val screws = async { carAssembleService.buyScrewsFlow().flowOn(dispatcher).toList() }
        val carInboard = async {
            carAssembleService.assembleEngineWithBatteryAsync(
                engine.await(),
                battery.await(),
                screws.await()
            )
        }
        return carAssembleService.assembleCarAsync(carBody.await(), carInboard.await())
    }

    @GetMapping(path = ["/assemble-flow-flux-merge"], produces = [MediaType.APPLICATION_STREAM_JSON_VALUE])
    suspend fun getScrews(): Flow<Screw> {
        val screws1 = carAssembleService.buyScrewsFlow().flowOn(dispatcher)
        val screws2 = carAssembleService.buyScrewsFlux()
        return merge(screws1, screws2.asFlow())
    }
}