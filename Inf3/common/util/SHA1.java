package util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

/**
 * Hashes a UTF-8 string into a SHA1
 * 
 * @author Daniel
 */
public class SHA1 {
	/**
	 * Digests the passed string
	 * 
	 * @param _str
	 *            string to hash
	 * @return hashed version of the passed string (SHA1)
	 * @throws NoSuchAlgorithmException
	 *             if the underlying system does not support SHA1
	 * @throws UnsupportedEncodingException
	 *             if the underlying system does not support UTF-8
	 */
	public String hash(String _str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.reset();
		md.update(_str.getBytes("UTF-8"));
		return byteToHex(md.digest());
	}

	private String byteToHex(final byte[] _bytes) {
		Formatter f = new Formatter();
		for (byte b : _bytes) {
			f.format("%02x", b);
		}
		String result = f.toString();
		f.close();
		return result;
	}
}
