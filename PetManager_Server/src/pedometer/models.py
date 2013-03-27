from django.db import models
from django.contrib.auth.models import User
from petPlatform.models import *



class StepLog(models.Model):
    dog = models.ForeignKey(Dog)
    hour = models.ForeignKey(Hour)
    day = models.ForeignKey(Day)
    month = models.ForeignKey(Month)
    steps = models.CharField(max_length=200, null=True)
    
    
    
    