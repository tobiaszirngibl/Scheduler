# -*- coding: utf-8 -*-
# Generated by Django 1.10.5 on 2017-04-19 12:36
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0013_participation_answer_relevant'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='favorite',
            name='color',
        ),
        migrations.AddField(
            model_name='appointment',
            name='color',
            field=models.CharField(default='3c8dbc', max_length=6),
        ),
        migrations.AlterField(
            model_name='actor',
            name='avatar',
            field=models.ImageField(default='/avatars/noAvatar.jpg', upload_to='avatars/'),
        ),
    ]
