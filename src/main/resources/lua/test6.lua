function test6()
    print("test6")
    test6_a()
    test6_b()
end

function test6_a()
    local t = test.Test:new()
    t.onend = function(self)
        print("onend")
        if self.ondone then
           print("unimplement ondone")
        end
    end
    t:play("play message")
end

function test6_b()
    local t = test.Test:new()
    t:play("play message")
end