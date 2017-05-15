from django.shortcuts import render
from app.models import User
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
import app.calculate as ca


@csrf_exempt
def register(request):
	key = ''
	if 'username' in request.POST and 'password' in request.POST:
		username = request.POST['username']
		password = request.POST['password']
		key = ca.get_key()

		User.objects.create(username=username, password=password, key=ca.sha3(key))
	return JsonResponse({'key':key})

@csrf_exempt
def listuser(request):
	user_list = User.objects.all()
	return JsonResponse({'foo':str(user_list)})

@csrf_exempt
def removeuser(request):
	user_list = User.objects.all().delete()
	return JsonResponse({'remove':'y'}) 


@csrf_exempt
def auth(request):
	if 'username' in request.POST and 'timestamp' in request.POST and 'esk1' in request.POST:

		username = request.POST['username']
		timestamp = request.POST['timestamp']
		esk1 = request.POST['esk1']

		user = User.objects.get(username = username)
		des1 = ca.cal_des1(esk1, user.key)


		if ca.check_client(username, timestamp, des1): # check user
			token = ca.cal_token()
			sk2 = ca.sha3(user.key)
			esk2 = ca.cal_esk2(sk2, token)
			user.key = sk2
			user.save()
			return JsonResponse({'esk':esk2})
		else:
			return JsonResponse({'auth':'f'})