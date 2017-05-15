import random
import hashlib

def get_key():
	return ''.join(str(random.randint(0,100) % 2) for i in range(0,256))


def do_xor(s1, s2):
 return "".join([chr(ord(c1) ^ ord(c2)) for (c1,c2) in zip(s1,s2)])

def sha3(a):
	return hashlib.sha3_512(a.encode("utf-8")).hexdigest()

def cal_des1(a, b):
	return do_xor(a, b)

def check_client(username, timestamp, des1):
	return sha3(username + "getway" + timestamp) == des1

def cal_token():
	return ''.join(str(random.randint(0,100) % 2) for i in range(0,256))

def cal_esk2(sk2, token):
	return doXOR((str(token) + "" + sha3(token)), sk2)