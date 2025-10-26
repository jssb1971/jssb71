package com.filmmodu

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*

class FilmmoduProvider : MainAPI() {
    override var mainUrl = "https://www.filmmodu.vip"
    override var name = "Filmmodu"
    override val supportedTypes = setOf(TvType.Movie)
    override val hasMainPage = true

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val doc = app.get(mainUrl).document
        val movies = doc.select("div.item a").map {
            val href = fixUrl(it.attr("href"))
            val title = it.selectFirst("h3")?.text() ?: "Bilinmiyor"
            val poster = it.selectFirst("img")?.attr("src")
            MovieSearchResponse(title, href, this.name, TvType.Movie, poster, null, null)
        }
        return newHomePageResponse("Filmmodu", movies)
    }

    override suspend fun load(url: String): LoadResponse {
        val doc = app.get(url).document
        val title = doc.selectFirst("h1")?.text() ?: ""
        val poster = doc.selectFirst("img")?.attr("src")
        val iframe = doc.selectFirst("iframe")?.attr("src")
        val videoUrl = iframe ?: url
        return newMovieLoadResponse(title, url, TvType.Movie, videoUrl) {
            this.posterUrl = poster
        }
    }
}
