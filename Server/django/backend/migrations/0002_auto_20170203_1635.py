# -*- coding: utf-8 -*-
# Generated by Django 1.10.5 on 2017-02-03 15:35
from __future__ import unicode_literals

from django.db import migrations, models
import django.utils.timezone


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0001_initial'),
    ]

    operations = [
        migrations.RenameField(
            model_name='appointment',
            old_name='date',
            new_name='date_begin',
        ),
        migrations.AddField(
            model_name='appointment',
            name='date_end',
            field=models.DateTimeField(default=django.utils.timezone.now),
            preserve_default=False,
        ),
        migrations.AddField(
            model_name='appointment',
            name='description',
            field=models.TextField(blank=True),
        ),
        migrations.AddField(
            model_name='appointment',
            name='location',
            field=models.CharField(blank=True, max_length=100),
        ),
        migrations.AddField(
            model_name='appointment',
            name='notes',
            field=models.TextField(blank=True),
        ),
        migrations.AddField(
            model_name='appointment',
            name='town',
            field=models.CharField(blank=True, max_length=100),
        ),
        migrations.AlterField(
            model_name='appointment',
            name='last_changed',
            field=models.DateTimeField(auto_now=True),
        ),
    ]
