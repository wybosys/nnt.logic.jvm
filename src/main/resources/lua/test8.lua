abc = {}
abc.Abc = {}

function abc.Abc:proc()
    return "abc"
end

function test8()
    local r = test.Test:proc()
    print(r:proc())
    collectgarbage("collect")
end
