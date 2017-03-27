from django.contrib import admin

from .models import Actor, Appointment, Group, Participation

# Register your models here.

admin.site.register(Actor)
admin.site.register(Appointment)
admin.site.register(Participation)
admin.site.register(Group)
