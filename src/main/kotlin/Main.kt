package org.example

const val SIZE = 20
const val PROMPT = false

fun combinationsSum(n: Int, k: Int): List<List<Int>> {
    val result = mutableListOf<List<Int>>()

    fun backtrack(start: Int, target: Int, path: List<Int>) {
        if (target == 0 && path.size == n) {
            result.add(path)
            return
        }
        if (target < 0 || path.size > n) {
            return
        }

        for (i in 0..k) {
            backtrack(i, target - i, path + i)
        }
    }

    backtrack(0, k, emptyList())
    return result
}

fun main() {
    if (!PROMPT) {
        return loopTest()
    }

    println("Enter row chains")
    val rowChains = askChains()

    println("Enter column chains")
    val colChains = askChains()

    println("Solving...")

    println(BoardSolver(rowChains, colChains).solve())
}

fun askChains(): List<List<Int>> {
    return readln().trim()
        .split(";")
        .map { str ->
            str
                .split(" ", ",")
                .filter { it.isNotEmpty() }
                .map { it.toInt() }
        }
}

fun loopTest(delay: Long = 1500) {
    while (true) {
        val board = Board.of(SIZE, SIZE) { Piece.random() }
        val rowChains = board.rows.map { it.getChains() }
        val colChains = board.cols.map { it.getChains() }
        println("Solving...")
        println("ROWS: $rowChains")
        println("COLS: $colChains")
        val solution = BoardSolver(rowChains, colChains).solve() ?: throw Exception("Failed!")
        println("Solved it! Got:")
        println(solution)
        println("${if (board == solution) "Equal" else "Not equal"} to original solution")
        Thread.sleep(delay)
    }
}