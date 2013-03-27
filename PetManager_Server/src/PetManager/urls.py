from django.conf.urls import patterns, include, url
from petPlatform.views import *
from petFeeder.views import *
from pedometer.views import *
from gpsTracker.views import *



import settings



# Uncomment the next two lines to enable the admin:
# from django.contrib import admin
# admin.autodiscover()

urlpatterns = patterns('',
#                       Account
                       (r'^register/$', register),
                       (r'^login/$', login),
                       (r'^dogRegister/$', dogRegister),
                       
#                       Pedometer
                       (r'^registerStepLog/$', registerStepLog),
#                       GPSTracker
                       (r'^registerGpsLog/$', registerGpsLog),
#                       PetFeeder
                       

    # Examples:
    # url(r'^$', 'PetManager.views.home', name='home'),
    # url(r'^PetManager/', include('PetManager.foo.urls')),

    # Uncomment the admin/doc line below to enable admin documentation:
    # url(r'^admin/doc/', include('django.contrib.admindocs.urls')),

    # Uncomment the next line to enable the admin:
    # url(r'^admin/', include(admin.site.urls)),
)
