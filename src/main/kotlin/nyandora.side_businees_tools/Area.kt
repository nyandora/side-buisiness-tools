package nyandora.side_businees_tools

import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class Area(private val areaId: String, private val areaName: String) {
    companion object {
        private const val URL_BASE = "https://www.walkerplus.com/event_list/"
        private val LINE_SEPARATOR = System.getProperty("line.separator")
    }

    fun collectEvents(yearMonth: String): String {
        val baseUrl = "${URL_BASE}${yearMonth}/${areaId}/"

        val urls = listOf<String>(baseUrl) + (2..100).toList().map { idx -> "${baseUrl}${idx}.html" }

        return urls.map { url -> collectEventsOf(url) }.joinToString("")
    }

    private fun collectEventsOf(url: String): String {
        println(url)

        val document = connect(url) ?: return ""

        val eventBlocks = document.getElementsByClass("m-mainlist-item")

        val events = ArrayList<String>()

        for (eventBlock in eventBlocks) {
            val title = eventBlock
                    .getElementsByClass("m-mainlist-item__ttl")[0]
                    .getElementsByTag("span")[0].text()

            val period = eventBlock
                    .getElementsByClass("m-mainlist-item-event__period")[0].text()
                    .replace("開催中 ", "")
                    .replace("終了間近 ", "")

            val description = eventBlock.getElementsByClass("m-mainlist-item__txt")[0].text()

            val address = eventBlock.getElementsByClass("m-mainlist-item__map")[0].text()

            val place = eventBlock.getElementsByClass("m-mainlist-item-event__place")[0].text()

            val tags = eventBlock
                    .getElementsByClass("m-mainlist-item__tagsitem")
                    .joinToString("・") { item: Element -> item.text() }

            val row = listOf<String>(tags, address, title, period, description, place).joinToString("\t")

            events.add(row)
        }

        return events.joinToString(LINE_SEPARATOR) + LINE_SEPARATOR
    }

    private fun connect(url: String): Document? {
        return connect(url, 1)
    }

    private fun connect(url: String, calledCount: Int): Document? {
        try {
            return Jsoup.connect(url).get()
        } catch (e: IOException) {
            if (e is HttpStatusException) {
                if (e.statusCode == 404) return null
            }

            val errorMsg = Date().toString() + ":${url}の読み込みでエラーが発生しました。${calledCount}回目の呼び出しです。"
            println(errorMsg)
            return connect(url, calledCount + 1)
        }
    }

    fun getHeader(): String {
        return "==================================" + LINE_SEPARATOR +
                areaName + LINE_SEPARATOR +
                "==================================" + LINE_SEPARATOR
    }
}