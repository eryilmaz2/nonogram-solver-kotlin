package org.example

//import kotlin.time.*
//
//inline fun <T> measure(func: () -> T): T {
//    val timeSource = TimeSource.Monotonic
//    val mark = timeSource.markNow()
//    return func().also { println(timeSource.markNow() - mark) }
//}

class BoardSolver(
    val rowChains: List<List<Int>>,
    val colChains: List<List<Int>>
) {
    sealed interface CycleResult {
        data class Success(val board: Board<Piece?>) : CycleResult
        data class BoardFinished(val board: Board<Piece?>) : CycleResult
        data object Unsolvable : CycleResult
        data object Stuck : CycleResult
    }

    private fun blankBoard(): Board<Piece?> {
        return Board.of(height = rowChains.size, width = colChains.size) { null }
    }

    var cycles = 0

    tailrec fun solve(board: Board<Piece?> = blankBoard(), depth: Int = 0): Board<Piece>? {
        fun Board<Piece?>.nonNull(): Board<Piece> {
            return Board(rows.map { it.requireNoNulls() })
        }

        val cycleResult = doCycle(board)

        println("#${cycles++} [Depth $depth] Cycle was ${cycleResult.javaClass.simpleName}")

        return when (cycleResult) {
            is CycleResult.Success -> solve(cycleResult.board, depth)
            is CycleResult.BoardFinished -> cycleResult.board.nonNull()
            is CycleResult.Unsolvable -> null
            is CycleResult.Stuck -> {
                val row = board.rows.indexOfFirst { it.contains(null) }
                val col = board.rows[row].indexOf(null)

                fun set(piece: Piece) =
                    board.setVal(row, col, piece).also { println("Changing ($row, $col) to $piece") }
                solve(set(Piece.FULL), depth + 1) ?: solve(set(Piece.EMPTY), depth + 1)
            }
        }
    }

    private fun doCycle(oldBoard: Board<Piece?>): CycleResult {
        var newBoard = oldBoard

        for ((i, col) in newBoard.cols.withIndex().filter { it.value.contains(null) }) {
            val combos = cellListCombos(colChains[i], col)
            if (combos.isEmpty()) return CycleResult.Unsolvable
            newBoard = newBoard.setCol(i, definites(combos))
        }

        for ((i, row) in newBoard.rows.withIndex().filter { it.value.contains(null) }) {
            val combos = cellListCombos(rowChains[i], row)
            if (combos.isEmpty()) return CycleResult.Unsolvable
            newBoard = newBoard.setRow(i, definites(combos))
        }

        return when {
            oldBoard == newBoard -> CycleResult.Stuck
            newBoard.rows.flatten().contains(null).not() -> CycleResult.BoardFinished(newBoard)
            else -> CycleResult.Success(newBoard)
        }
    }

    private fun definites(combos: List<List<Piece?>>): List<Piece?> {
        return combos.first().mapIndexed { i, first ->
            if (combos.all { it[i] == first }) first else null
        }
    }

    val cache = hashMapOf<Pair<List<Int>, Int>, List<List<Piece>>>()

    // big mess after this point
    private fun cellListCombos(chains: List<Int>, state: List<Piece?>): List<List<Piece>> {
        val balls = state.size - chains.sum() - maxOf(chains.size, 1) + 1
        val boxes = chains.size + 1

        return cache.computeIfAbsent(Pair(chains, state.size)) {
            combinationsSum(boxes, balls).map {
                it.zip(chains + 0).flatMap { (a, b) ->
                    List(a + 1) { Piece.EMPTY } + List(b) { Piece.FULL }
                }.run { subList(1, size - 1) }
            }
        }.filter {
            it.zip(state).all { (possible, known) ->
                known == null || possible == known
            }
        }
    }

    // returns all combinations of n numbers that sum to k
    private fun combinationsSum(n: Int, k: Int): List<List<Int>> {
        val result = mutableListOf<List<Int>>()

        fun backtrack(target: Int, path: List<Int>) {
            if (target == 0 && path.size == n) {
                result.add(path)
            } else if (target >= 0 && path.size <= n) {
                for (i in 0..k) {
                    backtrack(target - i, path + i)
                }
            }
        }

        backtrack(k, emptyList())
        return result
    }

    // old code
//    private fun combos(n: Int, from: Int, to: Int): List<List<Int>> {
//        if (n <= 0) return listOf(emptyList())
//
//        return (from..<to).flatMap { i ->
//            combos(
//                n - 1,
//                i,
//                to
//            ).map { combo -> combo + i }
//        }
//    }
}