require("test")

function main()
    print("main")

    local cls = {
        a = "a",
        sa = "sa",
    }

    local function objfree()
        print("main: objfree")
    end

    local t = {
    }
    setmetatable(t, { __index = cls, __gc = objfree })

    cls.a = 123
    assert(t.a == 123, "obj a 123")

    t = nil

    collectgarbage("collect")
    return 0
end

print("test lua main")
