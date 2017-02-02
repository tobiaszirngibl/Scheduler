#!/bin/bash

virtualenv --python=python3.5 env_server_3.5/
source ./env_server_3.5/bin/activate
pip install -r ./env_server_3.5/requirements.txt
deactivate
