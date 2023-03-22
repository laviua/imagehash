package ua.com.lavi.imagehash.matcher

import ua.com.lavi.imagehash.HashSearchResult
import ua.com.lavi.imagehash.ImageMatcher

class HammingImageMatcher : ImageMatcher{

    override fun distance(hash1: String, hash2: String): Double {
        if (hash1.length != hash2.length) {
            throw IllegalArgumentException("Hash lengths must be equal")
        }

        var distance = 0
        for (i in hash1.indices) {
            val xorResult = hash1[i].code.xor(hash2[i].code)
            distance += Integer.bitCount(xorResult)
        }

        return distance.toDouble()
    }
    override fun findTopXMostSimilar(targetHash: String, hashes: Collection<String>, topX: Int): List<HashSearchResult> {
        // Sort hashes by their Hamming distance to the targetHash
        val sortedHashes = hashes.mapIndexed { index, candidatePHash ->
            val hammingDistance = distance(targetHash, candidatePHash)
            HashSearchResult(index, candidatePHash, hammingDistance)
        }.sortedBy { it.distance }

        // Iterate over the sortedHashes collection and directly add the search results to a list
        val topResults = mutableListOf<HashSearchResult>()
        for (searchResult in sortedHashes) {
            if (topResults.size >= topX) {
                break
            }
            topResults.add(searchResult)
        }

        return topResults
    }
}