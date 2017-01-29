# -*- coding: utf-8 -*-
# Generated by Django 1.10.5 on 2017-01-29 14:44
from __future__ import unicode_literals

from django.conf import settings
from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    initial = True

    dependencies = [
        migrations.swappable_dependency(settings.AUTH_USER_MODEL),
    ]

    operations = [
        migrations.CreateModel(
            name='Actor',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('bio', models.TextField(blank=True, max_length=500, null=True)),
                ('user', models.OneToOneField(on_delete=django.db.models.deletion.CASCADE, to=settings.AUTH_USER_MODEL)),
            ],
        ),
        migrations.CreateModel(
            name='Appointment',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=150)),
                ('date', models.DateTimeField()),
                ('last_changed', models.DateTimeField()),
            ],
        ),
        migrations.CreateModel(
            name='Participation',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('answer', models.CharField(choices=[('y', 'Yes'), ('n', 'No'), ('p', 'Pending')], max_length=1)),
                ('is_necessary', models.BooleanField()),
                ('actor', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='backend.Actor')),
                ('appointment', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='backend.Appointment')),
            ],
        ),
        migrations.AddField(
            model_name='appointment',
            name='participants',
            field=models.ManyToManyField(through='backend.Participation', to='backend.Actor'),
        ),
    ]
