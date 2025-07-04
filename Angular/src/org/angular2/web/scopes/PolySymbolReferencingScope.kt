package org.angular2.web.scopes

import com.intellij.model.Pointer
import com.intellij.model.Pointer.hardPointer
import com.intellij.polySymbols.PolySymbol
import com.intellij.polySymbols.PolySymbolOrigin
import com.intellij.polySymbols.PolySymbolQualifiedKind
import com.intellij.polySymbols.query.PolySymbolsListSymbolsQueryParams
import com.intellij.polySymbols.query.PolySymbolsScope
import com.intellij.polySymbols.utils.ReferencingPolySymbol
import com.intellij.util.containers.Stack

class PolySymbolReferencingScope(
  qualifiedKind: PolySymbolQualifiedKind,
  name: String,
  private val isExclusive: Boolean,
  origin: PolySymbolOrigin,
  vararg qualifiedKinds: PolySymbolQualifiedKind,
) : PolySymbolsScope {

  private val symbol = ReferencingPolySymbol.create(
    qualifiedKind, name, origin, *qualifiedKinds
  )

  override fun isExclusiveFor(qualifiedKind: PolySymbolQualifiedKind): Boolean =
    isExclusive && qualifiedKind == symbol.qualifiedKind

  override fun getSymbols(qualifiedKind: PolySymbolQualifiedKind, params: PolySymbolsListSymbolsQueryParams, scope: Stack<PolySymbolsScope>): List<PolySymbol> =
    if (qualifiedKind == symbol.qualifiedKind)
      listOf(symbol)
    else
      emptyList()

  override fun createPointer(): Pointer<out PolySymbolsScope> =
    hardPointer(this)

  override fun getModificationCount(): Long = 0

  override fun equals(other: Any?): Boolean =
    other === this ||
    other is PolySymbolReferencingScope
    && other.symbol == symbol

  override fun hashCode(): Int =
    symbol.hashCode()
}