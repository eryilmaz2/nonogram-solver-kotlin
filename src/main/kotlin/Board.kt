package org.example

data class Board<T>(val rows: List<List<T>>) {
    val height = rows.size
    val width = rows[0].size

    val cols = (0..<width).map { col -> rows.map { it[col] } }

    fun setRow(i: Int, newRow: List<T>): Board<T> {
        if (newRow.size != width) throw IllegalArgumentException(newRow.toString())

        val newRows = rows.toMutableList()
        newRows[i] = newRow
        return Board(newRows)
    }

    fun setCol(i: Int, newCol: List<T>): Board<T> {
        if (newCol.size != height) throw IllegalArgumentException(newCol.toString())

        val newRows = rows.map { it.toMutableList() }
        for (rowIndex in newCol.indices) {
            newRows[rowIndex][i] = newCol[rowIndex]
        }
        return Board(newRows)
    }

    fun setVal(row: Int, col: Int, newVal: T): Board<T> {
        val newRows = rows.map { it.toMutableList() }
        newRows[row][col] = newVal
        return Board(newRows)
    }

    override fun toString(): String {
        return rows.joinToString("\n") { it.joinToString("") }
    }

    companion object {
        fun <T> of(height: Int = 5, width: Int = 5, filler: () -> T): Board<T> {
            return Board(List(height) { List(width) { filler() } })
        }
    }
}