package hu.csatari.primszam.controller

import hu.csatari.primszam.service.PrimszamServiceImpl
import hu.csatari.primszam.model.CalculateStarterResponseEnum
import hu.csatari.primszam.model.CalculateStopperResponseEnum
import io.swagger.v3.oas.annotations.Operation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.SortedSet
import javax.validation.constraints.Min


@RestController
class PrimszamController {

    @Autowired
    lateinit var primszamComponent: PrimszamServiceImpl

    var set: SortedSet<Int> = sortedSetOf()

    @ResponseBody
    @Operation(description = "Keresés megkezdéséhez szükséges adatot várja (Szálak száma), ennek értéke minimum 1.\n" +
            "Visszatérési értékek:\n" +
            "HIBA: Művelet közben hiba lépett fel.\n" +
            "ELINDULT: Sikeres elindítás.\n" +
            "FUT: A keresés már fut.")
    @GetMapping("/primszam/startSearch")
    fun startSearch(@RequestParam @Min(1) threadNumber: Int): CalculateStarterResponseEnum? {
        return try {
            set.clear()
            return primszamComponent.searchPrimszam(set, threadNumber)
            //caseRequestServiceImpl.findDetailsByCaseNumber(caseNumber, request)
        } catch (e: Exception) {
            e.printStackTrace()
            CalculateStarterResponseEnum.HIBA
        }
    }

    @ResponseBody
    @Operation(description = "Egy minimum és egy maximum értéket vár, amik között kilistázza a prím számokat.\n" +
            "Visszatérési értékek:\n" +
            "-1: Túl nagy az intervallum\n"+
            "-2: A keresés nem végzett a megadott intervallummal\n" +
            "-3: A keresés még nem fut\n" +
            "Egyéb esetben: az intervellumon belüli prímszámok listájával tér vissza")
    @GetMapping("/primszam/getResult")
    fun getResult(@RequestParam min: Int, @RequestParam max: Int): List<Int>? {
        return try {
            return primszamComponent.getResult(set, min, max)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    @ResponseBody
    @Operation(description = "Megállítja a keresést.\n" +
            "Visszatérési értékek:\n" +
            "LEALLT: Sikeres leállítás\n" +
            "NEM_FUTOTT: A keresés nem futott\n" +
            "HIBA: Művelet közben hiba lépett fel")
    @GetMapping("/primszam/stopSearch")
    fun stopSearch(): CalculateStopperResponseEnum {
        return try {
            return primszamComponent.stopSearch()
        } catch (e: Exception) {
            e.printStackTrace()
            return CalculateStopperResponseEnum.HIBA
        }
    }

}