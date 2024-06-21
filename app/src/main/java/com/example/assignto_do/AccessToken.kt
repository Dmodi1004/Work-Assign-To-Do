package com.example.assignto_do

import android.util.Log
import com.google.auth.oauth2.GoogleCredentials
import com.google.common.collect.Lists
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets

class AccessToken {

    private val firebaseMessagingScope = "https://www.googleapis.com/auth/firebase.messaging"

    fun getAccessToken(): String? {
        try {
            val jsonString = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"assign-todo-72cf6\",\n" +
                    "  \"private_key_id\": \"a31481ff659312b7e44e5ac3947daa9768d1aa24\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCybv7xT1ooJFSm\\nMcv7EGoNMopGi/5LdAVCUAXmA4/KAPzxNjT0C0HPKAUTpJ9dOH1KzbPf1SWaCmDq\\neqbkyCpsDLO6IiH54hgnve1C2x76SUc45IcZsnA6F/uw32gbGJYmvd6B/ZUI4Piv\\n/uu0+QZlRCLScIlya40LfrNqPAGPLUPufQqKZ1CsfsP4SrgYghrTMA9XxZI9ivy/\\nmZJNv5ypIPBTiGNN82CF0TjHiKqvZG+F5amiNh/1C9FMGrVfQmwVLvTpJtdSqEuV\\nT2XopdgxdrZq+SOYge0GEimfoLIMGfFE5Mj58tUr9VnZucLj/ygWKbeON9/WPXn5\\nR6Gj3IqLAgMBAAECggEAAml8/hEZVGcowOz11SlfJH5i6JEWVLnJbLLYqZD65vtg\\n3a6+H4K3KRlhLnmVA1ny7MmWXWV1KStHLR1rMtuJj9eliy8+OQ0JmUUTyaU4g8Sj\\n9zFHF6/oU8HbqzrIwv2Uc8HDLDCJlgGj+LI7kZyAdvz7l89hf7RmEFo3VIj0ICgf\\npAEnm4QTHxkLHJRj/fWXjQoRJV80IV0d6mLLh3/nkg6geIPYHJh26FpSyVATY1KR\\nytbiZLkItf55GmiGvBab5eeJhBOgXii2SWJAwIFCJhMha6zZ/M3pDFR/BtDRNaOY\\nIEw6heDg5mqEIPwHOGOXccYBBP6c8pMO6ut8HF5nWQKBgQDZMVEbB0yMWQNK1Hdg\\nmuTAMg1++1WHXqz64HOhF0bwsQLUS2oMWJICdFp5KNnfneDiVBkSIK7RB2QuMQw+\\nKDtmIVCpyLPtOTbrnS4vpSaGLfwsHWgGFI8CF5QTbXwZof98p0KX+lhga06H0Yar\\nzixaFScXKw/jvvtH2nEQVIExeQKBgQDSUMmNhnUhkd8FqfchuaH0LMspQq5QLnI5\\n6K9eSyFyX6IIjDUHI8cvOf8OpyoZ/Aosv6uAEa4HHPNBWMuM2qAS87jQM8nzDqqQ\\nV76DQnETVhpmUkbYoqubdZxi+EBJhQ3e9ucpJRf93NQ8iP14Qy7Ylw785aArIzUi\\nAb45V7Y/IwKBgH+Y75M3eItDiCGgLPn+RcF8Jl2hfp/myS0iTAwpxq2E8KqUG5th\\nNXZnsPdZPpaXBBUzXv89YDbBZuOUQSMLM8pObDszBDGr2U4Wq5mmJrnRrkKkjAZ2\\ncYXDIGVBfxT4DUbIZOEwXFdY82vrdnjcyUWrA6oizFT6UFnoRWBYpQXRAoGAPvgd\\nQJhvXXu1UJbQ/XH9AYSf11y1oCAn5xq51vubqgjkFLcV8WtO6d7tdM+sFVeXHErO\\n6GtitZJ/aqNRXnzezI3YWc4HuxQ7ETiUO+yaunVfNh+QQrCBvOv7xM4bQWfq/UTj\\ndTg79LA3g2Bos6c50XN481M1H3VNJT9sWvLeCA0CgYBjiQIdvEtVD7MZfElQjK1u\\neENqSnWCkgJ8AoHznnnEAv4ZqrZ3GCfIr3WZWE3fJ2CyhFypBxKjS7Qf4hz4A7qZ\\n2/zixBHEKKh/bxwIva371VGFXUFGxO8E5DG8O96LnsJ848Myxm/NGqVl93gBVn1W\\nu6Sigbiy5mysvWtJC6TVXg==\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-te83h@assign-todo-72cf6.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"105726193085362092529\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-te83h%40assign-todo-72cf6.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}\n"

            val stream: InputStream =
                ByteArrayInputStream(jsonString.toByteArray(StandardCharsets.UTF_8))

            val googleCredentials = GoogleCredentials.fromStream(stream)
                .createScoped(Lists.newArrayList(firebaseMessagingScope))
            googleCredentials.refresh()

            return googleCredentials.accessToken.tokenValue

        } catch (e: IOException) {
            Log.e("TAG", "getAccessToken: " + e.message)
            return null
        }
    }

}