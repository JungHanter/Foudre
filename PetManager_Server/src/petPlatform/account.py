# -*- coding: utf-8 -*-

import md5
from models import *
from django.views.decorators.csrf import csrf_exempt
from django.http import HttpResponse, Http404, HttpResponseRedirect
from locache import *
    

@csrf_exempt
def register(request):
    if request.method == 'POST':
        email = request.POST['email']
        password = md5.md5(request.POST['password']).hexdigest()
        deviceType = request.POST['deviceType']
        pushKey = request.POST['pushKey']
        nickname = request.POST['nickname']
        
        try:
            user = User.objects.create(username=email, email=email, password=password, first_name=nickname)
            userProfile = UserProfile.objects.create(user=user, deviceType=deviceType, pushKey=pushKey)
            try:
                locache = Locache("127.0.0.1")
                result = locache.register_key(str(user.id))
                if result != 0:
                    s=str(user.id)+'+'+result
                    return HttpResponse(s)
            except:
                return HttpResponse('인증키 만들기 실패, 로그인 해주세요')
        except:
            try:
                user = User.objects.get(username=email)
                return HttpResponse('이미 존재하는 이메일 주소 입니다.')
            except:
                return HttpResponse('회원가입 오류')
        
        return HttpResponse('POST')
    elif request.mehtod == 'GET':
        return HttpResponse('GET')
    elif request.mehtod == 'PUT':
        return HttpResponse('PUT')
    elif request.mehtod == 'DELETE':
        return HttpResponse('DELETE')




@csrf_exempt    
def login(request):
    username=request.POST['username']
    password=md5.md5(request.POST['password']).hexdigest()
    password1=request.POST['password']
    try:
        user = User.objects.get(username=username)
        locache = Locache("127.0.0.1")
        result = locache.register_key(str(user.id))
        if result != 0:
            return HttpResponse(result)
        else:
            return HttpResponse('아이디와 비밀번호를 확인해 주세요')
    except:
        return HttpResponse('아이디와 비밀번호를 확인해 주세요')
        

@csrf_exempt
def authenticate(request):
    key = request.POST['username']
    value = request.POST['value']
    locache = Locache()
    if locache.cache_authenticate(str(key), str(value)) == True:
#        authentication success
        return HttpResponse('200')
    else:
#        authentication fail
        return HttpResponse('fail')


