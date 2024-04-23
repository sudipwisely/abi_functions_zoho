package bi.accounting

import jakarta.inject.Singleton
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.zip.GZIPOutputStream

@Singleton
class CompressionUtil {

    @Throws(IOException::class)
    fun compressString(inputStr: String?): String? {
        if (inputStr.isNullOrEmpty()) {
            return inputStr
        }
        val byteArrayOutputStream = ByteArrayOutputStream()
        GZIPOutputStream(byteArrayOutputStream).use { gzipOutputStream ->
            gzipOutputStream.write(
                inputStr.toByteArray(
                    StandardCharsets.UTF_8
                )
            )
        }
        return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray())
    }
}

