package nyandora.side_businees_tools

import java.io.*

class EventCollector {
    companion object {
        private const val YEAR_MONTH = "202011"

        private val AREA_LIST = listOf<Area>(
                Area("ar0600", "東海"),
                Area("ar0500", "北陸"),
                Area("ar0700", "関西"),
                Area("ar0800", "中国"),
                Area("ar0900", "四国"),
                Area("ar1000", "九州")
        )
    }
    fun collectEvents(): String {
        return AREA_LIST
                .map { area -> area.getHeader() + area.collectEvents(YEAR_MONTH)}
                .joinToString(System.getProperty("line.separator"))
    }
}

fun main(args: Array<String>) {

    val events = EventCollector().collectEvents()

    try {
        PrintWriter(BufferedWriter(FileWriter(File("event_data.txt")))).use { pw -> pw.println(events) }
    } catch (e: IOException) {
        throw RuntimeException(e)
    }
}