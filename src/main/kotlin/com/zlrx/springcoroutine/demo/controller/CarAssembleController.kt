package com.zlrx.springcoroutine.demo.controller

import com.zlrx.springcoroutine.demo.model.Car
import com.zlrx.springcoroutine.demo.service.CarAssembleService
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactor.mono
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class CarAssembleController @Autowired constructor(
    private val carAssembleService: CarAssembleService
) {

    @GetMapping(path = ["/assemble"])
    fun assembleCar(): Mono<Car> {
        return mono {
            val screws = carAssembleService.buyScrews().collectList().log()
            val battery = carAssembleService.buyBattery().log()
            val engine = carAssembleService.buyEngine().log()

            val carBody = carAssembleService.produceCarBody().log()

            val carInboard = carAssembleService.assembleEngineWithBattery(
                engine.awaitFirst(),
                battery.awaitFirst(),
                screws.awaitFirst()
            ).log()

            carAssembleService.assembleCar(carBody.awaitFirst(), carInboard.awaitFirst()).log().awaitFirst()
        }.log()


    }


}