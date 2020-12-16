socket = require("socket")

function sleep(n)
    socket.select(nil, nil, n)
end

function test999()
    print("running coroutine")

    fn_produ = function()
        while true do
            sleep(1)
            local value = math.random()
            print("produce: ", value)
            coroutine.yield(value)
        end
    end

    consumer = function(produ)
        while true do
            local status, value = coroutine.resume(produ);
            print("consume: ", value)
        end
    end

    co_produ = coroutine.create(fn_produ)
    consumer(co_produ)

end
