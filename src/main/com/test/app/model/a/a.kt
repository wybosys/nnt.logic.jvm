package com.test.app.model.a

import com.nnt.core.model

class D {
    
}

@model()
open class C {

}

@model()
open class B : C() {

}

@model()
class A : B() {

}