package com.sohu.inputmethod.sogou

import android.support.annotation.IntDef
import kotlin.reflect.KClass

/**
 * Created by Qiao on 2019/03/25.
 */

val x = 0
var y = 1

fun isOdd(a: Int) = a % 2 != 0

data class A(var a: Int)

fun main() {
    val clazz: KClass<Int> = Int::class // 类引用 Kotlin class
    val javaClazz: Class<Int> = Int::class.java // 类引用 Java class

    val ints = intArrayOf(1, 2, 3, 4)
    var odds = ints.filter(::isOdd) // 函数引用
    val isOdd: (Int) -> Boolean = ::isOdd // 储存函数引用
    odds = ints.filter(isOdd)
    val isEmptyStringList: List<String>.() -> Boolean = List<String>::isEmpty // 成员/扩展 函数引用

    println("${::x.name} = ${::x.get()}") // "x = 0" // ::x 为属性引用 KProperty<Int> 类型
    println(A::a.get(A(3)))
}