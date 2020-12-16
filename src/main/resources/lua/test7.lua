function test7_a()
    local t = test.Test:new(1, 2)
    assert(t:proc() == "test7", "test7 proc is test7")
    -- t = nil 没有用，主要靠 gc
    t.abc = "abc"
    assert(t:proc2() == "abc", "test7 proc2 is abc")
    t:delete()
end

function test7_b()
    local t = test.Test:new(1, 2)
    assert(t:proc() == "test7", "test7 proc is test7")
    -- t = nil 没有用，主要靠 gc
end

function test7()
    test7_a()
    test7_b()
    collectgarbage("collect")
end
