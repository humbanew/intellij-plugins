// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package org.jetbrains.vuejs.lang.expr.stub.impl

import com.intellij.psi.stubs.*
import com.intellij.psi.tree.IElementType
import org.jetbrains.vuejs.lang.expr.psi.VueJSEmbeddedExpressionContent
import org.jetbrains.vuejs.lang.expr.psi.impl.VueJSEmbeddedExpressionContentImpl
import org.jetbrains.vuejs.lang.expr.stub.VueJSEmbeddedExpressionContentStub
import java.io.IOException

class VueJSEmbeddedExpressionContentStubImpl(parent: StubElement<*>?, elementType: IElementType)
  : StubBase<VueJSEmbeddedExpressionContent>(parent, elementType), VueJSEmbeddedExpressionContentStub {
  override fun createPsi(): VueJSEmbeddedExpressionContent {
    return VueJSEmbeddedExpressionContentImpl(this, elementType)
  }

  override fun index(sink: IndexSink) {}

  @Throws(IOException::class)
  override fun serialize(dataStream: StubOutputStream) {
  }
}
