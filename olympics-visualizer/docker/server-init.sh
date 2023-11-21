#!/bin/bash

export SUPERSET_CONFIG_PATH=/opt/superset/superset_config.py
export FLASK_APP=superset

# Initialize Apache Superset Database
superset db upgrade

# Create admin user
superset fab create-admin --username admin --firstname Superset --lastname Admin --email admin@superset.com --password admin

# Create default roles and permissions
superset init