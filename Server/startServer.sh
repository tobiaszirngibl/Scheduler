#!/bin/bash

source ./env_server_3.5/bin/activate
python ./django/manage.py runserver 0.0.0.0:8000
