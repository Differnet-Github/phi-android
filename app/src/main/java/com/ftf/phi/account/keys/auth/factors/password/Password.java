package com.ftf.phi.account.keys.auth.factors.password;

import com.ftf.phi.ByteCallback;
import com.ftf.phi.account.keys.auth.Factor;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/* Password is an authentication method that users are most comfortable with for logging in
 *	type - What you know
 * 	explanation -
 * 		To check if a user know a password you need to have the password saved somewhere.
 * 		This becomes problematic when you have a system where anyone can do the checking themselves
 * 		instead of just a secure server.
 *
 * 		To solve that problem Password Based Key Derivation Functions(PBKDF) where invented
 *
 * 		What a pbkdf does is hash the password in a way which allows multiple iterations of the hash.
 * 		A hash is a function that is easy to compute one way but real hard to compute the otherway.
 * 		This means if I can pass 6 into the function and get a 9 out but I can't start with a 9 and
 * 		get a 6 out. The only way I could find out that 6 -> 9 is by trying 1 -> ?, 2 -> ?, etc...
 *
 * 		Most people use a password that is about 8-10 characters a-Z 0-9 and special characters.
 * 		This means there are about 72^10 or 3.743906243×10^18 possible passwords in that range.
 * 		We can agree that only about 0.0001% of those are passwords that are actually going to be used
 * 		because while sT3&6G1^a is a nice password most people wont use it because they won't remember
 * 		it. This means we have more around 3743906243 passwords possible if we are being optimistic
 * 		If a computer can run the password hash 1 time every ms then it would only take 43.3 days to
 * 		crack a users password. Those kinds of speeds might be what you get on your grandmas computer
 * 		from the 1980's but even my $200 walmart laptop can pull 200h/s. On dedicated hardware
 * 		designed to crack passwords you could have the password before your finger even left the start
 * 		key.
 *
 * 		This is where hashing algorithm's designed to be a pbkdf come in. If we chain together hashes
 * 		then instead of 1 hash being 1 password we can make it so that 10,000 hashes are one password.
 * 		This has the effect of taking exponentially longer to crack a password the more iterations we
 * 		add.
 *
 * 		The last part of the pbkdf to explain is salt. Salt is just a random value that gets added to
 * 		the end of the password. This is because there will still be the 100 most common passwords.
 * 		Every password put through will always have the same output. This means if someone used
 * 		"password" as there password it would always be the same thing. Then if you where to crack one
 * 		person using password you would crack them all. With salt there password is no longer
 * 		"password" but now its "password1ab93". The user dose'nt even need to know salt exist but it
 * 		can still be protecting them.
 *
 * 		Even with pbkdf users can still pick stupid passwords. This means that the 1000 most common
 * 		passwords must be banned. This makes it harder to crack passwords. If you are making a
 * 		password cracker you aren't going to try "aaaaaaa", "aaaaaab", "aaaaaac", etc... you will
 * 		first try "password", "qwerty", etc... If the first 1000 passwords are banned then users will
 * 		more often pick a password that isn't easy to crack.
 */

//TODO: separate out password creation and usage to allow advanced password creation
//TODO: banned password list
public class Password implements Factor {
	private static final String PASSWORD_HASH = "PBKDF2withHmacSHA256";

	// Classic password things:
	private String hash;
	private byte[] salt;
	private int iterations;
	private int keyLength;

	// Create new password
	public Password(){
		this(PASSWORD_HASH, 999999, 64, 256);
	}
	public Password(String hash, int iterations, int saltBytes, int keyLength){
		this.hash = hash;
		this.iterations = iterations;

		this.salt = new byte[saltBytes];
		SecureRandom random = new SecureRandom();
		random.nextBytes(this.salt);

		this.keyLength = keyLength;
	}

	// Import password
	public Password(JSONObject fileData) throws JSONException {
		this.hash = fileData.getString("hash");
		this.salt = fileData.getString("salt").getBytes();
		this.iterations = fileData.getInt("iterations");
		this.keyLength = fileData.getInt("keyLength");
	}

	// Get the key fro mthe password
	@Override
	public void getKey(final ByteCallback callback) throws NoSuchAlgorithmException {
		//TODO: support more hashes
		switch(this.hash){
			case "PBKDF2withHmacSHA256":
				// Run the popup for the password
				new Popup(new ByteCallback() {
					@Override
					public void call(byte[] password) {
						PBEKeySpec spec = new PBEKeySpec(password.toString().toCharArray(), salt, iterations, keyLength);
						SecretKeyFactory skf;

						try {
							skf = SecretKeyFactory.getInstance(hash);
							callback.call(skf.generateSecret(spec).getEncoded());
						} catch (NoSuchAlgorithmException e) {
							e.printStackTrace();
						} catch (InvalidKeySpecException e) {
							e.printStackTrace();
						}
					}
				});
			default:
				throw new NoSuchAlgorithmException();
		}
	}

	// Export the key as json
	@Override
	public JSONObject asJSON() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("type", "password");

		json.put("hash", this.hash);
		json.put("salt", Arrays.toString(this.salt));
		json.put("iterations", this.iterations);
		json.put("keyLength", this.keyLength);

		return json;
	}
}
