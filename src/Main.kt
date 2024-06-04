import java.io.File
import java.nio.file.Paths
import kotlinx.coroutines.*
import java.io.FileWriter

val myDir = Paths.get("").toAbsolutePath().toString() // текущая директория
const val myData = "Data"
const val Checks = "Checks"
const val PercentVAT = 13
var check: Check? = null
var needSortValues: Int = 0

fun initMenu(): MutableList<Menu>
{
    val result: MutableList<Menu> = ArrayList()
    File(myDir,"$myData/Menu.txt")
        .readText()
        .split("\n")
        .forEach{ number ->
            result.add(Menu.toMenu(number))
        }

    return result
}

fun hardChoice(): Pair<String, String> // Выбор повара и оффицианта
{
    var waiter: String
    var cook: String

    while(true)
    {
        println("Выберите одного официанта, который сейчас свободен")
        var count = 1
        for(i in Restaurant.getWaiters())
        {
            println("${count++} $i")
        }

        try
        {
            val quickRead = readln()
            val dataValue = quickRead.toUInt()
            if(dataValue >= 1u && dataValue <= Restaurant.getWaiters().count().toUInt())
            {
                waiter = Restaurant.getWaiters()[(dataValue - 1u).toInt()]
                Restaurant.getWaiters().removeAt((dataValue - 1u).toInt())
                break
            }
            else
            {
                throw Exception("Хе-Хе-Хе")
            }
        }
        catch (_:Exception)
        {
            println("incorrect input".uppercase())
        }
    }

    while(true)
    {
        println("Выберите одного повара, который сейчас свободен")
        var count = 1
        for(i in Restaurant.getCooks())
        {
            println("${count++} $i")
        }

        try
        {
            val quickRead = readln()
            val dataValue = quickRead.toUInt()
            if(dataValue >= 1u && dataValue <= Restaurant.getCooks().count().toUInt())
            {
                cook = Restaurant.getCooks()[(dataValue - 1u).toInt()]
                Restaurant.getCooks().removeAt((dataValue - 1u).toInt())
                break
            }
            else
            {
                throw Exception("Хе-Хе-Хе")
            }
        }
        catch (_:Exception)
        {
            println("incorrect input".uppercase())
        }
    }

    return Pair(waiter, cook)
}

fun selfDelivery(shopping: MutableList<Order>, numberCheck:Int){// самовывоз

    val(waiter, cook) = hardChoice()
    check = Check(waiter, cook, shopping, numberCheck, false)

    while(true){
        println("Вам нужна сортировка чека? N or P or V or NOT")
        val quitRead = readln()
        when (quitRead) {
            "N" -> {
                needSortValues = 1
                break
            }
            "P" -> {
                needSortValues = 2
                break
            }
            "V" -> {
                needSortValues = 3
                break
            }
            "NOT" -> {
                needSortValues = 0
                break
            }
            else -> {
                println("incorrect input".uppercase())
            }
        }
    }
}

fun delivery(shopping: MutableList<Order>, numberCheck:Int) {// доставка

    val street: String
    val name: String
    while(true)
    {
        print("Введите свой адрес -> ")
        val quickRead = readln()
        if(checkStreet(quickRead))
        {
            street = quickRead
            break
        }
        else
        {
            println("incorrect input".uppercase())
        }
    }

    while(true)
    {
        print("Как можно к вам обращаться? -> ")
        val quickRead = readln()
        if(quickRead.isNotEmpty())
        {
            println("При встрече мы будем звать вас $quickRead")
            name = quickRead
            break
        }
        else
        {
            println("incorrect input".uppercase())
        }
    }
    val(waiter, cook) = hardChoice()
    check = Check(waiter, cook, shopping, numberCheck, true, street, name)
    while(true){
        println("Вам нужна сортировка чека? N or P or V or NOT")
        val quitRead = readln()
        when (quitRead) {
            "N" -> {
                needSortValues = 1
                break
            }
            "P" -> {
                needSortValues = 2
                break
            }
            "V" -> {
                needSortValues = 3
                break
            }
            "NOT" -> {
                needSortValues = 0
                break
            }
            else -> {
                println("incorrect input".uppercase())
            }
        }
    }
}

