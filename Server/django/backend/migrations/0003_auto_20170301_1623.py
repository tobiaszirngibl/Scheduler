# -*- coding: utf-8 -*-
# Generated by Django 1.10.5 on 2017-03-01 15:23
from __future__ import unicode_literals

import datetime
from django.db import migrations, models
from django.utils.timezone import utc


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0002_auto_20170203_1635'),
    ]

    operations = [
        migrations.AlterField(
            model_name='appointment',
            name='date_begin',
            field=models.DateTimeField(blank=True, default=datetime.datetime(2017, 3, 1, 15, 23, 48, 592939, tzinfo=utc)),
        ),
        migrations.AlterField(
            model_name='appointment',
            name='date_end',
            field=models.DateTimeField(blank=True, default=datetime.datetime(2017, 3, 1, 15, 23, 48, 592980, tzinfo=utc)),
        ),
    ]
