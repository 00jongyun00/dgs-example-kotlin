package com.kakaostyle.basicdgs.handler

import com.netflix.graphql.types.errors.TypedGraphQLError
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult
import java.util.concurrent.CompletableFuture
import org.springframework.stereotype.Component

@Component
class CustomDataFetchingExceptionHandler : DataFetcherExceptionHandler {
    override fun handleException(handlerParameters: DataFetcherExceptionHandlerParameters): CompletableFuture<DataFetcherExceptionHandlerResult> {
        return if (handlerParameters.exception is RuntimeException) {
            val exception = handlerParameters.exception
            val debugInfo = HashMap<String, Any>()
            debugInfo["field"] = "error message"
            val graphqlError = TypedGraphQLError.newInternalErrorBuilder()
                .message(exception.message)
                .debugInfo(debugInfo)
                .path(handlerParameters.path)
                .build()
            val result = DataFetcherExceptionHandlerResult.newResult()
                .error(graphqlError)
                .build()
            CompletableFuture.completedFuture(result)
        } else super.handleException(handlerParameters)
    }
}