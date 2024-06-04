import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class Menu(private val name: String, private val price: Double, private val cost: Double) // Меню для позиций чека
{

    fun getName():String { return name }
    fun getPrice():Double { return price }
    fun getCost():Double { return cost }

    override fun toString(): String
    {
        return "$name -> ${(price + cost) * PercentVAT / 100} : ${price + cost + ((price + cost) * PercentVAT / 100)}"
    }

    companion object
    {
        fun toMenu(value: String): Menu // преобразует из String в Menu
        {
            val consumer = value.split(":")
            var name = ""
            var price = 0.0
            var cost = 0.0
            for (i in 0..< consumer.count())
            {
                if(i == 0)
                {
                    name = consumer[i]
                }
                if(i == 1)
                {
                    price = consumer[i].toDouble()
                }
                if(i == 2)
                {
                    cost = consumer[i].toDouble()
                }
            }
            return Menu(name, price, cost)
        }
    }
}

class Order(value: Menu, private val count: Int) // атомарность заказов
{
    private val name:String = value.getName()
    private val vat:Double = ((value.getPrice() + value.getCost()) * count) * PercentVAT / 100
    private val price:Double = (value.getPrice() + value.getCost() + vat) * count

    fun getPrice(): Double {return price}
    fun getVAT(): Double {return vat}
    fun getName(): String {return name}


    override fun toString(): String
    {
        return "$name -> $vat : $price ($count)"
    }
}

open class Restaurant // класс, который хранит все поля, принадлежащие для конкретного ресторана
{
    companion object
    {
        private const val RESTAURANT:String = "ПAО Вкусные котлетки"
        private const val ADDRESS:String = "...Какой-то там"
        private val waiters:MutableList<String> = ArrayList(File(myDir,"$myData/waiters.txt").readText().split(" ")) // Список оффициантов, чья сегодня смена
        private val cooks:MutableList<String> = ArrayList(File(myDir,"$myData/cooks.txt").readText().split(" "))// Список поваров, чья сегодня смена
        private val date: String = SimpleDateFormat("dd/MM/yyyy").format(Date())
        fun getNameRestaurant(): String {return RESTAURANT}
        fun getAddressRestaurant(): String {return ADDRESS}
        fun getDate(): String {return date}
        fun getCooks(): MutableList<String> {return cooks}
        fun getWaiters(): MutableList<String> {return waiters}
    }
}

class Check(private val waiter:String, private val cook:String, private val orders: MutableList<Order>, private val number: Int, private val delivery:Boolean, private val addressClient:String = "", private val nameClient:String = ""): Restaurant() // Выдача чека
{
    private val time: String = SimpleDateFormat("hh:mm:ss").format(Date())
    private var totalPrice:Double = Int.MAX_VALUE.toDouble()

    fun getCook(): String {return cook}
    fun getWaiter(): String {return waiter}


    init
    {
        totalPrice = 0.0
        for(i in orders)
        {
            totalPrice += i.getPrice()
        }
    }

    fun toFile(sortedNeed:Int) // Шаблон распечатки чека
    {
        val sw = FileWriter(File(myDir,"$Checks/$number.txt"), false)
        sw.write("Комания: ${getNameRestaurant()}\n")
        sw.write("Адрес: ${getAddressRestaurant()}\n")
        sw.write("Дата: ${getDate()}\n")
        sw.write("Время: $time\n")
        if(delivery)
        {
            sw.write("Имя клиента: $nameClient\n")
            sw.write("Адрес клиента: $addressClient\n")
            sw.write("Доставщик: $waiter\n")
        }
        sw.write("Оффициант: $waiter\n")
        sw.write("Повар: $cook\n")

        var count = 1
        when (sortedNeed) {
            1 -> {
                orders.sortBy { it.getName() }
            }
            2 -> {
                orders.sortBy { it.getPrice() }
            }
            3 -> {
                orders.sortBy { it.getVAT() }
            }
        }
        for (i in orders) {
            sw.write("${count++}: $i\n")
        }
        sw.write("Общаяя стоимость: $totalPrice\n")
        sw.close()
    }
}