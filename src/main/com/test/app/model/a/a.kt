package com.test.app.model.a

import com.nnt.core.model

open class D {

}

@model()
open class C : D() {

}

@model([], C::class)
open class B : C() {

}

@model([], B::class)
class A : B() {

}