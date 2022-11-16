package com.kakaostyle.basicdgs.fetchers

import com.kakaostyle.basicdgs.service.ShowsService
import com.kakaostyle.types.Show
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import java.lang.RuntimeException


@DgsComponent
class ShowsDataFetcher(
    private val showsService: ShowsService
) {

    @DgsQuery
    fun shows(@InputArgument titleFilter: String?): List<Show> {
        return showsService.shows()
    }
}