# -*- coding: utf-8 -*-

# Create your views here.
from django.http import HttpResponse, Http404, HttpResponseRedirect
from django.template import Context
from django.shortcuts import render_to_response
from django.template import RequestContext
from django.template import loader, Context
from django.views.decorators.csrf import csrf_exempt
from django.contrib.auth import authenticate, login
from django.contrib.auth.models import User
import md5
from petPlatform.models import *
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
            s=str(user.id)+'+'+result
            return HttpResponse(result)
        else:
            return HttpResponse('아이디와 비밀번호를 확인해 주세요')
    except:
        return HttpResponse('아이디와 비밀번호를 확인해 주세요')
        

    
@csrf_exempt    
def dogRegister(request):
    userId = request.POST['userId']
    authentication = request.POST['authentication']
    locache = Locache("127.0.0.1")
    result = locache.authenticate(userId, authentication)
    if result == True:
        name = request.POST['name']
        age = request.POST['age']
        speciesId = request.POST['speciesId']
        birthday = request.POST['birthday']
        dog = Dog.objects.create(name=name, age=age, seecie=speciesId, birthday=birthday, owner=userId)
        return HttpResponse('200')
    else:
        HttpResponse('사용자 인증 실패')
        
        
        
        

