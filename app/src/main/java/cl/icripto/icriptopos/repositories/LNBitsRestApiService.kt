package cl.icripto.icriptopos.repositories




import cl.icripto.icriptopos.apis.LNBitsRestApi
import cl.icripto.icriptopos.models.LNBitsInvoiceData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LNBitsRestApiService {
    fun getInvoice (lnbitsServer : String, invoiceKey : String, invoiceData: LNBitsInvoiceData, onResult: (LNBitsInvoiceData?) -> Unit) {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(lnbitsServer)
            .build()
            .create(LNBitsRestApi::class.java)

        retrofitBuilder.addUser(invoiceKey ,invoiceData).enqueue(
            object : Callback<LNBitsInvoiceData> {
                override fun onFailure(call: Call<LNBitsInvoiceData>, t: Throwable) {
                    onResult(null)
                }

                override fun onResponse(call: Call<LNBitsInvoiceData>, response: Response<LNBitsInvoiceData>) {
                    val invoice = response.body()
                    onResult(invoice)
                }
            }
        )
    }
}