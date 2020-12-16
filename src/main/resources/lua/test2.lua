function test2()
    Test = { msg = "lua" }
    assert(_G["Test"] == Test, "global class")

    function Test:proc()
        return self.msg
    end

    function Test:sproc()
        return Test.msg
    end

    gs_test = {}
    setmetatable(gs_test, { __index = Test })
    assert(_G["gs_test"] == gs_test, "global variable")

    -- print(gs_test:proc())
    gs_test.msg = "nlua++"
    -- print(gs_test:proc())

    return 0
end
