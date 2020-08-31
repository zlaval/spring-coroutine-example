package com.zlrx.springcoroutine.demo.controller

import com.zlrx.springcoroutine.demo.model.Car
import com.zlrx.springcoroutine.demo.service.CarAssembleService
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class CarAssembleController @Autowired constructor(
    private val carAssembleService: CarAssembleService
) {

    private val logger = LoggerFactory.getLogger(javaClass)


    @GetMapping(path = ["/assemble"], produces = [MediaType.APPLICATION_STREAM_JSON_VALUE])
    fun assembleCar(): Mono<Car> {
        return mono(Dispatchers.Default + CoroutineName("Rest")) {
            logger.info("collect items")
            val screws = carAssembleService.buyScrewsAsync()
            val battery = carAssembleService.buyBatteryAsync()
            val engine = carAssembleService.buyEngineAsync()
            val carBody = carAssembleService.produceCarBodyAsync()

            logger.info("assemble car inboard")
            val carInboard = carAssembleService.assembleEngineWithBatteryAsync(
                engine.await(),
                battery.await(),
                screws.await()
            )
            logger.info("assemble car")
            carAssembleService.assembleCar(carBody.await(), carInboard.await()).awaitFirst()
        }.log()


    }


}