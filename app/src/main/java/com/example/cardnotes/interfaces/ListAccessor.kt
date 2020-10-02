package com.example.cardnotes.interfaces

interface ListAccessor<T> {
    fun add(item: T)
    fun remove(item: T)
    fun size(): Int
}