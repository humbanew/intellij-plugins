package org.angular2.library.forms.quickFixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.lang.javascript.psi.JSLiteralExpression
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.util.asSafely
import com.intellij.polySymbols.PolySymbol
import com.intellij.polySymbols.PolySymbolNameSegment
import com.intellij.polySymbols.inspections.PolySymbolsProblemQuickFixProvider
import com.intellij.polySymbols.references.PolySymbolReferenceProblem.ProblemKind
import com.intellij.polySymbols.utils.nameSegments
import org.angular2.library.forms.Angular2FormGroup
import org.angular2.library.forms.FORM_ANY_CONTROL_NAME_ATTRIBUTES
import org.angular2.library.forms.findFormGroupForGetCallParameter
import org.angular2.library.forms.findFormGroupForGetCallParameterArray
import org.angular2.library.forms.scopes.Angular2FormSymbolsScopeInAttributeValue
import org.angular2.library.forms.scopes.resolveFormSymbolForGetCallArrayLiteral

class Angular2FormsPolySymbolProblemQuickFixProvider : PolySymbolsProblemQuickFixProvider {
  override fun getQuickFixes(
    element: PsiElement,
    symbol: PolySymbol,
    segment: PolySymbolNameSegment,
    problemKind: ProblemKind,
  ): List<LocalQuickFix> {
    if (problemKind != ProblemKind.UnknownSymbol
        || (element !is XmlAttributeValue && element !is JSLiteralExpression))
      return emptyList()

    if (element is XmlAttributeValue
        && element.parent.asSafely<XmlAttribute>()?.name in FORM_ANY_CONTROL_NAME_ATTRIBUTES
    ) {
      val formGroup = Angular2FormSymbolsScopeInAttributeValue(element.parent as XmlAttribute)
        .getNearestFormGroup()
      if (formGroup != null) {
        return listOf(CreateFormGroupPropertyQuickFix(
          formGroup, element.value, (element.parent as XmlAttribute).name.removeSuffix("Name").removePrefix("form")))
      }
    }
    else if (element is JSLiteralExpression && element.isQuotedLiteral) {
      val errorPosition = segment.start
      val formGroup = if (errorPosition == 0)
        findFormGroupForGetCallParameter(element)
        ?: findFormGroupForGetCallParameterArray(element)
          ?.let { resolveFormSymbolForGetCallArrayLiteral(it, element) }
          ?.asSafely<Angular2FormGroup>()
      else {
        val prevSegment = symbol.nameSegments.find { errorPosition - 1 <= it.end }
        prevSegment?.symbols?.firstNotNullOfOrNull { it as? Angular2FormGroup }
      }
      if (formGroup != null) {
        return listOf(
          CreateFormGroupPropertyQuickFix(formGroup, segment.getName(symbol), "Control"),
          CreateFormGroupPropertyQuickFix(formGroup, segment.getName(symbol), "Group"),
          CreateFormGroupPropertyQuickFix(formGroup, segment.getName(symbol), "Array"),
        )
      }
    }
    return emptyList()
  }
}