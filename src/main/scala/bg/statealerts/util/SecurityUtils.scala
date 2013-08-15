package bg.statealerts.util

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

object SecurityUtils {

    private val HmacSha1Algorithm = "HmacSHA1";

    /**
     * Calculates a HmacSHA1 value
     *
     * @param data
     * @param key
     * @return HmacSHA1
     */
    def hmac(data: String, key: String): String = {
        try {
            // get an hmac_sha1 key from the raw key bytes
            val signingKey = new SecretKeySpec(key.getBytes(), HmacSha1Algorithm);
            val mac = Mac.getInstance(HmacSha1Algorithm);
            mac.init(signingKey);

            // compute the hmac on input data bytes
            val rawHmac = mac.doFinal(data.getBytes());

            val result = new String(Hex.encodeHex(rawHmac));
            return result.toUpperCase();
        } catch {
          case ex: Exception => throw new RuntimeException("Problem with calculating hmac", ex);
        }
    }
}
