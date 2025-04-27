package org.example.core.FileSearch.algorithm

object LevenshteinFuzzyMatcher : FuzzyMatchStrategy  {
    override fun match(query: String, candidate: String): Int {
        return Levenshtein.distance(query.lowercase(), candidate.lowercase())
    }
}