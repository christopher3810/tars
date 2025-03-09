package com.tars.app.outport.stock

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

//TODO : OPEN API SPEC 과 맞추기
interface GetStockInformationPort {

    @PostMapping("stockOpenApiUrl")
    fun getSomeStockInformation(
        @RequestBody portRequest: PortRequest
    )

    data class PortRequest(
        val stringData: String,
        val intergerData: Int
    )

}