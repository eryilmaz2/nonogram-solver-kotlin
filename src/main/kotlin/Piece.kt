package org.example

enum class Piece {
    FULL, EMPTY;

    val isFull get() = this == FULL;

    override fun toString() = when (this) {
        FULL -> "@"
        EMPTY -> "_"
    }

    companion object {
        fun random(): Piece {
            return if (Math.random() < 0.5) Piece.FULL else Piece.EMPTY
        }
    }
}

fun List<Piece>.getChains(): List<Int> {
    val chains = mutableListOf<Int>()
    var c = 0

    fun saveC() {
        if (c != 0) {
            chains.add(c)
            c = 0
        }
    }

    for (piece in this) {
        if (piece.isFull) {
            c++
        } else {
            saveC()
        }
    }

    saveC()

    return chains
}