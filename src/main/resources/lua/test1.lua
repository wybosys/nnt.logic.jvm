function test1()
    assert(test.Test.a == "a", "global a")
    assert(test.Test.sa == "sa", "global sa")
    local t0 = test.Test:new()
    local t1 = test.Test:new()
    t0:proc(null)
    assert(t0.a == "a", "obj0 a")
    assert(t0.sa == "sa", "obj0 sa")
    t0.a = 123
    t0.b = function(self)
        return self.a
    end
    assert(t0.a == 123, "obj0 a 123")
    assert(t1.a == "a", "obj1 a a")
    test.Test.a = 456
    assert(t0.a == 123, "obj0 a 456")
    assert(t1.a == 456, "obj1 a 456")
    test.Test.sa = 123
    assert(t1.sa == 123, "obj1 sa 123")
    t0:done()
    return 0
end
