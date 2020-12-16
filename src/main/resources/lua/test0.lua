function test0()
    local t = test.Test:new()
    assert(t:proc() == "c++", "proc failed")
    --assert(t:sproc(123) == 123, "sproc failed")
    --assert(test.Test:sproc("abc") == "abc", "sproc failed")

    t = test.Test2:new()
    assert(t:proc() == "c++", "proc failed")
    --assert(t:sproc(123) == 123, "sproc failed")
    assert(test.Test.sproc("abc") == "abc", "sproc failed")

    TestAbc = { a = 1 }
    function TestAbc:proc()
        return "abc"
    end

    TestCde = { b = 2 }
    function TestCde:proc1()
        return "cde"
    end

    local t2 = test.abc.Abc:new()
    assert(t2:proc() == "abc", "sub module test failed")

    return 0
end

function test0_a()
    local t = test.Test3:new()
    -- local abc = TestAbc:new()
    assert(t:proc() == "abc", "test3 abc")
    assert(t:proc1() == "cde", "test3 cde")
    assert(t.a == 1, "test3 a 1")
end
