package com.example.otp.service;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.crypto.KeyGenerator;

import org.springframework.stereotype.Service;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Service
public class OtpService {

	private static final Integer EXPIRE_MINS = 3;
	private LoadingCache<String, Integer> otpCache;
	private LoadingCache<String, Integer> countCache;
	private LoadingCache<String, Boolean> validateCache;

	private OtpService() {
		super();
		otpCache = CacheBuilder.newBuilder().expireAfterWrite(EXPIRE_MINS, TimeUnit.MINUTES)
				.build(new CacheLoader<String, Integer>() {
					public Integer load(String key) {
						return 0;
					}
				});
		countCache = CacheBuilder.newBuilder().expireAfterWrite(EXPIRE_MINS, TimeUnit.MINUTES)
				.build(new CacheLoader<String, Integer>() {
					public Integer load(String key) {
						return 0;
					}
				});
		validateCache = CacheBuilder.newBuilder().build(new CacheLoader<String, Boolean>() {
			public Boolean load(String key) {
				return false;
			}
		});

	}

	public int generateOTP(String key) {
		TimeBasedOneTimePasswordGenerator totp = null;
		try {
			totp = new TimeBasedOneTimePasswordGenerator();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		final Key keys;
		{
			KeyGenerator keyGenerator = null;
			try {
				keyGenerator = KeyGenerator.getInstance(totp.getAlgorithm());
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			// SHA-1 and SHA-256 prefer 64-byte (512-bit) keys; SHA512 prefers 128-byte
			// (1024-bit) keys
			keyGenerator.init(1024);
			keys = keyGenerator.generateKey();
		}
		final Instant now = Instant.now();
		int otpnow = 0;
		try {
			otpnow = totp.generateOneTimePassword(keys, now);
			Random rand = new Random();
			int x = rand.nextInt(10);
			int y = rand.nextInt(100);
			if (String.valueOf(otpnow).length() == 5)
				otpnow = otpnow * 10 + x;
			else if (String.valueOf(otpnow).length() == 4)
				otpnow = otpnow * 100 + y;
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		otpCache.put(key, otpnow);
		countCache.put(key, 0);
		return otpnow;
	}

	public void clearOTP(String key) {
		otpCache.invalidate(key);
		countCache.invalidate(key);
	}

	public int getOtp(String key) {
		try {
			return otpCache.get(key);
		} catch (Exception e) {
			return 0;
		}
	}

	public int MisplacedOTP(String key) {
		int count = -1;
		try {
			count = countCache.get(key);
			count++;
			countCache.put(key, count);
			if (count >= 3) {
				clearOTP(key);
				return count;
			}
			return count;
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return count;
	}

	public void validateDose(String name) {
		validateCache.put(name, true);
	}

	public void validateDosefinished(String name) {
		validateCache.invalidate(name);
	}

	public Boolean getValidateCache(String key) {
		try {
			return validateCache.get(key);
		} catch (Exception e) {
			return false;
		}
	}

}
