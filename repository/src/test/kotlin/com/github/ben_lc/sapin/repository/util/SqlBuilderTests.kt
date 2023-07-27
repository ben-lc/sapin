package com.github.ben_lc.sapin.repository.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SqlBuilderTests {

  @Test
  fun `Where builder should return empty string when it is empty`() {
    assertThat(where {}.toString()).isEqualTo("")
  }

  @Test
  fun `Where builder should return empty string when added conditions are empty`() {
    assertThat(where { and { +"a = b".takeUnless { true } } }.toString()).isEqualTo("")
  }
  @Test
  fun `Where builder should handle nullable conditions with takeUnless`() {
    val condition =
        where {
              and {
                +"col1 = 'x'"
                +"col2 != 'Y'".takeUnless { true }
                +"col3 = 1"
              }
            }
            .toString()
    assertThat("\n$condition")
        .isEqualTo(
            """
            WHERE
                col1 = 'x'
              AND
                col3 = 1"""
                .stripIndent())
  }

  @Test
  fun `Where builder should handle sub OR conditions`() {
    val condition =
        where {
              and {
                +"col1 = 'x'"
                +"col3 = 1"
                or {
                  +"toto"
                  +"toto"
                }
              }
            }
            .toString()
    assertThat("\n$condition")
        .isEqualTo(
            """
            WHERE
                col1 = 'x'
              AND
                col3 = 1
              AND (
                  toto
                OR
                  toto)"""
                .stripIndent())
  }

  @Test
  fun `Where builder should use AND operator as default when not specified`() {
    val condition =
        where {
              and {
                +"col1 = 'x'"
                +"col2 != 'Y'"
              }
              or {
                +"col3 = 'z'"
                +"col4 = 'p'"
              }
            }
            .toString()
    assertThat("\n$condition")
        .isEqualTo(
            """
            WHERE
                col1 = 'x'
              AND
                col2 != 'Y'
            AND (
                col3 = 'z'
              OR
                col4 = 'p')"""
                .stripIndent())

    val condition2 =
        where {
              +"col1 = 'x'"
              +"col2= 'y'"
            }
            .toString()

    assertThat("\n$condition2")
        .isEqualTo(
            """
            WHERE
              col1 = 'x'
            AND
              col2= 'y'"""
                .stripIndent())
  }
}
