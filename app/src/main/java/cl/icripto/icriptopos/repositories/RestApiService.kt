package cl.icripto.icriptopos.repositories



import android.util.Log
import cl.icripto.icriptopos.apis.RestApi
import cl.icripto.icriptopos.models.InvoiceData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RestApiService {
    fun getInvoice (lnbitsServer : String, invoiceKey : String, invoiceData: InvoiceData, onResult: (InvoiceData?) -> Unit) {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(lnbitsServer)
            .build()
            .create(RestApi::class.java)

        retrofitBuilder.addUser(invoiceKey ,invoiceData).enqueue(
            object : Callback<InvoiceData> {
                override fun onFailure(call: Call<InvoiceData>, t: Throwable) {
                    Log.d("holaaaa", "Fallo addUser Retrofit")
                    onResult(null)
                }

                override fun onResponse(call: Call<InvoiceData>, response: Response<InvoiceData>) {
                    Log.d("holaaaa", "Funciona adduser Retrofit")
                    val invoice = response.body()
                    onResult(invoice)
                }
            }
        )
    }
}