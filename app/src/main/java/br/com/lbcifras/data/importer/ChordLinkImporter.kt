package br.com.lbcifras.data.importer

import org.jsoup.Jsoup
import org.jsoup.parser.Parser

class ChordLinkImporter {

    fun importFromUrl(url: String): Result<ImportedSong> {
        return runCatching {
            val normalizedUrl = url.trim()
            require(normalizedUrl.startsWith("http://") || normalizedUrl.startsWith("https://")) {
                "Cole um link valido com http:// ou https://"
            }

            val doc = Jsoup.connect(normalizedUrl)
                .userAgent("Mozilla/5.0 (Android) AppleWebKit/537.36 Chrome/126.0.0.0 Mobile Safari/537.36")
                .timeout(20_000)
                .get()

            when {
                normalizedUrl.contains("ultimate-guitar.com", ignoreCase = true) -> parseUltimateGuitar(doc.html())
                normalizedUrl.contains("cifraclub.com.br", ignoreCase = true) -> parseCifraClub(doc.html())
                else -> parseGeneric(doc.title(), doc.body()?.text().orEmpty())
            }
        }
    }

    private fun parseUltimateGuitar(html: String): ImportedSong {
        val raw = extractByRegex(
            html,
            "\\\"wiki_tab\\\"\\s*:\\s*\\{\\s*\\\"content\\\"\\s*:\\s*\\\"(.*?)\\\"",
            RegexOption.DOT_MATCHES_ALL
        ) ?: extractByRegex(
            html,
            "&quot;wiki_tab&quot;\\s*:\\s*\\{\\s*&quot;content&quot;\\s*:\\s*&quot;(.*?)&quot;",
            RegexOption.DOT_MATCHES_ALL
        ) ?: throw IllegalArgumentException("Nao foi possivel localizar o conteudo da cifra no link da Ultimate Guitar")

        val decoded = decodeEscapedContent(raw)
        val chordPro = decoded
            .replace(Regex("\\[/?tab\\]", RegexOption.IGNORE_CASE), "")
            .replace(Regex("\\[ch](.+?)\\[/ch]", RegexOption.IGNORE_CASE), "[$1]")
            .replace(Regex("\\r\\n|\\n"), "\n")
            .replace(Regex("\\n{3,}"), "\n\n")
            .trim()

        val (title, artist) = extractTitleArtistFromMeta(
            html = html,
            fallbackTitle = "Cifra importada",
            fallbackArtist = ""
        )

        return ImportedSong(
            title = title,
            artist = artist,
            musicalKey = extractTonality(html) ?: "C",
            chordProText = chordPro
        )
    }

    private fun parseCifraClub(html: String): ImportedSong {
        val titleMeta = extractMetaProperty(html, "og:title")
        val (title, artist) = parseCifraClubTitle(titleMeta)

        val preCandidates = Regex("<pre[^>]*>(.*?)</pre>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
            .findAll(html)
            .map { it.groupValues[1] }
            .map { Parser.unescapeEntities(it, false) }
            .map { stripHtmlTags(it) }
            .map { it.replace(Regex("\\n{3,}"), "\n\n").trim() }
            .filter { it.length > 120 }
            .toList()

        val chordPro = if (preCandidates.isNotEmpty()) {
            preCandidates.maxByOrNull { it.length }.orEmpty()
        } else {
            val description = extractMetaProperty(html, "og:description").orEmpty()
            description
                .replace(" / ", "\n")
                .replace(Regex("\\s{2,}"), " ")
                .trim()
        }

        if (chordPro.isBlank()) {
            throw IllegalArgumentException("Nao foi possivel extrair a cifra do link da Cifra Club")
        }

        return ImportedSong(
            title = title.ifBlank { "Cifra importada" },
            artist = artist,
            musicalKey = extractTonality(html) ?: "C",
            chordProText = chordPro
        )
    }

    private fun parseGeneric(title: String, bodyText: String): ImportedSong {
        val normalized = bodyText
            .replace(Regex("\\n{3,}"), "\n\n")
            .trim()

        require(normalized.length > 80) {
            "Nao foi possivel extrair uma cifra desse link"
        }

        return ImportedSong(
            title = title.ifBlank { "Cifra importada" },
            artist = "",
            musicalKey = "C",
            chordProText = normalized
        )
    }

    private fun parseCifraClubTitle(metaTitle: String?): Pair<String, String> {
        if (metaTitle.isNullOrBlank()) return "Cifra importada" to ""

        val parts = metaTitle.split(" - ").map { it.trim() }
        return when {
            parts.size >= 2 -> parts[0] to parts[1]
            else -> metaTitle to ""
        }
    }

    private fun extractTitleArtistFromMeta(
        html: String,
        fallbackTitle: String,
        fallbackArtist: String
    ): Pair<String, String> {
        val ogTitle = extractMetaProperty(html, "og:title").orEmpty()
        if (ogTitle.isBlank()) return fallbackTitle to fallbackArtist

        val cleaned = ogTitle
            .replace("(Chords)", "", ignoreCase = true)
            .replace("CHORDS", "", ignoreCase = true)
            .trim()

        val parts = cleaned.split(" - ").map { it.trim() }
        return if (parts.size >= 2) {
            parts[0] to parts[1]
        } else {
            cleaned to fallbackArtist
        }
    }

    private fun extractTonality(html: String): String? {
        val raw = extractByRegex(
            html,
            "tonality_name(?:&quot;|\\\")\\s*:\\s*(?:&quot;|\\\")([A-G][#b]?m?)(?:&quot;|\\\")"
        )?.trim()

        return raw?.takeIf { it.isNotBlank() }
    }

    private fun extractMetaProperty(html: String, property: String): String? {
        val regex = Regex(
            "<meta[^>]+property=[\\\"']$property[\\\"'][^>]+content=[\\\"'](.*?)[\\\"'][^>]*>",
            RegexOption.IGNORE_CASE
        )

        val raw = regex.find(html)?.groupValues?.getOrNull(1)
        return raw?.let { Parser.unescapeEntities(it, false) }
    }

    private fun extractByRegex(
        input: String,
        pattern: String,
        vararg options: RegexOption
    ): String? {
        return Regex(pattern, setOf(*options))
            .find(input)
            ?.groupValues
            ?.getOrNull(1)
    }

    private fun decodeEscapedContent(input: String): String {
        val unescapedHtml = Parser.unescapeEntities(input, false)
        return unescapedHtml
            .replace("\\\\/", "/")
            .replace("\\\\r", "\r")
            .replace("\\\\n", "\n")
            .replace("\\\\t", "\t")
            .replace("\\\\\"", "\"")
            .replace("\\\\'", "'")
            .replace("\\\\u0026", "&")
    }

    private fun stripHtmlTags(text: String): String {
        return text.replace(Regex("<[^>]+>"), "")
    }
}
