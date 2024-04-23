package bi.accounting
import io.micronaut.context.env.Environment
import io.micronaut.function.aws.MicronautRequestStreamHandler


class FunctionRequestHandler : MicronautRequestStreamHandler() {
    override fun resolveFunctionName(env: Environment): String {
        return "abi-report-functions-zoho"
    }
}