package org.example.core.FileSearch.algorithm

interface FuzzyMatchStrategy {
    fun match(query:String, candidate:String ):Int
}