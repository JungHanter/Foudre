from django.db import models
from django.contrib.auth.models import User
from petPlatform.models import *


class LocationLog(models.Model):
    dog = models.ForeignKey(Dog)
    hour = models.ForeignKey(Hour)
    day = models.ForeignKey(Day)
    month = models.ForeignKey(Month)
    location = models.TextField()
    
    


