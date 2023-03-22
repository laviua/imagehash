package ua.com.lavi.imagehash

interface ImageMatcher {

    fun distance(hash1: String, hash2: String): Double

    fun findTopXMostSimilar(targetHash: String, hashes: Collection<String>, topX: Int): List<HashSearchResult>

}