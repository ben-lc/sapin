package com.github.ben_lc.sapin.repository.util

/** Small DSL to handle construction of SQL where conditions. */
interface Condition {
  fun render(builder: StringBuilder, indent: String)

  fun isNotEmpty(): Boolean
}

@DslMarker annotation class WhereConditionMarker

@WhereConditionMarker
abstract class ConditionNode(val operator: String? = "AND") : Condition {
  private val children = mutableListOf<Condition>()

  private fun <T : Condition> initCondition(condition: T, init: T.() -> Unit): T {
    condition.init()
    children.add(condition)
    return condition
  }

  fun and(init: AndCondition.() -> Unit) = initCondition(AndCondition(), init)
  fun or(init: OrCondition.() -> Unit) = initCondition(OrCondition(), init)

  override fun render(builder: StringBuilder, indent: String) {

    when {
      children.isEmpty() -> return
      else ->
          for ((index, child) in children.withIndex()) {
            child.render(builder, "$indent  ")
            if (index < children.size - 1) builder.append("\n$indent$operator")
          }
    }
  }

  override fun isNotEmpty() = children.isNotEmpty() && children.all { it.isNotEmpty() }

  override fun toString(): String {
    val builder = StringBuilder()
    render(builder, "")
    return builder.toString()
  }
  operator fun String?.unaryPlus() {
    if (!this.isNullOrEmpty()) {
      children.add(Bool(this))
    }
  }
}

class AndCondition : ConditionNode("AND")

class OrCondition : ConditionNode("OR") {
  override fun render(builder: StringBuilder, indent: String) {
    if (this.isNotEmpty()) builder.append(" (")
    super.render(builder, indent)
    if (this.isNotEmpty()) builder.append(")")
  }
}

class Bool(private val content: String) : Condition {
  override fun render(builder: StringBuilder, indent: String) {
    builder.append("\n$indent$content")
  }
  override fun isNotEmpty() = content.isNotBlank()
}

class Where : ConditionNode() {
  override fun render(builder: StringBuilder, indent: String) {
    if (this.isNotEmpty()) builder.append("WHERE")
    super.render(builder, indent)
  }
}

fun where(init: Where.() -> Unit): Where = Where().apply(init)

infix fun String.unless(predicate: () -> Boolean) = if (predicate()) "" else this
