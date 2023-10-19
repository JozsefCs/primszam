package hu.csatari.primszam.service

import hu.csatari.primszam.model.CalculateStarterResponseEnum
import hu.csatari.primszam.model.CalculateStopperResponseEnum
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class PrimszamServiceImpl {

    @Value("\${spring.task.execution.pool.core-size}")
    private val max: Long = 0

    var group: ThreadGroup = ThreadGroup("primSearch")

    fun searchPrimszam(set: MutableSet<Int>, noveles: Int): CalculateStarterResponseEnum {
        if (noveles > max) {
            return CalculateStarterResponseEnum.HIBA
        }
        if (group.activeCount() > 0)
            return CalculateStarterResponseEnum.FUT
        for (i in 1..noveles) {
            var thread = Thread(group) {
                var kezdo = i
                while (true) {
                    var prim = true
                    for (i in 2..kezdo / 2 step 1) {
                        if (kezdo % i == 0) {
                            prim = false
                            break
                        }
                    }
                    if (prim) {
                        set.add(kezdo)
                    }
                    kezdo += noveles
                }
            }
            thread.start()
        }
        return CalculateStarterResponseEnum.ELINDULT
    }

    fun getResult(set: MutableSet<Int>, min: Int, max: Int): List<Int> {
        val filter:List<Int>
        if ((max - min) > 200)
            return listOf(-1)
        else if(group.activeCount() > 0) {
            group.suspend()
            if(set.last() < max) {
                group.resume()
                return listOf(-2)
            }else{
                val filter = set.filter { (min <= it && it <= max) }
                group.resume()
                return filter
            }
        }else{
            return listOf(-3)
        }
    }

    fun stopSearch(): CalculateStopperResponseEnum {
        try {
            if (group.activeCount() > 0) {
                group.interrupt()
                return CalculateStopperResponseEnum.LEALLT
            } else {
                return CalculateStopperResponseEnum.NEM_FUTOTT
            }

        } catch (e: Exception) {
            return CalculateStopperResponseEnum.HIBA
        }


    }
}