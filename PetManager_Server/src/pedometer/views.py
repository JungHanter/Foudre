from django.http import HttpResponse, Http404, HttpResponseRedirect
from django.template import Context
from django.shortcuts import render_to_response
from django.template import RequestContext
from django.template import loader, Context
from django.views.decorators.csrf import csrf_exempt
from django.contrib.auth import authenticate, login
from django.contrib.auth.models import User
import md5
from models import *
from locache import *





def registerStepLog(request):
    dogId = request.POST['dogId']
    steps = request.POST['steps']
    monthId = request.POST['monthId']
    dayId = request.POST['dayId']
    hourId = request.POST['hourId']
    try:
        stepLog = StepLog.objects.create(dog=dogId, month=monthId, day=dayId,hour=hourId, steps=steps)
        HttpResponse('success')
    except:
        HttpResponse('fail')
    
    