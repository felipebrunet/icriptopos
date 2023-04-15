package cl.icripto.icriptopos.apis

import cl.icripto.icriptopos.models.PriceObject
import retrofit2.Call
import retrofit2.http.*


interface BtcPriceInterface {
    @GET("BTC")
    fun getData(): Call<PriceObject>
}