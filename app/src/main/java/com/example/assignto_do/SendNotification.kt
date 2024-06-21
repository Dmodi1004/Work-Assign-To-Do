package com.example.assignto_do

import android.content.Context
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class SendNotification {

    private var userFcmToken: String? = null
    private var title: String? = null
    private var body: String? = null
    private var context: Context? = null

    private val postUrl = "https://fcm.googleapis.com/v1/projects/assign-todo-72cf6/messages:send"

    fun SendNotifications(userFcmToken: String?, title: String?, body: String?, context: Context?) {
        this.userFcmToken = userFcmToken
        this.title = title
        this.body = body
        this.context = context
    }

    fun SendNotifications() {
        val requestQueue = Volley.newRequestQueue(context)
        val mainObj = JSONObject()

        try {
            val messageObject = JSONObject()
            val notificationObject = JSONObject()
            notificationObject.put("title", title)
            notificationObject.put("body", body)
            messageObject.put("token", userFcmToken)
            messageObject.put("notification", notificationObject)
            mainObj.put("message", messageObject)

            val request: JsonObjectRequest = object : JsonObjectRequest(
                Method.POST, postUrl, mainObj,
                Response.Listener { response: JSONObject? -> },
                Response.ErrorListener { volleyError: VolleyError? -> }) {
                override fun getHeaders(): Map<String, String> {
                    val accessToken = AccessToken()
                    val accessKey = accessToken.getAccessToken()
                    val header: MutableMap<String, String> = HashMap()
                    header["content-type"] = "application/json"
                    header["authorization"] = "Bearer $accessKey"

                    return header
                }
            }

            requestQueue.add(request)
        } catch (e: JSONException) {
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }
    }

}