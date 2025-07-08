package com.crush.util


class StringUtil {
    fun containsChineseCharacters(str: String): String {
        var newString = str
        for (c in str.toCharArray()) {
            if (isChineseCharacter(c)) {
                newString = newString.replace(c.toString(),"",false)
            }
        }
        return newString
    }

    fun isChineseCharacter(c: Char): Boolean {
        val ub = Character.UnicodeBlock.of(c)
        return (ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub === Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B || ub === Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub === Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS || ub === Character.UnicodeBlock.GENERAL_PUNCTUATION)
    }

    fun filterChinese(input: String): String {
        val regex = "[\\p{InCJK Unified Ideographs}&&\\P{InCJK Compatibility Ideographs}]+"
        return input.replace(regex, "")
    }

//    fun filterChinese(input: String): String {
//        val charset = Charset.forName("UTF-8")
//        val decoder = charset.decoder()
//        val iterator = input.toCharArray().iterator()
//        val builder = StringBuilder()
//        while (iterator.hasNext()) {
//            val ch = iterator.next()
//            if (Character.isISOControl(ch) || !Character.isHighSurrogate(ch) && !Character.isLowSurrogate(ch)) {
//                builder.append(ch)
//            }
//        }
//        return builder.toString()
//    }
}