# -*- coding: utf-8 -*-
# Generated by Django 1.10.5 on 2017-04-06 13:20
from __future__ import unicode_literals

from django.conf import settings
from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0005_auto_20170403_1543'),
    ]

    operations = [
        migrations.AddField(
            model_name='appointment',
            name='organizer',
            field=models.ForeignKey(default=None, on_delete=django.db.models.deletion.CASCADE, related_name='owner', to=settings.AUTH_USER_MODEL),
        ),
    ]
