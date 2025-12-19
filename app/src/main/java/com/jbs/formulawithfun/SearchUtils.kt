package com.jbs.formulawithfun

import java.util.Locale

object SearchUtils {
    /**
     * Normalize titles/queries for forgiving matching:
     * - lowercase
     * - remove ALL whitespace
     * - convert common superscript characters into ^N form
     */
    fun normalizeFormulaString(input: String): String {
        return input
            .lowercase(Locale.getDefault())
            .replace("\\s+".toRegex(), "")           // Remove ALL spaces
            .replace("²", "^2")
            .replace("³", "^3")
            .replace("⁴", "^4")
            .replace("⁵", "^5")
            .replace("⁶", "^6")
            .replace("⁷", "^7")
            .replace("⁸", "^8")
            .replace("⁹", "^9")
            .replace("⁰", "^0")
    }
}
