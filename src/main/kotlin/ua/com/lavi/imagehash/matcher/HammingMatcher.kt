package ua.com.lavi.imagehash.matcher

import ua.com.lavi.imagehash.HashSearchResult
import ua.com.lavi.imagehash.ImageMatcher
import java.util.*

class HammingMatcher : ImageMatcher{

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
    override fun findTopXMostSimilar(targetHash: String, hashes: List<String>, topX: Int): List<HashSearchResult> {
        val priorityQueue = PriorityQueue<HashSearchResult>(compareBy { it.distance })

        hashes.forEachIndexed { index, candidatePHash ->
            val hammingDistance = distance(targetHash, candidatePHash)
            val searchResult = HashSearchResult(index, candidatePHash, hammingDistance)

            if (priorityQueue.size < topX) {
                priorityQueue.add(searchResult)
            } else if (hammingDistance < priorityQueue.peek().distance) {
                priorityQueue.poll()
                priorityQueue.add(searchResult)
            }
        }

        return priorityQueue.toList().sortedBy { it.distance }
    }
}