fun checkStreet(street:String): Boolean // Проверка существавания улицы
{
    return (street.isNotEmpty())
}

fun cancellation(shopping: MutableList<Order>)
{
    while (true) {

        println("Выберите позицию продукта:")
        var count = 1
        for (i in shopping) {
            println("${count++}: $i")
        }
        val quickRead = readln()

        if(shopping.isEmpty() || quickRead == "/Close")
        {
            return
        }

        try {
            val dataValue = quickRead.toUInt()
            if (dataValue >= 1u && dataValue <= shopping.count().toUInt()) {
                shopping.removeAt((dataValue - 1u).toInt())
            } else {
                throw Exception("Хе-Хе-Хе")
            }

        } catch (_: Exception) {
            println("incorrect input".uppercase())
        }
        println("Чтобы закончить удаление введите /Close")
    }
}

fun workersClient()
{
    val shopping: MutableList<Order> = ArrayList()
    var tempMenu:Menu
    val mainMenuRestaurant: MutableList<Menu> = initMenu() // инициализация меню ресторана
    var numberCheck = File(myDir,"$myData/Services.txt").readText().toInt()

    while (true) {

        println("Выберите позицию продукта:")
        var count = 1
        for (i in mainMenuRestaurant) {
            println("${count++}: $i")
        }

        var quickRead = readln()
        if (quickRead == "/exit") {
            return
        } else if (quickRead == "/Close") {
            if (shopping.isNotEmpty()) {
                break
            }
        } else if(quickRead == "/Cancel")
        {
            if (shopping.isNotEmpty()) {
                cancellation(shopping)
            }
        }
        else {

            try {
                var dataValue = quickRead.toUInt()
                if (dataValue >= 1u && dataValue <= mainMenuRestaurant.count().toUInt()) {
                    tempMenu = mainMenuRestaurant[(dataValue - 1u).toInt()]
                } else {
                    throw Exception("Хе-Хе-Хе")
                }
                while (true) {
                    print("Сколько единиц продукции хотите купить? -> ")
                    try {
                        quickRead = readln()
                        dataValue = quickRead.toUInt()
                        break
                    } catch (_: Exception) {
                        println("incorrect input".uppercase())
                    }
                }

                shopping.add(Order(tempMenu, dataValue.toInt()))

            } catch (_: Exception) {
                println("incorrect input".uppercase())
            }
        }
        println("Введите /Close, чтобы завершить выбор товаров")
        println("Введите /Cancel, чтобы удалить некоторые позиции")
    }

    while (true) {
        println("Самовывоз или доставка? Y - доставка, N - самовывоз")
        val quickRead = readln()
        if (quickRead == "Y") {
            delivery(shopping, numberCheck)
            numberCheck += 1

            val file = File(myDir,"$myData/Services.txt")
            val writer = FileWriter(file, false)
            writer.write(numberCheck.toString())
            writer.close()

            break
        } else if (quickRead == "N") {
            selfDelivery(shopping, numberCheck)
            numberCheck += 1

            val file = File(myDir,"$myData/Services.txt")
            val writer = FileWriter(file, false)
            writer.write(numberCheck.toString())
            writer.close()

            break
        } else {
            println("incorrect input".uppercase())
        }
    }
}

fun main(): Unit = runBlocking {

    while(true) {
        workersClient()
        launch(Dispatchers.IO) {
            val checkRead = check!!
            val sortedNeed = needSortValues
            println("Не переживайте, ваш заказ выполняется и мы вам скажем когда он будет готов)")
            delay(40000)
            checkRead.toFile(sortedNeed)
            println("Прошлый заказ был готов, просьба заберите его как можно скорее")
            Restaurant.getCooks().add(checkRead.getCook())
            Restaurant.getWaiters().add(checkRead.getWaiter())
        }
    }
}