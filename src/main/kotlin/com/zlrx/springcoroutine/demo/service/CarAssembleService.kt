package com.zlrx.springcoroutine.demo.service

import com.zlrx.springcoroutine.demo.model.Battery
import com.zlrx.springcoroutine.demo.model.Car
import com.zlrx.springcoroutine.demo.model.CarBody
import com.zlrx.springcoroutine.demo.model.CarInboard
import com.zlrx.springcoroutine.demo.model.Engine
import com.zlrx.springcoroutine.demo.model.Screw
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

@Service
class CarAssembleService {

    fun buyEngine(): Mono<Engine> = Mono.just(Engine(100))
        .delayElement(Duration.ofSeconds(2))

    fun buyBattery(): Mono<Battery> = Mono.just(Battery(20))
        .delayElement(Duration.ofSeconds(1))

    fun buyScrews(): Flux<Screw> = Flux.just(Screw(10), Screw(12), Screw(8))
        .delayElements(Duration.ofMillis(150))

    fun produceCarBody() = Mono.just(CarBody(700)).delayElement(Duration.ofMillis(700))

    fun assembleEngineWithBattery(engine: Engine, battery: Battery, screws: List<Screw>) = Mono.just(CarInboard(engine, battery, screws))
        .delayElement(Duration.ofMillis(300))

    fun assembleCar(carBody: CarBody, carInboard: CarInboard) = Mono.just(Car(carBody, carInboard))
        .delayElement(Duration.ofMillis(300))

}