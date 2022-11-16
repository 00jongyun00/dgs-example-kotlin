package com.kakaostyle.basicdgs.instrumentation

import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.execution.instrumentation.InstrumentationContext
import graphql.execution.instrumentation.InstrumentationState
import graphql.execution.instrumentation.SimpleInstrumentation
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters
import graphql.schema.DataFetcher
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import java.util.concurrent.CompletableFuture
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ExampleTracingInstrumentation : SimpleInstrumentation() {

    val logger = LoggerFactory.getLogger(this::class.simpleName)

    override fun createState(): InstrumentationState {
        return TraceState(System.currentTimeMillis())
    }

    override fun beginExecution(
        parameters: InstrumentationExecutionParameters,
        state: InstrumentationState
    ): InstrumentationContext<ExecutionResult>? {
        return super.beginExecution(parameters, state)
    }

    override fun instrumentDataFetcher(
        dataFetcher: DataFetcher<*>,
        parameters: InstrumentationFieldFetchParameters
    ): DataFetcher<*> {

        // We only care about user code
        if (parameters.isTrivialDataFetcher || parameters.executionStepInfo.path.toString().startsWith("/__schema")) {
            return dataFetcher
        }

        val dataFetcherName = findDatafetcherTag(parameters)

        return DataFetcher { environment ->
            val startTime = System.currentTimeMillis()
            val result = dataFetcher.get(environment)
            if (result is CompletableFuture<*>) {
                result.whenComplete { _, _ ->
                    val totalTime = System.currentTimeMillis() - startTime
                    logger.info("Async datafetcher '$dataFetcherName' took ${totalTime}ms")
                }
            } else {
                val totalTime = System.currentTimeMillis() - startTime
                logger.info("Datafetcher '$dataFetcherName': ${totalTime}ms")
            }

            result
        }
    }

    override fun instrumentExecutionInput(
        executionInput: ExecutionInput, parameters: InstrumentationExecutionParameters, state: InstrumentationState
    ): ExecutionInput {

        val totalTime = System.currentTimeMillis() - (state as TraceState).traceStartTime
        logger.info("Total execution time: $totalTime")
        return super.instrumentExecutionInput(executionInput, parameters, state)
    }

    private fun findDatafetcherTag(parameters: InstrumentationFieldFetchParameters): String {
        val type = parameters.executionStepInfo.parent.type
        val parentType = if (type is GraphQLNonNull) {
            type.wrappedType as GraphQLObjectType
        } else {
            type as GraphQLObjectType
        }

        return "${parentType.name}.${parameters.executionStepInfo.path.segmentName}"
    }

    data class TraceState(var traceStartTime: Long = 0) : InstrumentationState
}