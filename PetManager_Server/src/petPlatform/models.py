from django.db import models
from django.contrib.auth.models import User


# Create your models here.

class UserProfile(models.Model):
    user = models.OneToOneField(User, related_name='profile')
    deviceType = models.SmallIntegerField(null=False)
    pushKey = models.CharField(max_length=100, null=False, unique=True)
    friends = models.ManyToManyField(User) 
    def __unicode__(self):
        return '%s' % (self.user.username)
    def get_absolute_url(self):
        return self.user.username

class Sepcies(models.Model):
    species = models.CharField(max_length=100, null=False, unique=True)
    sizeType = models.IntegerField()

class Dog(models.Model):
    name = models.CharField(max_length=100, null=False, unique=False)
    age = models.SmallIntegerField(null=False)
    species = models.ForeignKey(Sepcies)
    birthday = models.DateField()
    owner = models.ManyToManyField(User, null=False)
    pedometer = models.BooleanField(default=False)
    gps = models.BooleanField(default=False)
    bowl = models.BooleanField(default=False)
    

class Pedometer(models.Model):
    name = models.CharField(max_length=100, null=False, unique=True)

class Gps(models.Model):
    name = models.CharField(max_length=100, null=False, unique=True)
    
class Bowl(models.Model):
    name = models.CharField(max_length=100, null=False, unique=True)
    
    
    
class Month(models.Model):
    month = models.SmallIntegerField()    

class Day(models.Model):
    day = models.SmallIntegerField()    

class Hour(models.Model):
    hour = models.SmallIntegerField()    
