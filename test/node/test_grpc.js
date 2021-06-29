const yargs = require("yargs")
const grpc = require("grpc")
const util = require("util")

const {
    TestClient,
    TestService
} = require("./dubbo/test_grpc_pb")
const {
    Empty
} = require("google-protobuf/google/protobuf/empty_pb")

function test(idx) {
    console.info(`test ${idx}`)

    let cli = new TestClient("localhost:8093", grpc.credentials.createInsecure(), null)

    // 测试hello        
    util.promisify(cli.hello).call(cli, new Empty()).then(data => {
        console.info(`收到数据 ${data.getMessage()}`)
    })

    // 测试返回错误
    cli.error(new Empty(), (err, data) => {
        console.info(`${err.code} ${err.message}`)
    })
}

if (require.parent == null) {
    const args = yargs
        .option('ncnt', {
            alias: 'n',
            default: 1,
            type: 'integer'
        })
        .help()
        .argv

    for (i = 0; i < args.ncnt; ++i) {
        test(i)
    }
}